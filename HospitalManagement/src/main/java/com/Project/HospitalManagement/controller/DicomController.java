package com.project.HospitalManagement.controller;


import com.project.HospitalManagement.dto.DicomFetchResponse;
import com.project.HospitalManagement.dto.DicomUploadResponse;
import com.project.HospitalManagement.service.DicomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/hospital")
public class DicomController {

    private final DicomService dicomService;

    public DicomController(DicomService dicomService) {
        this.dicomService = dicomService;
    }


    //post dicom
    @PostMapping("/{id}/dicom")
    public ResponseEntity<DicomUploadResponse> uploadDicom(@PathVariable Long id ,
                                                           @RequestParam("file") MultipartFile file)
    {
        return ResponseEntity.ok(dicomService.uploadDicom(id,file));
    }

    //Get dicom
    @GetMapping("/{id}/dicom")
    public ResponseEntity<DicomFetchResponse> fetchDicom(
            @PathVariable Long id) {

        return ResponseEntity.ok(dicomService.fetchDicom(id));
    }


}
