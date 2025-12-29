package com.Project.HospitalManagement.service;

import com.Project.HospitalManagement.config.DicomMetadataExtractor;
import com.Project.HospitalManagement.dto.DicomFetchResponse;
import com.Project.HospitalManagement.dto.DicomUploadResponse;
import com.Project.HospitalManagement.dto.PatientDetailedResponse;
import com.Project.HospitalManagement.entity.Dicom;
import com.Project.HospitalManagement.entity.Hospital;
import com.Project.HospitalManagement.repository.DicomRepository;
import com.Project.HospitalManagement.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DicomService {

    private final DicomRepository dicomRepository;
    private final HospitalRepository hospitalRepository;

    public DicomService(DicomRepository dicomRepository,
                        HospitalRepository hospitalRepository) {
        this.dicomRepository = dicomRepository;
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Upload DICOM and extract metadata
     */
    public DicomUploadResponse uploadDicom(Long hospitalId, MultipartFile file) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital Id not found"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("DICOM file is required");
        }

        if (file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".dcm")) {
            throw new IllegalArgumentException("Invalid DICOM file format");
        }

        try {
            // ---------- Store file ----------
            String baseDir = System.getProperty("dicom.upload.dir");
            if (baseDir == null) {
                baseDir = "C:/hospital-data/dicom"; // fallback safety
            }

            File uploadDir = new File(baseDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".dcm";
            File target = new File(uploadDir, fileName);

            file.transferTo(target);

            String path = target.getAbsolutePath();

            // ---------- Extract metadata ----------
            Map<String, String> metadata = DicomMetadataExtractor.extract(target);

            String patientName = metadata.get("patient_name");
            String patientIdStr = metadata.get("patient_id");
            String age = metadata.get("age");
            String sex = metadata.get("sex");
            String birthDate = metadata.get("birth_date");

            // ---------- Validate required fields ----------
            if (patientIdStr == null || patientIdStr.isBlank()) {
                throw new RuntimeException("Missing Patient ID in DICOM");
            }
            if (patientName == null || patientName.isBlank()) {
                throw new RuntimeException("Missing Patient Name in DICOM");
            }
            if (sex == null || sex.isBlank()) {
                throw new RuntimeException("Missing Patient Sex in DICOM");
            }
            if (birthDate == null || birthDate.isBlank()) {
                throw new RuntimeException("Missing Patient Birth Date in DICOM");
            }

            // ---------- Save to DB ----------
            Dicom dicom = new Dicom();
            dicom.setHospitalId(hospitalId);
            dicom.setPatientId(patientIdStr);
            dicom.setPatientName(patientName);
            dicom.setAge(age); // may be null, allowed
            dicom.setSex(sex);
            dicom.setBirthDate(birthDate);
            dicom.setUploadDate(LocalDateTime.now());
            dicom.setDicomFilePath(path);

            dicomRepository.save(dicom);

            // ---------- Prepare response ----------
            PatientDetailedResponse patient = new PatientDetailedResponse();
            patient.setPatientName(patientName);
            patient.setPatientId(dicom.getPatientId());
            patient.setAge(age);
            patient.setSex(sex);
            patient.setBirthDate(birthDate);
            patient.setUploadDate(dicom.getUploadDate().toString());
            patient.setFileUrl(path);

            DicomUploadResponse response = new DicomUploadResponse();
            response.setHospitalCode(hospital.getHospitalCode());
            response.setDicomStatus("Attached");
            response.setDetailedResponse(patient);

            return response;

        } catch (Exception e) {
            // Keep real error visible for Postman + debugging
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Fetch DICOM metadata for hospital
     */
    public DicomFetchResponse fetchDicom(Long hospitalId) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid hospital ID"));

        return dicomRepository.findByHospitalId(hospitalId)
                .map(dicom -> {
                    DicomFetchResponse res = new DicomFetchResponse();
                    res.setHospitalCode(hospital.getHospitalCode());
                    res.setHasFile(true);
                    res.setDicomId(String.valueOf(dicom.getId()));
                    res.setDicomUrl(dicom.getDicomFilePath());
                    res.setXrayImageUrl(dicom.getDicomFilePath());

                    PatientDetailedResponse patient = new PatientDetailedResponse();
                    patient.setPatientName(dicom.getPatientName());
                    patient.setPatientId(dicom.getPatientId());
                    patient.setAge(dicom.getAge());
                    patient.setSex(dicom.getSex());
                    patient.setBirthDate(dicom.getBirthDate());
                    patient.setUploadDate(dicom.getUploadDate().toString());

                    res.setPatientDetails(patient);
                    return res;
                })
                .orElseGet(() -> {
                    DicomFetchResponse res = new DicomFetchResponse();
                    res.setHospitalCode(hospital.getHospitalCode());
                    res.setHasFile(false);
                    return res;
                });
    }
}
