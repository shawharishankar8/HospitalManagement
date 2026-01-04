package com.project.HospitalManagement.dto;


import lombok.Data;

@Data
public class DicomUploadResponse {

    private String hospitalCode;
    private String dicomStatus;
    private PatientDetailedResponse detailedResponse;
}
