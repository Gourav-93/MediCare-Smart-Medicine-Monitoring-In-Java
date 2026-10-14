package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
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
public class CaregiverDashboardDto {

    @Builder.Default
    private CaregiverInfoDto caregiver = new CaregiverInfoDto();

    @Builder.Default
    private List<CaregiverDashboardPatientDto> patients = new ArrayList<>();

    @Builder.Default
    private List<EmergencyAlert> activeEmergencies = new ArrayList<>();

    @Builder.Default
    private List<MedicineLog> missedMedicines = new ArrayList<>();

    @Builder.Default
    private List<MedicineSchedule> todaySchedules = new ArrayList<>();

    @Builder.Default
    private List<MedicineLog> pendingMedicines = new ArrayList<>();

    @Builder.Default
    private List<PatientAdherenceDto> adherence = new ArrayList<>();

    private int unreadNotifications;

    // No-Args Constructor
    public CaregiverDashboardDto() {
    }

    // All-Args Constructor
    public CaregiverDashboardDto(
            CaregiverInfoDto caregiver,
            List<CaregiverDashboardPatientDto> patients,
            List<EmergencyAlert> activeEmergencies,
            List<MedicineLog> missedMedicines,
            List<MedicineSchedule> todaySchedules,
            List<MedicineLog> pendingMedicines,
            List<PatientAdherenceDto> adherence,
            int unreadNotifications
    ) {
        this.caregiver = caregiver;
        this.patients = patients;
        this.activeEmergencies = activeEmergencies;
        this.missedMedicines = missedMedicines;
        this.todaySchedules = todaySchedules;
        this.pendingMedicines = pendingMedicines;
        this.adherence = adherence;
        this.unreadNotifications = unreadNotifications;
    }
}