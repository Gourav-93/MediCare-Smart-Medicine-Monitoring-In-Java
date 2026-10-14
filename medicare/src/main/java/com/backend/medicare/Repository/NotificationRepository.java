package com.backend.medicare.Repository;

import com.backend.medicare.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findAllByOrderByCreatedAtDesc();
    long countByUser_IdAndIsReadFalse(Integer userId);
    long countByIsReadFalse();

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Integer userId);
}
