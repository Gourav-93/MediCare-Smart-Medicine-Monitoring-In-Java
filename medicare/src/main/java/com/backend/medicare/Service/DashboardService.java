package com.backend.medicare.Service;

import com.backend.medicare.DTO.*;
import com.backend.medicare.Model.*;
import com.backend.medicare.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final MedicineLogRepository medicineLogRepository;
    private final EmergencyAlertRepository emergencyAlertRepository;
    private final NotificationRepository notificationRepository;

    public DashboardService(UserRepository userRepository,
                            PatientRepository patientRepository,
                            CaregiverRepository caregiverRepository,
                            CaregiverPatientRepository caregiverPatientRepository,
                            MedicineRepository medicineRepository,
                            MedicineScheduleRepository medicineScheduleRepository,
                            MedicineLogRepository medicineLogRepository,
                            EmergencyAlertRepository emergencyAlertRepository,
                            NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
        this.medicineRepository = medicineRepository;
        this.medicineScheduleRepository = medicineScheduleRepository;
        this.medicineLogRepository = medicineLogRepository;
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.notificationRepository = notificationRepository;
    }

    public PatientDashboardDto getPatientDashboard(int currentUserId) {
        Patient patient = patientRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));

        List<Medicine> medicines = medicineRepository.findByPatient_IdOrderByNameAsc(patient.getId());
        int totalMedicines = medicines.size();

        LocalDate todayDate = LocalDate.now();
        LocalDateTime startOfDay = todayDate.atStartOfDay();
        LocalDateTime endOfDay = todayDate.atTime(LocalTime.MAX);

        List<MedicineSchedule> todaySchedules = medicineScheduleRepository.findTodaySchedulesForPatient(
                patient.getId(), startOfDay, endOfDay);

        List<MedicineLog> todaysLogs = medicineLogRepository.findTodayLogsForPatient(
                patient.getId(), startOfDay, endOfDay);

        List<MedicineLog> pendingMedicines = new ArrayList<>();
        for (MedicineSchedule schedule : todaySchedules) {
            LocalDateTime scheduledTime = todayDate.atTime(schedule.getTime());
            Optional<MedicineLog> existing = todaysLogs.stream()
                    .filter(l -> l.getMedicine().getId().equals(schedule.getMedicine().getId()) &&
                                 l.getScheduledTime().equals(scheduledTime))
                    .findFirst();

            if (existing.isEmpty()) {
                pendingMedicines.add(MedicineLog.builder()
                        .medicine(schedule.getMedicine())
                        .patient(patient)
                        .scheduledTime(scheduledTime)
                        .status("Pending")
                        .build());
            } else if ("Pending".equalsIgnoreCase(existing.get().getStatus())) {
                pendingMedicines.add(existing.get());
            }
        }
        pendingMedicines.sort(Comparator.comparing(MedicineLog::getScheduledTime));

        List<MedicineLog> missedMedicines = medicineLogRepository
                .findTop20ByPatient_IdAndStatusOrderByScheduledTimeDesc(patient.getId(), "Missed");

        List<MedicineLog> takenMedicines = medicineLogRepository
                .findTop20ByPatient_IdAndStatusOrderByTakenTimeDesc(patient.getId(), "Taken");

        List<MedicineLog> allLogs = medicineLogRepository.findByPatient_Id(patient.getId());
        int totalDoses = allLogs.size();
        int takenDoses = (int) allLogs.stream().filter(l -> "Taken".equalsIgnoreCase(l.getStatus())).count();
        int missedDoses = (int) allLogs.stream().filter(l -> "Missed".equalsIgnoreCase(l.getStatus())).count();
        double adherencePercentage = totalDoses == 0 ? 0 : Math.round((double) takenDoses / totalDoses * 100.0 * 100.0) / 100.0;

        List<Medicine> lowStockMedicines = medicines.stream()
                .filter(m -> m.getStock() <= 5)
                .toList();

        List<EmergencyAlert> activeEmergencies = emergencyAlertRepository
                .findByPatient_IdAndStatusOrderByCreatedAtDesc(patient.getId(), "ACTIVE");

        int unreadNotifications = (int) notificationRepository.countByUser_IdAndIsReadFalse(currentUserId);

        PatientInfoDto patientInfo = PatientInfoDto.builder()
                .id(patient.getId())
                .userId(patient.getUser().getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .dateOfBirth(patient.getDateOfBirth())
                .emergencyContact(patient.getEmergencyContact())
                .build();

        return PatientDashboardDto.builder()
                .patient(patientInfo)
                .totalMedicines(totalMedicines)
                .medicines(medicines)
                .todaySchedules(todaySchedules)
                .pendingMedicines(pendingMedicines)
                .missedMedicines(missedMedicines)
                .takenMedicines(takenMedicines)
                .totalDoses(totalDoses)
                .takenDoses(takenDoses)
                .missedDoses(missedDoses)
                .adherencePercentage(adherencePercentage)
                .lowStockMedicines(lowStockMedicines)
                .activeEmergencies(activeEmergencies)
                .unreadNotifications(unreadNotifications)
                .build();
    }

    public CaregiverDashboardDto getCaregiverDashboard(int currentUserId) {
        Caregiver caregiver = caregiverRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Caregiver profile not found."));

        List<CaregiverPatient> links = caregiverPatientRepository.findByCaregiver_Id(caregiver.getId());
        List<Patient> linkedPatients = links.stream().map(CaregiverPatient::getPatient).toList();
        List<Integer> patientIds = linkedPatients.stream().map(Patient::getId).toList();

        CaregiverInfoDto caregiverInfo = CaregiverInfoDto.builder()
                .id(caregiver.getId())
                .userId(caregiver.getUser().getId())
                .name(caregiver.getUser().getName())
                .email(caregiver.getUser().getEmail())
                .relationship(caregiver.getRelationship())
                .build();

        List<CaregiverDashboardPatientDto> patientDtos = linkedPatients.stream()
                .map(p -> CaregiverDashboardPatientDto.builder()
                        .patientId(p.getId())
                        .userId(p.getUser().getId())
                        .name(p.getUser().getName())
                        .email(p.getUser().getEmail())
                        .dateOfBirth(p.getDateOfBirth())
                        .emergencyContact(p.getEmergencyContact())
                        .build())
                .toList();

        if (patientIds.isEmpty()) {
            int unread = (int) notificationRepository.countByUser_IdAndIsReadFalse(currentUserId);
            return CaregiverDashboardDto.builder()
                    .caregiver(caregiverInfo)
                    .patients(patientDtos)
                    .unreadNotifications(unread)
                    .build();
        }

        List<EmergencyAlert> activeEmergencies = emergencyAlertRepository
                .findByPatient_IdInAndStatusOrderByCreatedAtDesc(patientIds, "ACTIVE");

        List<MedicineLog> missedMedicines = medicineLogRepository
                .findTop20ByPatient_IdInAndStatusOrderByScheduledTimeDesc(patientIds, "Missed");

        LocalDate todayDate = LocalDate.now();
        LocalDateTime startOfDay = todayDate.atStartOfDay();
        LocalDateTime endOfDay = todayDate.atTime(LocalTime.MAX);

        List<MedicineSchedule> todaySchedules = medicineScheduleRepository.findTodaySchedulesForPatients(
                patientIds, startOfDay, endOfDay);

        List<MedicineLog> todaysLogs = medicineLogRepository.findTodayLogsForPatients(
                patientIds, startOfDay, endOfDay);

        List<MedicineLog> pendingMedicines = new ArrayList<>();
        for (MedicineSchedule schedule : todaySchedules) {
            LocalDateTime scheduledTime = todayDate.atTime(schedule.getTime());
            Optional<MedicineLog> existing = todaysLogs.stream()
                    .filter(l -> l.getMedicine().getId().equals(schedule.getMedicine().getId()) &&
                                 l.getScheduledTime().equals(scheduledTime))
                    .findFirst();

            if (existing.isEmpty()) {
                pendingMedicines.add(MedicineLog.builder()
                        .medicine(schedule.getMedicine())
                        .patient(schedule.getMedicine().getPatient())
                        .scheduledTime(scheduledTime)
                        .status("Pending")
                        .build());
            } else if ("Pending".equalsIgnoreCase(existing.get().getStatus())) {
                pendingMedicines.add(existing.get());
            }
        }
        pendingMedicines.sort(Comparator.comparing(MedicineLog::getScheduledTime));
        if (pendingMedicines.size() > 20) {
            pendingMedicines = pendingMedicines.subList(0, 20);
        }

        List<MedicineLog> allLogs = medicineLogRepository.findByPatient_IdIn(patientIds);
        List<PatientAdherenceDto> adherenceList = linkedPatients.stream().map(p -> {
            List<MedicineLog> pLogs = allLogs.stream().filter(l -> l.getPatient().getId().equals(p.getId())).toList();
            int total = pLogs.size();
            int taken = (int) pLogs.stream().filter(l -> "Taken".equalsIgnoreCase(l.getStatus())).count();
            int missed = (int) pLogs.stream().filter(l -> "Missed".equalsIgnoreCase(l.getStatus())).count();
            double pct = total == 0 ? 0 : Math.round((double) taken / total * 100.0 * 100.0) / 100.0;

            return PatientAdherenceDto.builder()
                    .patientId(p.getId())
                    .patientName(p.getUser().getName())
                    .totalDoses(total)
                    .takenDoses(taken)
                    .missedDoses(missed)
                    .adherencePercentage(pct)
                    .build();
        }).toList();

        int unreadNotifications = (int) notificationRepository.countByUser_IdAndIsReadFalse(currentUserId);

        return CaregiverDashboardDto.builder()
                .caregiver(caregiverInfo)
                .patients(patientDtos)
                .activeEmergencies(activeEmergencies)
                .missedMedicines(missedMedicines)
                .todaySchedules(todaySchedules)
                .pendingMedicines(pendingMedicines)
                .adherence(adherenceList)
                .unreadNotifications(unreadNotifications)
                .build();
    }

    public AdminDashboardDto getAdminDashboard() {
        int totalUsers = (int) userRepository.count();
        int totalPatients = (int) patientRepository.count();
        int totalCaregivers = (int) caregiverRepository.count();
        int totalMedicines = (int) medicineRepository.count();
        int totalMedicineLogs = (int) medicineLogRepository.count();
        int totalTakenDoses = (int) medicineLogRepository.countByStatusIgnoreCase("Taken");
        int totalMissedDoses = (int) medicineLogRepository.countByStatusIgnoreCase("Missed");
        int totalPendingDoses = (int) medicineLogRepository.countByStatusIgnoreCase("Pending");

        int completedDoses = totalTakenDoses + totalMissedDoses;
        double overallAdherencePercentage = completedDoses == 0 ? 0 :
                Math.round((double) totalTakenDoses / completedDoses * 100.0 * 100.0) / 100.0;

        int activeEmergencyAlerts = (int) emergencyAlertRepository.countByStatus("ACTIVE");
        int totalEmergencyAlerts = (int) emergencyAlertRepository.count();
        int unreadNotifications = (int) notificationRepository.countByIsReadFalse();
        int caregiverPatientRelationships = (int) caregiverPatientRepository.count();

        List<Medicine> lowStockMedicines = medicineRepository.findByStockLessThanEqualOrderByStockAsc(5);
        List<EmergencyAlert> activeEmergencies = emergencyAlertRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .totalPatients(totalPatients)
                .totalCaregivers(totalCaregivers)
                .totalMedicines(totalMedicines)
                .totalMedicineLogs(totalMedicineLogs)
                .totalTakenDoses(totalTakenDoses)
                .totalMissedDoses(totalMissedDoses)
                .totalPendingDoses(totalPendingDoses)
                .overallAdherencePercentage(overallAdherencePercentage)
                .activeEmergencyAlerts(activeEmergencyAlerts)
                .totalEmergencyAlerts(totalEmergencyAlerts)
                .unreadNotifications(unreadNotifications)
                .caregiverPatientRelationships(caregiverPatientRelationships)
                .lowStockMedicines(lowStockMedicines)
                .activeEmergencies(activeEmergencies)
                .build();
    }
}
