package com.backend.medicare.Service;

import com.backend.medicare.DTO.CaregiverPatientDto;
import com.backend.medicare.Model.Caregiver;
import com.backend.medicare.Model.CaregiverPatient;
import com.backend.medicare.Model.Patient;
import com.backend.medicare.Model.User;
import com.backend.medicare.Repository.CaregiverPatientRepository;
import com.backend.medicare.Repository.CaregiverRepository;
import com.backend.medicare.Repository.PatientRepository;
import com.backend.medicare.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CaregiverPatientService {

    private final CaregiverPatientRepository caregiverPatientRepository;
    private final CaregiverRepository caregiverRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public CaregiverPatientService(CaregiverPatientRepository caregiverPatientRepository,
                                   CaregiverRepository caregiverRepository,
                                   PatientRepository patientRepository,
                                   UserRepository userRepository) {
        this.caregiverPatientRepository = caregiverPatientRepository;
        this.caregiverRepository = caregiverRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CaregiverPatient linkPatient(CaregiverPatientDto dto, int currentUserId, String role) {
        if (dto.getCaregiverId() <= 0) {
            throw new IllegalArgumentException("Valid caregiverId is required.");
        }

        if (dto.getPatientId() <= 0 && (dto.getPatientEmail() == null || dto.getPatientEmail().trim().isEmpty())) {
            throw new IllegalArgumentException("Either PatientId or PatientEmail is required.");
        }

        Caregiver caregiver = caregiverRepository.findById(dto.getCaregiverId())
                .orElseThrow(() -> new IllegalArgumentException("Caregiver not found."));

        if ("CAREGIVER".equalsIgnoreCase(role) && !caregiver.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Caregiver can only link themselves.");
        }

        if (dto.getPatientId() <= 0 && dto.getPatientEmail() != null && !dto.getPatientEmail().trim().isEmpty()) {
            User pUser = userRepository.findByEmailIgnoreCase(dto.getPatientEmail().trim().toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("Patient with that email not found."));
            if (!"PATIENT".equalsIgnoreCase(pUser.getRole())) {
                throw new IllegalArgumentException("User with that email is not a patient.");
            }
            Patient patient = patientRepository.findByUser_Id(pUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));
            dto.setPatientId(patient.getId());
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        if (caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(caregiver.getId(), patient.getId())) {
            throw new IllegalArgumentException("Relationship already exists.");
        }

        CaregiverPatient relationship = CaregiverPatient.builder()
                .caregiver(caregiver)
                .patient(patient)
                .relationType(dto.getRelationType() != null ? dto.getRelationType().trim() : "")
                .build();

        return caregiverPatientRepository.save(relationship);
    }

    public List<CaregiverPatient> getPatients(int caregiverId, int currentUserId, String role) {
        Caregiver caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new IllegalArgumentException("Caregiver not found."));

        if ("CAREGIVER".equalsIgnoreCase(role) && !caregiver.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Caregiver can only view their own linked patients.");
        }

        return caregiverPatientRepository.findByCaregiver_Id(caregiverId);
    }

    public List<CaregiverPatient> getCaregivers(int patientId, int currentUserId, String role) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        if ("PATIENT".equalsIgnoreCase(role) && !patient.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Patient can only view their own connected caregivers.");
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty() || !caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(caregiver.get().getId(), patientId)) {
                throw new SecurityException("Caregiver can only view caregivers of patients linked to them.");
            }
        }

        return caregiverPatientRepository.findByPatient_Id(patientId);
    }

    @Transactional
    public boolean unlinkPatient(int caregiverId, int patientId, int currentUserId, String role) {
        Caregiver caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new IllegalArgumentException("Caregiver not found."));

        if ("CAREGIVER".equalsIgnoreCase(role) && !caregiver.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("Caregiver can only unlink their own relationship.");
        }

        if (!caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(caregiverId, patientId)) {
            return false;
        }

        caregiverPatientRepository.deleteByCaregiver_IdAndPatient_Id(caregiverId, patientId);
        return true;
    }
}
