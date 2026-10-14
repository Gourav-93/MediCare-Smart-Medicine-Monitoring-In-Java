package com.backend.medicare.Repository;

import com.backend.medicare.Model.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaregiverRepository extends JpaRepository<Caregiver, Integer> {
    Optional<Caregiver> findByUser_Id(Integer userId);

    default Optional<Caregiver> findByUserId(Integer userId) {
        return findByUser_Id(userId);
    }
}
