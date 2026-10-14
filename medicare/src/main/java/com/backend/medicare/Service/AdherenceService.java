package com.backend.medicare.Service;

import com.backend.medicare.DTO.AdherenceDto;
import com.backend.medicare.Model.Caregiver;
import com.backend.medicare.Model.MedicineLog;
import com.backend.medicare.Repository.CaregiverPatientRepository;
import com.backend.medicare.Repository.CaregiverRepository;
import com.backend.medicare.Repository.MedicineLogRepository;
import com.backend.medicare.Repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdherenceService {

    private final MedicineLogRepository medicineLogRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;

    public AdherenceService(MedicineLogRepository medicineLogRepository,
                            PatientRepository patientRepository,
                            CaregiverRepository caregiverRepository,
                            CaregiverPatientRepository caregiverPatientRepository) {
        this.medicineLogRepository = medicineLogRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
    }

    public AdherenceDto getPatientAdherence(int patientId, int currentUserId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            if (!patientRepository.existsById(patientId)) {
                throw new IllegalArgumentException("Patient not found.");
            }
        } else if ("PATIENT".equalsIgnoreCase(role)) {
            if (!patientRepository.existsByIdAndUser_Id(patientId, currentUserId)) {
                throw new SecurityException("Unauthorized to view this patient's adherence.");
            }
        } else if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty() || !caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(caregiver.get().getId(), patientId)) {
                throw new SecurityException("Unauthorized to view this patient's adherence.");
            }
        } else {
            throw new SecurityException("Unauthorized.");
        }

        int takenDoses = (int) medicineLogRepository.countByPatient_IdAndStatusIgnoreCase(patientId, "Taken");
        int missedDoses = (int) medicineLogRepository.countByPatient_IdAndStatusIgnoreCase(patientId, "Missed");
        int pendingDoses = (int) medicineLogRepository.countByPatient_IdAndStatusIgnoreCase(patientId, "Pending");
        int totalDoses = takenDoses + missedDoses + pendingDoses;

        double adherencePercentage = totalDoses == 0 ? 0 : Math.round((double) takenDoses / totalDoses * 100.0 * 100.0) / 100.0;

        return AdherenceDto.builder()
                .patientId(patientId)
                .totalDoses(totalDoses)
                .takenDoses(takenDoses)
                .missedDoses(missedDoses)
                .adherencePercentage(adherencePercentage)
                .build();
    }
}
