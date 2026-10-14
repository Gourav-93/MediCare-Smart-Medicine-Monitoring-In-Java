package com.backend.medicare.Repository;

import com.backend.medicare.Model.MedicineLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineLogRepository extends JpaRepository<MedicineLog, Integer> {
    List<MedicineLog> findByPatient_Id(Integer patientId);
    List<MedicineLog> findByPatient_IdIn(List<Integer> patientIds);
    Optional<MedicineLog> findByMedicine_IdAndScheduledTime(Integer medicineId, LocalDateTime scheduledTime);

    @Query("SELECT l FROM MedicineLog l WHERE l.patient.id = :patientId AND l.scheduledTime >= :startOfDay AND l.scheduledTime <= :endOfDay")
    List<MedicineLog> findTodayLogsForPatient(
            @Param("patientId") Integer patientId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT l FROM MedicineLog l WHERE l.patient.id IN :patientIds AND l.scheduledTime >= :startOfDay AND l.scheduledTime <= :endOfDay")
    List<MedicineLog> findTodayLogsForPatients(
            @Param("patientIds") List<Integer> patientIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    List<MedicineLog> findTop20ByPatient_IdAndStatusOrderByScheduledTimeDesc(Integer patientId, String status);
    List<MedicineLog> findTop20ByPatient_IdInAndStatusOrderByScheduledTimeDesc(List<Integer> patientIds, String status);
    List<MedicineLog> findTop20ByPatient_IdAndStatusOrderByTakenTimeDesc(Integer patientId, String status);

    long countByStatusIgnoreCase(String status);
    long countByPatient_IdAndStatusIgnoreCase(Integer patientId, String status);
}
