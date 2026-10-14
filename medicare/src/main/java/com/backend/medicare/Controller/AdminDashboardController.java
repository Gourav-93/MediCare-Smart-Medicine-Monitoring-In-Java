package com.backend.medicare.Controller;

import com.backend.medicare.DTO.AdminDashboardDto;
import com.backend.medicare.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/dashboard/admin", "/api/Dashboard/Admin"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        AdminDashboardDto dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }
}
