package com.backend.medicare.Repository;

import com.backend.medicare.Model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    List<Medicine> findByPatient_Id(Integer patientId);
    List<Medicine> findByPatient_IdIn(List<Integer> patientIds);
    List<Medicine> findByPatient_IdOrderByNameAsc(Integer patientId);
    List<Medicine> findByStockLessThanEqualOrderByStockAsc(int stockThreshold);
}
