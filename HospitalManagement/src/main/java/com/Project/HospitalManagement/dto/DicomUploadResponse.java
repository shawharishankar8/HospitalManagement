package com.Project.HospitalManagement.dto;


import lombok.Data;

@Data
public class DicomUploadResponse {

    private String hospitalCode;
    private String dicomStatus;
    private PatientDetailedResponse detailedResponse;
}
