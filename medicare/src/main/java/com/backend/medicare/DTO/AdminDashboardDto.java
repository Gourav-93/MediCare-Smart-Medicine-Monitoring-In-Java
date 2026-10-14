package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
import com.backend.medicare.Model.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    // No-Args Constructor
    public AdminDashboardDto() {
    }

    // All-Args Constructor
    public AdminDashboardDto(
            int totalUsers,
            int totalPatients,
            int totalCaregivers,
            int totalMedicines,
            int totalMedicineLogs,
            int totalTakenDoses,
            int totalMissedDoses,
            int totalPendingDoses,
            double overallAdherencePercentage,
            int activeEmergencyAlerts,
            int totalEmergencyAlerts,
            int unreadNotifications,
            int caregiverPatientRelationships,
            List<Medicine> lowStockMedicines,
            List<EmergencyAlert> activeEmergencies
    ) {
        this.totalUsers = totalUsers;
        this.totalPatients = totalPatients;
        this.totalCaregivers = totalCaregivers;
        this.totalMedicines = totalMedicines;
        this.totalMedicineLogs = totalMedicineLogs;
        this.totalTakenDoses = totalTakenDoses;
        this.totalMissedDoses = totalMissedDoses;
        this.totalPendingDoses = totalPendingDoses;
        this.overallAdherencePercentage = overallAdherencePercentage;
        this.activeEmergencyAlerts = activeEmergencyAlerts;
        this.totalEmergencyAlerts = totalEmergencyAlerts;
        this.unreadNotifications = unreadNotifications;
        this.caregiverPatientRelationships = caregiverPatientRelationships;
        this.lowStockMedicines = lowStockMedicines;
        this.activeEmergencies = activeEmergencies;
    }
}