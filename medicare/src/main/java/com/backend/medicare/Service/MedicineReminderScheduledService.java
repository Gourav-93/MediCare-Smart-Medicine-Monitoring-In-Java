package com.backend.medicare.Service;

import com.backend.medicare.Model.*;
import com.backend.medicare.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Equivalent Java background service for MedicineReminderBackgroundService.cs.
 * Runs on a 15-second loop, generates pending logs, dispatches reminder, missed, and caregiver alert emails,
 * and maintains idempotency in EmailDeliveryLogs to prevent duplicate dispatch across backend instances.
 */
@Service
public class MedicineReminderScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(MedicineReminderScheduledService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    private final MedicineRepository medicineRepository;
    private final MedicineLogRepository medicineLogRepository;
    private final EmailDeliveryLogRepository emailDeliveryLogRepository;
    private final NotificationRepository notificationRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;
    private final EmailService emailService;

    @Value("${medicare.reminder.enabled:true}")
    private boolean reminderEnabled;

    public MedicineReminderScheduledService(MedicineRepository medicineRepository,
                                            MedicineLogRepository medicineLogRepository,
                                            EmailDeliveryLogRepository emailDeliveryLogRepository,
                                            NotificationRepository notificationRepository,
                                            CaregiverPatientRepository caregiverPatientRepository,
                                            EmailService emailService) {
        this.medicineRepository = medicineRepository;
        this.medicineLogRepository = medicineLogRepository;
        this.emailDeliveryLogRepository = emailDeliveryLogRepository;
        this.notificationRepository = notificationRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelayString = "${medicare.reminder.interval-ms:15000}")
    @Transactional
    public void executeReminderCycle() {
        if (!reminderEnabled) {
            return;
        }

        try {
            checkMedicineSchedules();
        } catch (Exception ex) {
            logger.error("Error while checking medicine schedules: {}", ex.getMessage(), ex);
        }
    }

    private void checkMedicineSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        List<Medicine> medicines = medicineRepository.findAll();

        for (Medicine medicine : medicines) {
            if (medicine.getStartDate() == null || medicine.getEndDate() == null) {
                continue;
            }

            LocalDate start = medicine.getStartDate().toLocalDate();
            LocalDate end = medicine.getEndDate().toLocalDate();

            if (start.isAfter(today) || end.isBefore(today)) {
                continue;
            }

            for (MedicineSchedule schedule : medicine.getMedicineSchedules()) {
                if (!"Daily".equalsIgnoreCase(schedule.getFrequency())) {
                    continue;
                }

                LocalDateTime scheduledTime = today.atTime(schedule.getTime());

                Optional<MedicineLog> logOpt = medicineLogRepository
                        .findByMedicine_IdAndScheduledTime(medicine.getId(), scheduledTime);

                MedicineLog log = logOpt.orElse(null);

                // 1. Reminder (2 minutes before scheduled time)
                if (now.isAfter(scheduledTime.minusMinutes(2)) || now.isEqual(scheduledTime.minusMinutes(2))) {
                    if (log == null) {
                        log = MedicineLog.builder()
                                .medicine(medicine)
                                .patient(medicine.getPatient())
                                .scheduledTime(scheduledTime)
                                .status("Pending")
                                .build();
                        log = medicineLogRepository.save(log);
                    }

                    trySendReminderEmail(schedule, medicine, scheduledTime);
                }

                // 2. Check Missed (5 minutes grace period)
                if (log != null && "Pending".equalsIgnoreCase(log.getStatus()) &&
                        (now.isAfter(scheduledTime.plusMinutes(5)) || now.isEqual(scheduledTime.plusMinutes(5)))) {
                    log.setStatus("Missed");
                    medicineLogRepository.save(log);

                    trySendMissedEmail(schedule, medicine, scheduledTime);
                }

                // 3. Check Caregiver Alert (30 minutes grace period)
                if (log != null && "Missed".equalsIgnoreCase(log.getStatus()) &&
                        (now.isAfter(scheduledTime.plusMinutes(30)) || now.isEqual(scheduledTime.plusMinutes(30)))) {
                    trySendCaregiverAlert(schedule, medicine, scheduledTime);
                }
            }
        }
    }

    private void trySendReminderEmail(MedicineSchedule schedule, Medicine medicine, LocalDateTime scheduledTime) {
        if (medicine.getPatient() == null || medicine.getPatient().getUser() == null) return;
        String recipientEmail = medicine.getPatient().getUser().getEmail();
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) return;

        Optional<EmailDeliveryLog> deliveryLogOpt = emailDeliveryLogRepository
                .findByScheduleAndOccurrenceAndTypeAndEmail(
                        schedule.getId(), scheduledTime, "Reminder", recipientEmail);

        if (deliveryLogOpt.isPresent()) {
            EmailDeliveryLog dl = deliveryLogOpt.get();
            if ("Sent".equalsIgnoreCase(dl.getStatus()) || dl.getRetryCount() >= 3) {
                return;
            }
        }

        EmailDeliveryLog deliveryLog = deliveryLogOpt.orElseGet(() -> {
            EmailDeliveryLog newLog = EmailDeliveryLog.builder()
                    .medicineSchedule(schedule)
                    .scheduledOccurrence(scheduledTime)
                    .notificationType("Reminder")
                    .recipientEmail(recipientEmail)
                    .status("Pending")
                    .build();

            Notification patientNotification = Notification.builder()
                    .user(medicine.getPatient().getUser())
                    .title("Medicine Reminder")
                    .message("Time to take " + medicine.getName() + ".")
                    .type("MedicineReminder")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(patientNotification);

            return emailDeliveryLogRepository.save(newLog);
        });

        try {
            String subject = "MediCare - Upcoming Medicine Reminder";
            String body = "Hello " + medicine.getPatient().getUser().getName() + ",\n\n" +
                    "This is a reminder from MediCare that it is almost time to take your medicine.\n" +
                    "Medicine: " + medicine.getName() + "\n" +
                    "Scheduled Time: " + scheduledTime.format(TIME_FORMATTER) + "\n" +
                    "Dosage: " + (medicine.getDosage() != null && !medicine.getDosage().isEmpty() ? medicine.getDosage() : "N/A") + "\n" +
                    "Please take your medicine at the scheduled time and record your medicine intake in MediCare.\n" +
                    "Stay healthy!\n\n" +
                    "Regards,\nMediCare Team";

            emailService.sendEmail(recipientEmail, subject, body);
            deliveryLog.setStatus("Sent");
            deliveryLog.setSentAt(LocalDateTime.now());
        } catch (Exception ex) {
            logger.error("Failed to send reminder email to patient {}: {}", recipientEmail, ex.getMessage());
            deliveryLog.setStatus("Failed");
            deliveryLog.setErrorMessage(ex.getMessage());
            deliveryLog.setRetryCount(deliveryLog.getRetryCount() + 1);
        } finally {
            emailDeliveryLogRepository.save(deliveryLog);
        }
    }

    private void trySendMissedEmail(MedicineSchedule schedule, Medicine medicine, LocalDateTime scheduledTime) {
        if (medicine.getPatient() == null || medicine.getPatient().getUser() == null) return;
        String recipientEmail = medicine.getPatient().getUser().getEmail();
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) return;

        Optional<EmailDeliveryLog> deliveryLogOpt = emailDeliveryLogRepository
                .findByScheduleAndOccurrenceAndTypeAndEmail(
                        schedule.getId(), scheduledTime, "Missed", recipientEmail);

        if (deliveryLogOpt.isPresent()) {
            EmailDeliveryLog dl = deliveryLogOpt.get();
            if ("Sent".equalsIgnoreCase(dl.getStatus()) || dl.getRetryCount() >= 3) {
                return;
            }
        }

        EmailDeliveryLog deliveryLog = deliveryLogOpt.orElseGet(() -> {
            EmailDeliveryLog newLog = EmailDeliveryLog.builder()
                    .medicineSchedule(schedule)
                    .scheduledOccurrence(scheduledTime)
                    .notificationType("Missed")
                    .recipientEmail(recipientEmail)
                    .status("Pending")
                    .build();

            Notification patientNotification = Notification.builder()
                    .user(medicine.getPatient().getUser())
                    .title("Medicine Missed")
                    .message("You missed your " + medicine.getName() + " dose.")
                    .type("MedicineMissed")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(patientNotification);

            return emailDeliveryLogRepository.save(newLog);
        });

        try {
            String subject = "MediCare - Missed Medicine Alert";
            String body = "Hello " + medicine.getPatient().getUser().getName() + ",\n\n" +
                    "Our MediCare system noticed that you have not recorded taking the following scheduled medicine.\n" +
                    "Medicine: " + medicine.getName() + "\n" +
                    "Scheduled Time: " + scheduledTime.format(TIME_FORMATTER) + "\n" +
                    "Status: Missed\n" +
                    "If you have already taken this medicine, please update your medicine record in MediCare.\n" +
                    "Please follow your healthcare professional's instructions.\n\n" +
                    "Regards,\nMediCare Team";

            emailService.sendEmail(recipientEmail, subject, body);
            deliveryLog.setStatus("Sent");
            deliveryLog.setSentAt(LocalDateTime.now());
        } catch (Exception ex) {
            logger.error("Failed to send missed medicine email to patient {}: {}", recipientEmail, ex.getMessage());
            deliveryLog.setStatus("Failed");
            deliveryLog.setErrorMessage(ex.getMessage());
            deliveryLog.setRetryCount(deliveryLog.getRetryCount() + 1);
        } finally {
            emailDeliveryLogRepository.save(deliveryLog);
        }
    }

    private void trySendCaregiverAlert(MedicineSchedule schedule, Medicine medicine, LocalDateTime scheduledTime) {
        if (medicine.getPatient() == null) return;
        List<CaregiverPatient> caregivers = caregiverPatientRepository.findByPatient_Id(medicine.getPatient().getId());

        for (CaregiverPatient cp : caregivers) {
            Caregiver caregiver = cp.getCaregiver();
            if (caregiver == null || caregiver.getUser() == null) continue;
            String caregiverEmail = caregiver.getUser().getEmail();
            if (caregiverEmail == null || caregiverEmail.trim().isEmpty()) continue;

            Optional<EmailDeliveryLog> deliveryLogOpt = emailDeliveryLogRepository
                    .findByScheduleAndOccurrenceAndTypeAndEmail(
                            schedule.getId(), scheduledTime, "CaregiverAlert30Min", caregiverEmail);

            if (deliveryLogOpt.isPresent()) {
                EmailDeliveryLog dl = deliveryLogOpt.get();
                if ("Sent".equalsIgnoreCase(dl.getStatus()) || dl.getRetryCount() >= 3) {
                    continue;
                }
            }

            EmailDeliveryLog deliveryLog = deliveryLogOpt.orElseGet(() -> {
                EmailDeliveryLog newLog = EmailDeliveryLog.builder()
                        .medicineSchedule(schedule)
                        .scheduledOccurrence(scheduledTime)
                        .notificationType("CaregiverAlert30Min")
                        .recipientEmail(caregiverEmail)
                        .status("Pending")
                        .build();

                Notification caregiverNotification = Notification.builder()
                        .user(caregiver.getUser())
                        .title("Patient Medicine Alert")
                        .message("The patient " + medicine.getPatient().getUser().getName() + " missed " + medicine.getName() + " for 30 minutes.")
                        .type("CaregiverMedicineAlert30Min")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(caregiverNotification);

                return emailDeliveryLogRepository.save(newLog);
            });

            try {
                String subject = "MediCare - Patient Medicine Alert";
                String body = "Hello " + caregiver.getUser().getName() + ",\n\n" +
                        "This is an important medicine alert from MediCare.\n\n" +
                        "The following patient has not recorded taking their scheduled medicine within 30 minutes of the scheduled time.\n\n" +
                        "Patient: " + medicine.getPatient().getUser().getName() + "\n" +
                        "Medicine: " + medicine.getName() + "\n" +
                        "Scheduled Time: " + scheduledTime.format(TIME_FORMATTER) + "\n" +
                        "Status: Missed\n\n" +
                        "Please contact the patient to check whether they have taken the medicine.\n" +
                        "If the patient has already taken it, please ensure their medicine intake record is updated in MediCare.\n\n" +
                        "Regards,\nMediCare Team";

                emailService.sendEmail(caregiverEmail, subject, body);
                deliveryLog.setStatus("Sent");
                deliveryLog.setSentAt(LocalDateTime.now());
            } catch (Exception ex) {
                logger.error("Failed to send 30-min caregiver alert to {}: {}", caregiverEmail, ex.getMessage());
                deliveryLog.setStatus("Failed");
                deliveryLog.setErrorMessage(ex.getMessage());
                deliveryLog.setRetryCount(deliveryLog.getRetryCount() + 1);
            } finally {
                emailDeliveryLogRepository.save(deliveryLog);
            }
        }
    }
}
