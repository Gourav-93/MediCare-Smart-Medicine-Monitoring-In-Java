package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.MedicineLog;
import com.backend.medicare.Model.MedicineSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PatientDashboardDto {

    @Builder.Default
    private PatientInfoDto patient = new PatientInfoDto();

    private int totalMedicines;

    @Builder.Default
    private List<Medicine> medicines = new ArrayList<>();

    @Builder.Default
    private List<MedicineSchedule> todaySchedules = new ArrayList<>();

    @Builder.Default
    private List<MedicineLog> pendingMedicines = new ArrayList<>();

    @Builder.Default
    private List<MedicineLog> missedMedicines = new ArrayList<>();

    @Builder.Default
    private List<MedicineLog> takenMedicines = new ArrayList<>();

    private int totalDoses;
    private int takenDoses;
    private int missedDoses;
    private double adherencePercentage;

    @Builder.Default
    private List<Medicine> lowStockMedicines = new ArrayList<>();

    @Builder.Default
    private List<EmergencyAlert> activeEmergencies = new ArrayList<>();

    private int unreadNotifications;

    // No-Args Constructor
    public PatientDashboardDto() {
    }

    // All-Args Constructor
    public PatientDashboardDto(
            PatientInfoDto patient,
            int totalMedicines,
            List<Medicine> medicines,
            List<MedicineSchedule> todaySchedules,
            List<MedicineLog> pendingMedicines,
            List<MedicineLog> missedMedicines,
            List<MedicineLog> takenMedicines,
            int totalDoses,
            int takenDoses,
            int missedDoses,
            double adherencePercentage,
            List<Medicine> lowStockMedicines,
            List<EmergencyAlert> activeEmergencies,
            int unreadNotifications
    ) {
        this.patient = patient;
        this.totalMedicines = totalMedicines;
        this.medicines = medicines;
        this.todaySchedules = todaySchedules;
        this.pendingMedicines = pendingMedicines;
        this.missedMedicines = missedMedicines;
        this.takenMedicines = takenMedicines;
        this.totalDoses = totalDoses;
        this.takenDoses = takenDoses;
        this.missedDoses = missedDoses;
        this.adherencePercentage = adherencePercentage;
        this.lowStockMedicines = lowStockMedicines;
        this.activeEmergencies = activeEmergencies;
        this.unreadNotifications = unreadNotifications;
    }
}