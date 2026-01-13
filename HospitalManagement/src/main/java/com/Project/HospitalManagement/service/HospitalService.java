package com.Project.HospitalManagement.service;

import com.Project.HospitalManagement.repository.HospitalRepository;
import com.Project.HospitalManagement.dto.ContactRequest;
import com.Project.HospitalManagement.dto.HospitalRequest;
import com.Project.HospitalManagement.entity.Hospital;
import com.Project.HospitalManagement.entity.HospitalContact;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HospitalService  {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital createHospital(HospitalRequest request)
    {
        Hospital hospital = new Hospital();
        hospital.setName(request.getHospitalName());
        hospital.setAddress(request.getHospitalAddress());
        hospital.setHospitalCode(generateHospitalCode(request.getHospitalName()));
        hospital.setContacts(mapContacts(hospital, request));
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Setting date to: " + now);
        System.out.println("Hour: " + now.getHour() + ", Minute: " + now.getMinute());
        hospital.setDate(now);

        return hospitalRepository.save(hospital);
    }

    public List<Hospital> getHospitals(String hospitalName, String hospitalCode)
    {
        final int MAX_LIMIT=100;
        List<Hospital> hospitals = null;
        if (hospitalName != null && hospitalCode != null) {
            hospitals= hospitalRepository
                    .findByNameContainingIgnoreCaseAndHospitalCodeContainingIgnoreCase(
                            hospitalName, hospitalCode
                    );
        }

        else if (hospitalName != null) {
            hospitals= hospitalRepository.findByNameContainingIgnoreCase(hospitalName);
        }

         else if(hospitalCode != null) {
            hospitals=hospitalRepository.findByHospitalCodeContainingIgnoreCase(hospitalCode);
        }

        // Use the renamed method
        else{
            hospitals= hospitalRepository.findAllOrderByCreatedAtDesc();
        }
        return hospitals.stream()
                .limit(MAX_LIMIT)
                .collect(Collectors.toList());
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

    public void deleteHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hospital Id not found"));

        hospitalRepository.delete(hospital);
    }
}
