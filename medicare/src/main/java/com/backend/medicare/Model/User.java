package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", nullable = false)
    @Builder.Default
    private String name = "";

    @Column(name = "Email", nullable = false, unique = true)
    @Builder.Default
    private String email = "";

    @JsonIgnore
    @Column(name = "PasswordHash", nullable = false)
    @Builder.Default
    private String passwordHash = "";

    @Column(name = "Role", nullable = false)
    @Builder.Default
    private String role = "";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private Caregiver caregiver;

    // No-argument constructor
    public User() {
    }

    // All-argument constructor
    public User(Integer id, String name, String email,
                String passwordHash, String role,
                Patient patient, Caregiver caregiver) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.patient = patient;
        this.caregiver = caregiver;
    }
}