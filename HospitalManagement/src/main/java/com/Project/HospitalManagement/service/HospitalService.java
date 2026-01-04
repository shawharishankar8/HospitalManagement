package com.project.HospitalManagement.service;

import com.project.HospitalManagement.dto.ContactRequest;
import com.project.HospitalManagement.dto.HospitalRequest;
import com.project.HospitalManagement.entity.Hospital;
import com.project.HospitalManagement.entity.HospitalContact;
import com.project.HospitalManagement.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HospitalService  {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital createHospital(HospitalRequest request)
    {
        Hospital hospital = new Hospital();
        hospital.setName(request.getHospitalName());
        hospital.setAddress(request.getHospitalAddress());
        hospital.setDate(LocalDate.now());

        hospital.setHospitalCode(generateHospitalCode(request.getHospitalName()));

        hospital.setContacts(mapContacts(hospital, request));

        return hospitalRepository.save(hospital);
    }

    public List<Hospital> getHospitals(String hospitalName, String hospitalCode)
    {
        if (hospitalName != null && hospitalCode != null) {
            return hospitalRepository
                    .findByNameContainingIgnoreCaseAndHospitalCodeContainingIgnoreCase(
                            hospitalName, hospitalCode
                    );
        }

        if (hospitalName != null) {
            return hospitalRepository.findByNameContainingIgnoreCase(hospitalName);
        }

        if (hospitalCode != null) {
            return hospitalRepository.findByHospitalCodeContainingIgnoreCase(hospitalCode);
        }
        return hospitalRepository.findAll();
    }

    public Hospital updateHospital(Long id , HospitalRequest request)
    {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Hospital Id not found"));

        hospital.setName(request.getHospitalName());
        hospital.setAddress(request.getHospitalAddress());
        hospital.getContacts().clear();
        hospital.getContacts().addAll(mapContacts(hospital, request));

        return hospitalRepository.save(hospital);

    }

    private List<HospitalContact> mapContacts(Hospital hospital, HospitalRequest request) {
        List<HospitalContact> contacts = new ArrayList<>();

        contacts.add(buildContact(hospital, request.getFirstContact(),0));
        contacts.add(buildContact(hospital, request.getSecondContact(),1));

        return  contacts;
    }

    private HospitalContact buildContact(Hospital hospital, ContactRequest dto , int type)
    {
        HospitalContact c = new HospitalContact();
        c.setHospital(hospital);
        c.setContactType(type);
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getContactNumber());

        return c;
    }

    private String generateHospitalCode(String hospitalName) {

        String prefix = hospitalName.replaceAll("\\s+", "").substring(0, 1).toUpperCase();
        String suffix = UUID.randomUUID().toString().replace("-", "");
        return prefix + "-" + suffix;

    }
}
