package com.backend.medicare.Controller;

import com.backend.medicare.DTO.CaregiverDashboardDto;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/dashboard/caregiver", "/api/Dashboard/Caregiver"})
@PreAuthorize("hasAnyAuthority('ROLE_CAREGIVER', 'CAREGIVER', 'ROLE_ADMIN', 'ADMIN')")
public class CaregiverDashboardController {

    private final DashboardService dashboardService;

    public CaregiverDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            CaregiverDashboardDto dashboard = dashboardService.getCaregiverDashboard(userPrincipal.getId());
            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
