package com.Project.HospitalManagement.dto;

import lombok.Data;

@Data
public class PatientDetailedResponse {
    private String patientName;
    private String patientId;
    private String age;
    private String sex;
    private String birthDate;
    private String uploadDate;
    private String fileUrl;
}
