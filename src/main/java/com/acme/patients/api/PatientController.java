package com.acme.patients.api;

import com.google.common.collect.ImmutableList;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    public record Patient(String id, String name, String mrn, String careUnit, String notes) {}

    // De-identified sample data; this service never holds real PHI in the demo
    private static final List<Patient> PATIENTS = ImmutableList.of(
            new Patient("p-1001", "Patient A", "MRN-48213", "Cardiology", "Post-op day 2, stable"),
            new Patient("p-1002", "Patient B", "MRN-48977", "Neurology", "Monitoring <pending>"),
            new Patient("p-1003", "Patient C", "MRN-49102", "Diabetes", "CGM paired & syncing"));

    @GetMapping
    public List<Patient> list() {
        return PATIENTS.stream().map(PatientController::sanitize).toList();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable String id) {
        return PATIENTS.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .map(PatientController::sanitize)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "patient not found"));
    }

    private static Patient sanitize(Patient p) {
        return new Patient(p.id(), p.name(), p.mrn(), p.careUnit(), StringEscapeUtils.escapeHtml4(p.notes()));
    }
}
