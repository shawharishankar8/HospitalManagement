package com.Project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospital_contacts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    @JsonBackReference
    private Hospital hospital;

    @Column(name = "contact_type", nullable = false)
    private int contactType;
    // 0 = First Contact, 1 = Second Contact

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 25)
    private String phone;
}
