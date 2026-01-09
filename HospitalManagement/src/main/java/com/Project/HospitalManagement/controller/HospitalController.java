package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.dto.HospitalRequest;
import com.project.HospitalManagement.dto.HospitalResponse;
import com.project.HospitalManagement.entity.Hospital;
import com.project.HospitalManagement.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/addHospital")
    public ResponseEntity<HospitalResponse<Map<String, Object>>> createHospital(@Valid @RequestBody HospitalRequest request)
    {
        Hospital hospital = hospitalService.createHospital(request);

        Map<String, Object> responseData = Map.of(
                "hospitalId", hospital.getId(),
                "hospitalCode", hospital.getHospitalCode()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new HospitalResponse<>(
                        true,
                        "Hospital registered successfully.",
                        responseData
                )
        );
    }

    @GetMapping("/getList")
    public ResponseEntity<HospitalResponse<List<Hospital>>> getHospitals(
            @RequestParam(required = false) String hospitalName,
            @RequestParam(required = false) String hospitalCode
    ) {
        List<Hospital> hospitals =  hospitalService.getHospitals(hospitalName,hospitalCode);
        return ResponseEntity.ok(
                new HospitalResponse<>(
                        true,
                        "Hospitals fetched successfully.",
                        hospitals
                )
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponse<Hospital>> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalRequest request) {

        Hospital updatedHospital = hospitalService.updateHospital(id, request);

        return ResponseEntity.ok(
                new HospitalResponse<>(
                        true,
                        "Hospital record updated successfully.",
                        updatedHospital
                )
        );
    }
    @DeleteMapping("/deleteHospital/{id}")
    public ResponseEntity<HospitalResponse<Void>> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);

        return ResponseEntity.ok(
                new HospitalResponse<>(
                        true,
                        "Hospital deleted successfully.",
                        null
                )
        );
    }
}
