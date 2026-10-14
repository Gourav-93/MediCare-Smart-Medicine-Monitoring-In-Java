package com.backend.medicare.Service;

import com.backend.medicare.DTO.EmergencyAlertDto;
import com.backend.medicare.Model.*;
import com.backend.medicare.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmergencyAlertService {

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public EmergencyAlertService(EmergencyAlertRepository emergencyAlertRepository,
                                 PatientRepository patientRepository,
                                 CaregiverRepository caregiverRepository,
                                 CaregiverPatientRepository caregiverPatientRepository,
                                 NotificationRepository notificationRepository,
                                 EmailService emailService) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Map<String, Object> createEmergency(EmergencyAlertDto dto, int currentUserId) {
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Emergency message is required.");
        }

        Patient patient = patientRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));

        EmergencyAlert alert = EmergencyAlert.builder()
                .patient(patient)
                .message(dto.getMessage().trim())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        alert = emergencyAlertRepository.save(alert);

        List<CaregiverPatient> links = caregiverPatientRepository.findByPatient_Id(patient.getId());
        int caregiversNotified = 0;

        for (CaregiverPatient cp : links) {
            Caregiver caregiver = cp.getCaregiver();
            if (caregiver == null || caregiver.getUser() == null) continue;

            User cgUser = caregiver.getUser();

            // In-app Notification
            Notification notification = Notification.builder()
                    .user(cgUser)
                    .title("🚨 Emergency Alert")
                    .message("Emergency alert from patient " + patient.getUser().getName() + ": " + dto.getMessage().trim())
                    .type("EmergencyAlert")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            // Email Notification
            if (cgUser.getEmail() != null && !cgUser.getEmail().trim().isEmpty()) {
                String subject = "🚨 Emergency Alert - MediCare";
                String body = "Hello " + cgUser.getName() + ",\n\n" +
                        "An emergency alert has been triggered by patient " + patient.getUser().getName() + ".\n\n" +
                        "Message: " + dto.getMessage().trim() + "\n\n" +
                        "Please check on the patient immediately.\n\n" +
                        "Regards,\nMediCare";
                emailService.sendEmail(cgUser.getEmail(), subject, body);
            }
            caregiversNotified++;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Emergency alert created successfully.");
        response.put("emergencyId", alert.getId());
        response.put("status", alert.getStatus());
        response.put("caregiversNotified", caregiversNotified);
        return response;
    }

    public List<EmergencyAlert> getEmergencies(int currentUserId, String role) {
        if ("PATIENT".equalsIgnoreCase(role)) {
            Optional<Patient> patient = patientRepository.findByUser_Id(currentUserId);
            return patient.map(value -> emergencyAlertRepository.findByPatient_IdOrderByCreatedAtDesc(value.getId()))
                    .orElse(Collections.emptyList());
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty()) return Collections.emptyList();

            List<CaregiverPatient> links = caregiverPatientRepository.findByCaregiver_Id(caregiver.get().getId());
            List<Integer> patientIds = links.stream().map(CaregiverPatient::getPatientId).toList();
            if (patientIds.isEmpty()) return Collections.emptyList();

            return emergencyAlertRepository.findByPatient_IdInOrderByCreatedAtDesc(patientIds);
        }

        return emergencyAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Map<String, Object> resolveEmergency(int id, int currentUserId, String role) {
        EmergencyAlert alert = emergencyAlertRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Emergency alert not found."));

        if ("PATIENT".equalsIgnoreCase(role)) {
            if (!alert.getPatient().getUser().getId().equals(currentUserId)) {
                throw new SecurityException("Unauthorized to resolve this emergency.");
            }
        } else if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty() || !caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(caregiver.get().getId(), alert.getPatient().getId())) {
                throw new SecurityException("Unauthorized to resolve this emergency.");
            }
        }

        if ("RESOLVED".equalsIgnoreCase(alert.getStatus())) {
            throw new IllegalArgumentException("Emergency is already resolved.");
        }

        alert.setStatus("RESOLVED");
        alert.setResolvedAt(LocalDateTime.now());
        emergencyAlertRepository.save(alert);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Emergency resolved successfully.");
        response.put("emergencyId", alert.getId());
        response.put("status", alert.getStatus());
        response.put("resolvedAt", alert.getResolvedAt());
        return response;
    }
}
