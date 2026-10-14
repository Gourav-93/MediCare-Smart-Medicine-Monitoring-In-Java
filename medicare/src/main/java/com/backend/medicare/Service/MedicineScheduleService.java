package com.backend.medicare.Service;

import com.backend.medicare.DTO.MedicineScheduleDto;
import com.backend.medicare.Model.Caregiver;
import com.backend.medicare.Model.CaregiverPatient;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.MedicineSchedule;
import com.backend.medicare.Model.Patient;
import com.backend.medicare.Repository.CaregiverPatientRepository;
import com.backend.medicare.Repository.CaregiverRepository;
import com.backend.medicare.Repository.MedicineRepository;
import com.backend.medicare.Repository.MedicineScheduleRepository;
import com.backend.medicare.Repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineScheduleService {

    private final MedicineScheduleRepository scheduleRepository;
    private final MedicineRepository medicineRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPatientRepository caregiverPatientRepository;

    public MedicineScheduleService(MedicineScheduleRepository scheduleRepository,
                                  MedicineRepository medicineRepository,
                                  PatientRepository patientRepository,
                                  CaregiverRepository caregiverRepository,
                                  CaregiverPatientRepository caregiverPatientRepository) {
        this.scheduleRepository = scheduleRepository;
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.caregiverRepository = caregiverRepository;
        this.caregiverPatientRepository = caregiverPatientRepository;
    }

    public List<MedicineSchedule> getAllForUser(int currentUserId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return scheduleRepository.findAll();
        }

        if ("PATIENT".equalsIgnoreCase(role)) {
            Optional<Patient> patient = patientRepository.findByUser_Id(currentUserId);
            return patient.map(value -> scheduleRepository.findByPatientId(value.getId())).orElse(Collections.emptyList());
        }

        if ("CAREGIVER".equalsIgnoreCase(role)) {
            Optional<Caregiver> caregiver = caregiverRepository.findByUser_Id(currentUserId);
            if (caregiver.isEmpty()) return Collections.emptyList();

            List<CaregiverPatient> links = caregiverPatientRepository.findByCaregiver_Id(caregiver.get().getId());
            List<Integer> patientIds = links.stream().map(CaregiverPatient::getPatientId).toList();
            if (patientIds.isEmpty()) return Collections.emptyList();

            return scheduleRepository.findByPatientIdIn(patientIds);
        }

        return Collections.emptyList();
    }

    public Optional<MedicineSchedule> getById(int id) {
        return scheduleRepository.findById(id);
    }

    @Transactional
    public MedicineSchedule add(MedicineScheduleDto dto) {
        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found."));

        MedicineSchedule schedule = MedicineSchedule.builder()
                .medicine(medicine)
                .time(dto.getTime())
                .frequency(dto.getFrequency() != null ? dto.getFrequency().trim() : "")
                .build();

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public MedicineSchedule update(int id, MedicineScheduleDto dto) {
        MedicineSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found."));

        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found."));

        schedule.setMedicine(medicine);
        schedule.setTime(dto.getTime());
        schedule.setFrequency(dto.getFrequency() != null ? dto.getFrequency().trim() : "");

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public boolean delete(int id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
