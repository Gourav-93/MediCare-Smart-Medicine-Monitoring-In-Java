package com.backend.medicare.Controller;

import com.backend.medicare.DTO.EmergencyAlertDto;
import com.backend.medicare.Model.EmergencyAlert;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.EmergencyAlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/emergency", "/api/Emergency"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class EmergencyController {

    private final EmergencyAlertService alertService;

    public EmergencyController(EmergencyAlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'PATIENT')")
    public ResponseEntity<?> createEmergency(@RequestBody EmergencyAlertDto dto,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Map<String, Object> result = alertService.createEmergency(dto, userPrincipal.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getEmergencies(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<EmergencyAlert> alerts = alertService.getEmergencies(userPrincipal.getId(), userPrincipal.getRole());
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveEmergency(@PathVariable int id,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Map<String, Object> result = alertService.resolveEmergency(id, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(result);
        } catch (java.util.NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
