package com.Project.HospitalManagement.service;

import com.Project.HospitalManagement.repository.DicomRepository;
import com.Project.HospitalManagement.repository.HospitalRepository;
import com.Project.HospitalManagement.config.DicomMetadataExtractor;
import com.Project.HospitalManagement.dto.DicomFetchResponse;
import com.Project.HospitalManagement.dto.DicomUploadResponse;
import com.Project.HospitalManagement.dto.PatientDetailedResponse;
import com.Project.HospitalManagement.entity.Dicom;
import com.Project.HospitalManagement.entity.Hospital;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DicomService {
    private static final String DICOM_UPLOAD_DIR = "hospital-data/dicom";

    private final DicomRepository dicomRepository;
    private final HospitalRepository hospitalRepository;

    public DicomService(DicomRepository dicomRepository,
                        HospitalRepository hospitalRepository) {
        this.dicomRepository = dicomRepository;
        this.hospitalRepository = hospitalRepository;
    }

     // Upload DICOM and extract metadata

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
            // ====== FIX 1: Get the CURRENT WORKING DIRECTORY ======
            String projectRoot = System.getProperty("user.dir");
            System.out.println("DEBUG: Project root is: " + projectRoot);

            // ====== FIX 2: Create FULL path to upload directory ======
            File uploadDir = new File(projectRoot, DICOM_UPLOAD_DIR);
            System.out.println("DEBUG: Upload directory will be: " + uploadDir.getAbsolutePath());

            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("DEBUG: Directory created: " + created);
            }

            String fileName = System.currentTimeMillis() + ".dcm";
            File target = new File(uploadDir, fileName);

            System.out.println("DEBUG: Trying to save to: " + target.getAbsolutePath());

            // ====== FIX 3: Use Files.copy() instead of transferTo() ======
            // This ensures file goes to the exact location we specify
            java.nio.file.Files.copy(
                    file.getInputStream(),
                    target.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            String path = target.getAbsolutePath();
            System.out.println("SUCCESS: File saved to: " + path);

            // ====== FIX 4: Verify the file was created ======
            if (target.exists()) {
                System.out.println("VERIFIED: File exists, size: " + target.length() + " bytes");
            } else {
                System.out.println("WARNING: File might not have been created!");
            }

            // Extract metadata
            Map<String, String> metadata = DicomMetadataExtractor.extract(target);

            String patientName = metadata.get("patient_name");
            String patientIdStr = metadata.get("patient_id");
            String age = metadata.get("age");
            String sex = metadata.get("sex");
            String birthDate = metadata.get("birth_date");

            // Validate required fields
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

            // save and replace the dicom
            Dicom dicom = dicomRepository.findByHospitalId(hospitalId)
                    .orElseGet(Dicom::new);

            // ====== FIX 5: Delete old file with correct path ======
            if (dicom.getId() != null && dicom.getDicomFilePath() != null) {
                File oldFile = new File(projectRoot, dicom.getDicomFilePath());
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    System.out.println("DEBUG: Old file deleted: " + deleted);
                }
            }

            // ====== FIX 6: Store RELATIVE path in database ======
            String relativePath = DICOM_UPLOAD_DIR + "/" + fileName;

            //setting the data for database - store relative path
            extracted(hospitalId, dicom, patientIdStr, patientName, age, sex, birthDate, relativePath);

            dicomRepository.save(dicom);

            // ---------- Prepare response ----------
            PatientDetailedResponse patient = new PatientDetailedResponse();
            // Pass relative path to response, not absolute
            DicomUploadResponse response = getDicomUploadResponse(patient, patientName, dicom, age, sex, birthDate, relativePath, hospital);

            return response;

        } catch (Exception e) {
            System.err.println("ERROR in uploadDicom: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("DICOM upload failed: " + e.getMessage(), e);
        }
    }

    private static void extracted(Long hospitalId, Dicom dicom, String patientIdStr, String patientName, String age, String sex, String birthDate, String path) {
        dicom.setHospitalId(hospitalId);
        dicom.setPatientId(patientIdStr);
        dicom.setPatientName(patientName);
        dicom.setAge(age); // may be null, allowed
        dicom.setSex(sex);
        dicom.setBirthDate(birthDate);
        dicom.setUploadDate(LocalDateTime.now());
        dicom.setDicomFilePath(path);
    }

    private static DicomUploadResponse getDicomUploadResponse(PatientDetailedResponse patient,
                                                              String patientName, Dicom dicom, String age, String sex,
                                                              String birthDate, String relativePath, Hospital hospital) {  // Changed from 'path' to 'relativePath'

        patient.setPatientName(patientName);
        patient.setPatientId(dicom.getPatientId());
        patient.setAge(age);
        patient.setSex(sex);
        patient.setBirthDate(birthDate);
        patient.setUploadDate(dicom.getUploadDate().toString());

        // Convert relative path to absolute for the response if needed
        String projectRoot = System.getProperty("user.dir");
        String absolutePath = new File(projectRoot, relativePath).getAbsolutePath();
        patient.setFileUrl(absolutePath);

        DicomUploadResponse response = new DicomUploadResponse();
        response.setHospitalCode(hospital.getHospitalCode());
        response.setDicomStatus("Attached");
        response.setDetailedResponse(patient);
        return response;
    }


    //Fetch DICOM metadata for hospital
    public DicomFetchResponse fetchDicom(Long hospitalId){
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid hospital ID"));

        return dicomRepository.findByHospitalId(hospitalId)
                .map(dicom -> {
                    // ====== FIX: Convert relative path to absolute ======
                    String relativePath = dicom.getDicomFilePath();
                    String projectRoot = System.getProperty("user.dir");
                    File file = new File(projectRoot, relativePath);

                    System.out.println("DEBUG: Looking for file at: " + file.getAbsolutePath());

                    if (!file.exists()) {
                        System.err.println("ERROR: File not found at: " + file.getAbsolutePath());
                        throw new RuntimeException("DICOM file not found on disk");
                    }

                    String base64File;
                    try {
                        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
                        base64File = java.util.Base64.getEncoder().encodeToString(fileBytes);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to read DICOM file: " + e.getMessage());
                    }

                    DicomFetchResponse res = new DicomFetchResponse();
                    res.setHospitalCode(hospital.getHospitalCode());
                    res.setHasFile(true);
                    res.setDicomId(String.valueOf(dicom.getId()));
                    res.setDicomFileBase64(base64File);

                    PatientDetailedResponse patient = new PatientDetailedResponse();
                    patient.setPatientName(dicom.getPatientName());
                    patient.setPatientId(dicom.getPatientId());
                    patient.setAge(dicom.getAge());
                    patient.setSex(dicom.getSex());
                    patient.setBirthDate(dicom.getBirthDate());
                    patient.setUploadDate(dicom.getUploadDate().toString());
                    // Store the actual file path if needed
                    patient.setFileUrl(file.getAbsolutePath());

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
