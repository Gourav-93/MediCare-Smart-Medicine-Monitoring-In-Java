package com.backend.medicare.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name = "";

    @Column(name = "Email", nullable = false, unique = true)
    private String email = "";

    @JsonIgnore
    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash = "";

    @Column(name = "Role", nullable = false)
    private String role = "";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private Caregiver caregiver;
}