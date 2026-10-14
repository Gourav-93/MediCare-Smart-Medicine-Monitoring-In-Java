package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Caregivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Relationship", nullable = false)
    private String relationship = "";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    @JsonIgnoreProperties({"caregiver", "patient", "hibernateLazyInitializer", "handler"})
    private User user;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"caregiver", "hibernateLazyInitializer", "handler"})
    private List<CaregiverPatient> caregiverPatients = new ArrayList<>();

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}