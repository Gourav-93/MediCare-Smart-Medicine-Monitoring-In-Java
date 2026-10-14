package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Caregivers")
@Getter
@Setter
@Builder
public class Caregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Relationship", nullable = false)
    @Builder.Default
    private String relationship = "";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    @JsonIgnoreProperties({"caregiver", "patient", "hibernateLazyInitializer", "handler"})
    private User user;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"caregiver", "hibernateLazyInitializer", "handler"})
    private List<CaregiverPatient> caregiverPatients = new ArrayList<>();

    // No-Args Constructor (Required by JPA/Hibernate)
    public Caregiver() {
    }

    // All-Args Constructor
    public Caregiver(Integer id, String relationship, User user, List<CaregiverPatient> caregiverPatients) {
        this.id = id;
        this.relationship = relationship;
        this.user = user;
        this.caregiverPatients = caregiverPatients;
    }

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}