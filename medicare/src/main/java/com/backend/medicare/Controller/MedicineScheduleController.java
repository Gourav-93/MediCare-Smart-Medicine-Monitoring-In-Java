package com.backend.medicare.Controller;

import com.backend.medicare.DTO.MedicineScheduleDto;
import com.backend.medicare.Model.Medicine;
import com.backend.medicare.Model.MedicineSchedule;
import com.backend.medicare.Repository.MedicineRepository;
import com.backend.medicare.Security.UserPrincipal;
import com.backend.medicare.Service.MedicineScheduleService;
import com.backend.medicare.Service.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/Medicineschedule", "/api/medicineschedule", "/api/medicine-schedule"})
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_CAREGIVER', 'ADMIN', 'PATIENT', 'CAREGIVER')")
public class MedicineScheduleController {

    private final MedicineScheduleService scheduleService;
    private final MedicineService medicineService;
    private final MedicineRepository medicineRepository;

    public MedicineScheduleController(MedicineScheduleService scheduleService,
                                      MedicineService medicineService,
                                      MedicineRepository medicineRepository) {
        this.scheduleService = scheduleService;
        this.medicineService = medicineService;
        this.medicineRepository = medicineRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<MedicineSchedule> schedules = scheduleService.getAllForUser(userPrincipal.getId(), userPrincipal.getRole());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<MedicineSchedule> scheduleOpt = scheduleService.getById(id);
        if (scheduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
        }

        MedicineSchedule schedule = scheduleOpt.get();
        if (!medicineService.canAccessPatient(schedule.getMedicine().getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        return ResponseEntity.ok(schedule);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody MedicineScheduleDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Medicine> medicineOpt = medicineRepository.findById(dto.getMedicineId());
        if (medicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        if (!medicineService.canAccessPatient(medicineOpt.get().getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        try {
            MedicineSchedule schedule = scheduleService.add(dto);
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                   @RequestBody MedicineScheduleDto dto,
                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<MedicineSchedule> scheduleOpt = scheduleService.getById(id);
        if (scheduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
        }

        MedicineSchedule existing = scheduleOpt.get();
        if (!medicineService.canAccessPatient(existing.getMedicine().getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        Optional<Medicine> newMedicineOpt = medicineRepository.findById(dto.getMedicineId());
        if (newMedicineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found.");
        }

        if (!medicineService.canAccessPatient(newMedicineOpt.get().getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        try {
            MedicineSchedule updated = scheduleService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<MedicineSchedule> scheduleOpt = scheduleService.getById(id);
        if (scheduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
        }

        MedicineSchedule existing = scheduleOpt.get();
        if (!medicineService.canAccessPatient(existing.getMedicine().getPatient().getId(), userPrincipal.getId(), userPrincipal.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        boolean deleted = scheduleService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
        }

        return ResponseEntity.ok("Schedule deleted successfully.");
    }
}
