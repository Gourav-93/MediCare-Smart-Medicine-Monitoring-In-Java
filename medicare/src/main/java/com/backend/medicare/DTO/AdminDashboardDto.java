package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
import com.backend.medicare.Model.Medicine;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDto {
    private int totalUsers;
    private int totalPatients;
    private int totalCaregivers;
    private int totalMedicines;
    private int totalMedicineLogs;
    private int totalTakenDoses;
    private int totalMissedDoses;
    private int totalPendingDoses;
    private double overallAdherencePercentage;
    private int activeEmergencyAlerts;
    private int totalEmergencyAlerts;
    private int unreadNotifications;
    private int caregiverPatientRelationships;

    @Builder.Default
    private List<Medicine> lowStockMedicines = new ArrayList<>();

    @Builder.Default
    private List<EmergencyAlert> activeEmergencies = new ArrayList<>();
}
