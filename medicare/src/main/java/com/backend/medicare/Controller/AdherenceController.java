package com.backend.medicare.Controller;

import com.backend.medicare.DTO.AdherenceDto;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.AdherenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/adherence", "/api/Adherence"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class AdherenceController {

    @Autowired 
    private AdherenceService adherenceService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAdherence(@PathVariable int patientId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            AdherenceDto result = adherenceService.getPatientAdherence(patientId, userPrincipal.getId(), userPrincipal.getRole());
            return ResponseEntity.ok(result);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
