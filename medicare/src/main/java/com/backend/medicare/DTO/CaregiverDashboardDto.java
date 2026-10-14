package com.backend.medicare.DTO;

import com.backend.medicare.Model.EmergencyAlert;
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
}
