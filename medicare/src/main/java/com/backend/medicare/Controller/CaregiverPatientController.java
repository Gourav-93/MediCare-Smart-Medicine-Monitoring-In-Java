package com.backend.medicare.Controller;

import com.backend.medicare.DTO.CaregiverPatientDto;
import com.backend.medicare.Model.CaregiverPatient;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.CaregiverPatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/Caregiverpatient", "/api/caregiverpatient", "/api/caregiver-patient"})
public class CaregiverPatientController {

    private final CaregiverPatientService service;

    public CaregiverPatientController(CaregiverPatientService service) {
        this.service = service;
    }

    @PostMapping("/link")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CAREGIVER', 'ADMIN', 'CAREGIVER')")
    public ResponseEntity<?> linkPatient(@RequestBody CaregiverPatientDto dto,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            CaregiverPatient result = service.linkPatient(dto, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(result);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/caregiver/{caregiverId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CAREGIVER', 'ADMIN', 'CAREGIVER')")
    public ResponseEntity<?> getPatients(@PathVariable int caregiverId,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (caregiverId <= 0) return ResponseEntity.badRequest().body("Invalid caregiverId.");

        try {
            List<CaregiverPatient> patients = service.getPatients(caregiverId, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(patients);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
    public ResponseEntity<?> getCaregivers(@PathVariable int patientId,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (patientId <= 0) return ResponseEntity.badRequest().body("Invalid patientId.");

        try {
            List<CaregiverPatient> caregivers = service.getCaregivers(patientId, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(caregivers);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/unlink")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CAREGIVER', 'ADMIN', 'CAREGIVER')")
    public ResponseEntity<?> unlinkPatient(@RequestParam int caregiverId,
                                          @RequestParam int patientId,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (caregiverId <= 0 || patientId <= 0) {
            return ResponseEntity.badRequest().body("Valid caregiverId and patientId are required.");
        }

        try {
            boolean unlinked = service.unlinkPatient(caregiverId, patientId, userPrincipal.getId(), userPrincipal.getRole());
            if (!unlinked) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Relationship not found.");
            }
            return ResponseEntity.ok("Patient unlinked successfully.");
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
