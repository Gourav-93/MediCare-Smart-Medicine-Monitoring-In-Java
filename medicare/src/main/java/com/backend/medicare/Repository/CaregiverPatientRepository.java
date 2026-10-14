package com.backend.medicare.Repository;

import com.backend.medicare.Model.CaregiverPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaregiverPatientRepository extends JpaRepository<CaregiverPatient, Integer> {
    List<CaregiverPatient> findByCaregiver_Id(Integer caregiverId);
    List<CaregiverPatient> findByPatient_Id(Integer patientId);
    Optional<CaregiverPatient> findByCaregiver_IdAndPatient_Id(Integer caregiverId, Integer patientId);
    boolean existsByCaregiver_IdAndPatient_Id(Integer caregiverId, Integer patientId);
    void deleteByCaregiver_IdAndPatient_Id(Integer caregiverId, Integer patientId);

    default List<CaregiverPatient> findByCaregiverId(Integer caregiverId) {
        return findByCaregiver_Id(caregiverId);
    }

    default List<CaregiverPatient> findByPatientId(Integer patientId) {
        return findByPatient_Id(patientId);
    }
}
