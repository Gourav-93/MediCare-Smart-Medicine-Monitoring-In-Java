package com.backend.medicare.Service;

import com.backend.medicare.DTO.MedicineDto;
import com.backend.medicare.Model.Caregiver;
import com.backend.medicare.Model.CaregiverPatient;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.Patient;
import com.backend.medicare.Repository.CaregiverPatientRepository;
import com.backend.medicare.Repository.CaregiverRepository;
import com.backend.medicare.Repository.MedicineRepository;
import com.backend.medicare.Repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;

    public MedicineService(MedicineRepository medicineRepository,
                           PatientRepository patientRepository,
                           CaregiverRepository caregiverRepository,
                           CaregiverPatientRepository caregiverPatientRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
    }

    public boolean canAccessPatient(int patientId, int currentUserId, String role) {
        if (currentUserId <= 0) return false;

        if ("ADMIN".equalsIgnoreCase(role)) {
            return patientRepository.existsById(patientId);
        }

        if ("PATIENT".equalsIgnoreCase(role)) {
            return patientRepository.existsByIdAndUser_Id(patientId, currentUserId);
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            return caregiver.filter(value -> caregiverPatientRepository.existsByCaregiver_IdAndPatient_Id(value.getId(), patientId)).isPresent();
        }

        return false;
    }

    public List<Medicine> getAllForUser(int currentUserId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return medicineRepository.findAll();
        }

        if ("PATIENT".equalsIgnoreCase(role)) {
            Optional<Patient> patient = patientRepository.findByUser_Id(currentUserId);
            return patient.map(value -> medicineRepository.findByPatient_Id(value.getId())).orElse(Collections.emptyList());
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty()) return Collections.emptyList();

            List<CaregiverPatient> links = caregiverPatientRepository.findByCaregiver_Id(caregiver.get().getId());
            List<Integer> patientIds = links.stream().map(CaregiverPatient::getPatientId).toList();
            if (patientIds.isEmpty()) return Collections.emptyList();

            return medicineRepository.findByPatient_IdIn(patientIds);
        }

        return Collections.emptyList();
    }

    public Optional<Medicine> getById(int id) {
        return medicineRepository.findById(id);
    }

    @Transactional
    public Medicine add(MedicineDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        Medicine medicine = Medicine.builder()
                .patient(patient)
                .name(dto.getName() != null ? dto.getName().trim() : "")
                .dosage(dto.getDosage() != null ? dto.getDosage().trim() : "")
                .dose(dto.getDose())
                .stock(dto.getStock())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return medicineRepository.save(medicine);
    }

    @Transactional
    public Medicine update(int id, MedicineDto dto) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found."));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        medicine.setPatient(patient);
        medicine.setName(dto.getName() != null ? dto.getName().trim() : "");
        medicine.setDosage(dto.getDosage() != null ? dto.getDosage().trim() : "");
        medicine.setDose(dto.getDose());
        medicine.setStock(dto.getStock());
        medicine.setStartDate(dto.getStartDate());
        medicine.setEndDate(dto.getEndDate());

        return medicineRepository.save(medicine);
    }

    @Transactional
    public boolean delete(int id) {
        if (medicineRepository.existsById(id)) {
            medicineRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
