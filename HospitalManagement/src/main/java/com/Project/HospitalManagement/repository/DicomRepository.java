package com.Project.HospitalManagement.repository;

import com.Project.HospitalManagement.entity.Dicom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.LongFunction;

@Repository
public interface DicomRepository extends JpaRepository<Dicom,Long> {
    Optional<Dicom> findByHospitalId(Long hospitalId);


}
