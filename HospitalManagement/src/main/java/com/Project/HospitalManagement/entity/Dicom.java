package com.Project.HospitalManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dicom", uniqueConstraints = @UniqueConstraint(columnNames = "hospital_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dicom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false, unique = true)
    private Long hospitalId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Column(nullable = false, length = 20)
    private String age;

    @Column(nullable = false, length = 20)
    private String sex;

    @Column(name = "birth_date", nullable = false, length = 15)
    private String birthDate;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "dicom_file_path", nullable = false, length = 250)
    private String dicomFilePath;
}
