package com.Project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="hospital_code", nullable = false, unique = true, length = 50)
    private String hospitalCode;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = false,length = 200)
    private String address;

    @Column(nullable = false)
    private LocalDate date;


    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<HospitalContact> contacts;
}
