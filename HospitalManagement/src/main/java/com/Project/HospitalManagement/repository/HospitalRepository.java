package com.Project.HospitalManagement.repository;

import com.Project.HospitalManagement.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    List<Hospital> findByNameContainingIgnoreCase(String name);
    List<Hospital> findByHospitalCodeContainingIgnoreCase(String hospitalCode);
    List<Hospital> findByNameContainingIgnoreCaseAndHospitalCodeContainingIgnoreCase(
            String name,
            String hospitalCode
    );
}
