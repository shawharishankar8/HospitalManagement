package com.Project.HospitalManagement.dto;

import lombok.Data;

@Data
public class DicomFetchResponse {
    private String hospitalCode;
    private boolean hasFile;
    private String dicomId;
    private String dicomUrl;
    private String dicomFileBase64;
    private PatientDetailedResponse patientDetails;
}
