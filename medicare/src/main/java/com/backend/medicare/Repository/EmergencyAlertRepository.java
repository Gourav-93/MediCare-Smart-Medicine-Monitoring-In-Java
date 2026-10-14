package com.backend.medicare.Repository;

import com.backend.medicare.Model.EmergencyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Integer> {
    List<EmergencyAlert> findByPatient_IdOrderByCreatedAtDesc(Integer patientId);
    List<EmergencyAlert> findByPatient_IdInOrderByCreatedAtDesc(List<Integer> patientIds);
    List<EmergencyAlert> findAllByOrderByCreatedAtDesc();

    List<EmergencyAlert> findByPatient_IdAndStatusOrderByCreatedAtDesc(Integer patientId, String status);
    List<EmergencyAlert> findByPatient_IdInAndStatusOrderByCreatedAtDesc(List<Integer> patientIds, String status);
    List<EmergencyAlert> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);
}
