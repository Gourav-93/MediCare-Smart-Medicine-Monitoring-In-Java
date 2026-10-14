package com.backend.medicare.Repository;

import com.backend.medicare.Model.EmailDeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailDeliveryLogRepository extends JpaRepository<EmailDeliveryLog, Integer> {

    @Query("SELECT e FROM EmailDeliveryLog e WHERE e.medicineSchedule.id = :scheduleId " +
           "AND e.scheduledOccurrence = :occurrence AND e.notificationType = :notificationType " +
           "AND e.recipientEmail = :recipientEmail")
    Optional<EmailDeliveryLog> findByScheduleAndOccurrenceAndTypeAndEmail(
            @Param("scheduleId") Integer scheduleId,
            @Param("occurrence") LocalDateTime occurrence,
            @Param("notificationType") String notificationType,
            @Param("recipientEmail") String recipientEmail);
}
