package com.backend.medicare.Controller;

import com.backend.medicare.DTO.MedicineDto;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.Patient;
import com.backend.medicare.Repository.PatientRepository;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/medicine", "/api/Medicine"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class MedicineController {

    private final MedicineService medicineService;
    private final PatientRepository patientRepository;

    public MedicineController(MedicineService medicineService, PatientRepository patientRepository) {
        this.medicineService = medicineService;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Medicine> medicines = medicineService.getAllForUser(userPrincipal.getId(), userPrincipal.getRole());
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Medicine> medicineOpt = medicineService.getById(id);
        if (medicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        Medicine medicine = medicineOpt.get();
        if (!medicineService.canAccessPatient(medicine.getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        return ResponseEntity.ok(medicine);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody MedicineDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if ("PATIENT".equalsIgnoreCase(userPrincipal.getRole())) {
            Optional<Patient> patientOpt = patientRepository.findByUser_Id(userPrincipal.getId());
            patientOpt.ifPresent(patient -> dto.setPatientId(patient.getId()));
        }

        if (dto.getPatientId() <= 0) {
            return ResponseEntity.badRequest().body("Valid patientId is required.");
        }

        if (!medicineService.canAccessPatient(dto.getPatientId(), userPrincipal.getId(), userPrincipal.getRole())) {
            if (!patientRepository.existsById(dto.getPatientId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        try {
            Medicine created = medicineService.add(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                   @RequestBody MedicineDto dto,
                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if ("PATIENT".equalsIgnoreCase(userPrincipal.getRole())) {
            Optional<Patient> patientOpt = patientRepository.findByUser_Id(userPrincipal.getId());
            patientOpt.ifPresent(patient -> dto.setPatientId(patient.getId()));
        }

        if (dto.getPatientId() <= 0) {
            return ResponseEntity.badRequest().body("Valid patientId is required.");
        }

        Optional<Medicine> medicineOpt = medicineService.getById(id);
        if (medicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        Medicine existing = medicineOpt.get();
        if (!medicineService.canAccessPatient(existing.getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        if (!medicineService.canAccessPatient(dto.getPatientId(), userPrincipal.getId(), userPrincipal.getRole())) {
            if (!patientRepository.existsById(dto.getPatientId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        try {
            Medicine updated = medicineService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Medicine> medicineOpt = medicineService.getById(id);
        if (medicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        Medicine existing = medicineOpt.get();
        if (!medicineService.canAccessPatient(existing.getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        boolean deleted = medicineService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        return ResponseEntity.ok("Medicine deleted successfully.");
    }
}
