package com.backend.medicare.Controller;

import com.backend.medicare.DTO.MedicineLogDto;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.MedicineLog;
import com.backend.medicare.Repository.MedicineRepository;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.MedicineLogService;
import com.backend.medicare.Service.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/medicinelog", "/api/MedicineLog", "/api/medicine-log"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class MedicineLogController {

    private final MedicineLogService logService;
    private final MedicineService medicineService;
    private final MedicineRepository medicineRepository;

    public MedicineLogController(MedicineLogService logService,
                                 MedicineService medicineService,
                                 MedicineRepository medicineRepository) {
        this.logService = logService;
        this.medicineService = medicineService;
        this.medicineRepository = medicineRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<MedicineLog> logs = logService.getAllForUser(userPrincipal.getId(), userPrincipal.getRole());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<MedicineLog> logOpt = logService.getById(id);
        if (logOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine log not found.");
        }

        MedicineLog log = logOpt.get();
        if (!medicineService.canAccessPatient(log.getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        return ResponseEntity.ok(log);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody MedicineLogDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Medicine> medicineOpt = medicineRepository.findById(dto.getMedicineId());
        if (medicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        if (medicineOpt.get().getPatient().getId() != dto.getPatientId()) {
            return ResponseEntity.badRequest().body("Medicine does not belong to the specified patient.");
        }

        if (!medicineService.canAccessPatient(dto.getPatientId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        try {
            MedicineLog created = logService.add(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/taken")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'PATIENT')")
    public ResponseEntity<?> markAsTaken(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            MedicineLog updated = logService.markAsTaken(id, userPrincipal.getId());
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine log not found.");
            }
            return ResponseEntity.ok(updated);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
