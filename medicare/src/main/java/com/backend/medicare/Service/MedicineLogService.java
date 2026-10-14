package com.backend.medicare.Service;

import com.backend.medicare.DTO.MedicineLogDto;
import com.backend.medicare.Model.*;
import com.backend.medicare.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineLogService {

    private final MedicineLogRepository logRepository;
    private final MedicineRepository medicineRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;
    private final NotificationRepository notificationRepository;

    public MedicineLogService(MedicineLogRepository logRepository,
                              MedicineRepository medicineRepository,
                              PatientRepository patientRepository,
                              CaregiverRepository caregiverRepository,
                              CaregiverPatientRepository caregiverPatientRepository,
                              NotificationRepository notificationRepository) {
        this.logRepository = logRepository;
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<MedicineLog> getAllForUser(int currentUserId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return logRepository.findAll();
        }

        if ("PATIENT".equalsIgnoreCase(role)) {
            Optional<Patient> patient = patientRepository.findByUser_Id(currentUserId);
            return patient.map(value -> logRepository.findByPatient_Id(value.getId())).orElse(Collections.emptyList());
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty()) return Collections.emptyList();

            List<CaregiverPatient> links = caregiverPatientRepository.findByCaregiver_Id(caregiver.get().getId());
            List<Integer> patientIds = links.stream().map(CaregiverPatient::getPatientId).toList();
            if (patientIds.isEmpty()) return Collections.emptyList();

            return logRepository.findByPatient_IdIn(patientIds);
        }

        return Collections.emptyList();
    }

    public Optional<MedicineLog> getById(int id) {
        return logRepository.findById(id);
    }

    @Transactional
    public MedicineLog add(MedicineLogDto dto) {
        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found."));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        if (!medicine.getPatient().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("Medicine does not belong to the specified patient.");
        }

        MedicineLog log = MedicineLog.builder()
                .medicine(medicine)
                .patient(patient)
                .scheduledTime(dto.getScheduledTime())
                .status(dto.getStatus() != null && !dto.getStatus().isEmpty() ? dto.getStatus() : "Pending")
                .build();

        return logRepository.save(log);
    }

    @Transactional
    public MedicineLog markAsTaken(int id, int currentUserId) {
        MedicineLog log = logRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine log not found."));

        if (log.getPatient() == null || log.getPatient().getUser() == null ||
            !log.getPatient().getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Patient can mark only their own medicine as taken.");
        }

        if ("Taken".equalsIgnoreCase(log.getStatus())) {
            return log;
        }

        log.setStatus("Taken");
        log.setTakenTime(LocalDateTime.now());

        Medicine medicine = log.getMedicine();
        if (medicine != null) {
            int previousStock = medicine.getStock();
            if (medicine.getStock() >= medicine.getDose()) {
                medicine.setStock(medicine.getStock() - medicine.getDose());
            } else {
                medicine.setStock(0);
            }
            medicineRepository.save(medicine);

            // Low stock alert triggered when transitioning to <= 5
            if (previousStock > 5 && medicine.getStock() <= 5) {
                Notification notification = Notification.builder()
                        .user(medicine.getPatient().getUser())
                        .title("Low Medicine Stock")
                        .message(medicine.getName() + " stock is low. Please refill the medicine.")
                        .type("Low Stock")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(notification);
            }
        }

        return logRepository.save(log);
    }
}
