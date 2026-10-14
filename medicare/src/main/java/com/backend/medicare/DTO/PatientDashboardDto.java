package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.MedicineLog;
import com.backend.medicare.Model.MedicineSchedule;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
