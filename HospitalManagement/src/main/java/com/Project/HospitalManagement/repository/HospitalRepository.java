package com.Project.HospitalManagement.repository;

import com.Project.HospitalManagement.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    @Query("SELECT h FROM Hospital h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY h.id DESC")
    List<Hospital> findByNameContainingIgnoreCase(String name);

    @Query("SELECT h FROM Hospital h WHERE LOWER(h.hospitalCode) LIKE LOWER(CONCAT('%', :hospitalCode, '%')) ORDER BY h.id DESC")
    List<Hospital> findByHospitalCodeContainingIgnoreCase(String hospitalCode);

    @Query("SELECT h FROM Hospital h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(h.hospitalCode) LIKE LOWER(CONCAT('%', :hospitalCode, '%')) ORDER BY h.id DESC")
    List<Hospital> findByNameContainingIgnoreCaseAndHospitalCodeContainingIgnoreCase(
            String name,
            String hospitalCode
    );

    // Perfect! Sorting by ID DESC puts newest first
    @Query("SELECT h FROM Hospital h ORDER BY h.id DESC")
    List<Hospital> findAllOrderByCreatedAtDesc();
}