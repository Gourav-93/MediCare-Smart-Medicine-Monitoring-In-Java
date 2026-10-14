package com.backend.medicare.Repository;

import com.backend.medicare.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByUser_Id(Integer userId);
    boolean existsByIdAndUser_Id(Integer id, Integer userId);

    default Optional<Patient> findByUserId(Integer userId) {
        return findByUser_Id(userId);
    }

    default boolean existsByIdAndUserId(Integer id, Integer userId) {
        return existsByIdAndUser_Id(id, userId);
    }
}
