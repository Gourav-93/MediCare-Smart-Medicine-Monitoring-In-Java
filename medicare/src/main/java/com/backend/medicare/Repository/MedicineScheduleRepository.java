package com.backend.medicare.Repository;

import com.backend.medicare.Model.MedicineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicineScheduleRepository extends JpaRepository<MedicineSchedule, Integer> {
    List<MedicineSchedule> findByMedicine_Id(Integer medicineId);

    @Query("SELECT s FROM MedicineSchedule s WHERE s.medicine.patient.id = :patientId")
    List<MedicineSchedule> findByPatientId(@Param("patientId") Integer patientId);

    @Query("SELECT s FROM MedicineSchedule s WHERE s.medicine.patient.id IN :patientIds")
    List<MedicineSchedule> findByPatientIdIn(@Param("patientIds") List<Integer> patientIds);

    @Query("SELECT s FROM MedicineSchedule s WHERE s.medicine.patient.id = :patientId " +
           "AND s.medicine.startDate <= :endOfDay AND s.medicine.endDate >= :startOfDay ORDER BY s.time ASC")
    List<MedicineSchedule> findTodaySchedulesForPatient(
            @Param("patientId") Integer patientId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT s FROM MedicineSchedule s WHERE s.medicine.patient.id IN :patientIds " +
           "AND s.medicine.startDate <= :endOfDay AND s.medicine.endDate >= :startOfDay ORDER BY s.time ASC")
    List<MedicineSchedule> findTodaySchedulesForPatients(
            @Param("patientIds") List<Integer> patientIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}
