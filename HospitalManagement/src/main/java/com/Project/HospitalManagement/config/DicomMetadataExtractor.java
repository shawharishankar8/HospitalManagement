package com.Project.HospitalManagement.config;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DicomMetadataExtractor {

    public static Map<String, String> extract(File dicomFile) throws IOException {

        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {

            Attributes attrs = dis.readDataset(-1, -1);

            Map<String, String> metadata = new HashMap<>();

            metadata.put("patient_name",
                    attrs.getString(Tag.PatientName, ""));

            metadata.put("patient_id",
                    attrs.getString(Tag.PatientID, ""));

            metadata.put("age",
                    attrs.getString(Tag.PatientAge, ""));

            metadata.put("sex",
                    attrs.getString(Tag.PatientSex, ""));

            metadata.put("birth_date",
                    attrs.getString(Tag.PatientBirthDate, ""));

            return metadata;
        }
    }
}
