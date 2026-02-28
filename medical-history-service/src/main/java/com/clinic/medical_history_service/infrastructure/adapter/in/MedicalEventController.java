package com.clinic.medical_history_service.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.medical_history_service.application.service.MedicalEventService;
import com.clinic.medical_history_service.domain.model.Analysis;
import com.clinic.medical_history_service.domain.model.Diagnosis;
import com.clinic.medical_history_service.domain.model.Referral;
import com.clinic.medical_history_service.domain.model.Treatment;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/medical-events")
@RequiredArgsConstructor
public class MedicalEventController {

    private final MedicalEventService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Map<String, Object>> findAll(
            @RequestParam(required = false) Long appointmentId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.findAll(appointmentId, petId, veterinarianId, page, size);
    }

    @GetMapping("/diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Map<String, Object>> listDiagnosis(
            @RequestParam(required = false) Long medicalCaseId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listDiagnosis(
                medicalCaseId,
                petId,
                veterinarianId,
                page,
                size);
    }

    @PreAuthorize("hasRole('VETERINARY')")
    @PostMapping("/diagnosis")
    public Mono<Diagnosis> createDiagnosis(
            @RequestBody Diagnosis diagnosis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createDiagnosis(diagnosis, vetId);
    }

    @GetMapping("/analysis")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Map<String, Object>> listAnalysis(
            @RequestParam(required = false) Long medicalCaseId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listAnalysis(
                medicalCaseId,
                petId,
                veterinarianId,
                page,
                size);
    }

    @PostMapping("/analysis")
    public Mono<Analysis> createAnalysis(
            @RequestBody Analysis analysis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createAnalysis(analysis, vetId);
    }

    @GetMapping("/referral")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Map<String, Object>> listReferral(
            @RequestParam(required = false) Long medicalCaseId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listReferral(
                medicalCaseId,
                petId,
                veterinarianId,
                page,
                size);
    }

    @PostMapping("/referral")
    public Mono<Referral> createReferral(
            @RequestBody Referral referral,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createReferral(referral, vetId);
    }

    @GetMapping("/treatment")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Map<String, Object>> listTreatment(
            @RequestParam(required = false) Long medicalCaseId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listTreatment(
                medicalCaseId,
                petId,
                veterinarianId,
                page,
                size);
    }

    @PostMapping("/treatment")
    public Mono<Treatment> createTreatment(
            @RequestBody Treatment treatment,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createTreatment(treatment, vetId);
    }

    @PreAuthorize("hasRole('VETERINARY','ADMIN')")
    @PutMapping("/analysis/{id}")
    public Mono<Analysis> updateAnalysis(
            @PathVariable Long id,
            @RequestBody Analysis analysis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.updateAnalysis(id, analysis, vetId);
    }

    @PreAuthorize("hasRole('VETERINARY','ADMIN')")
    @PutMapping("/diagnosis/{id}")
    public Mono<Diagnosis> updateDiagnosis(
            @PathVariable Long id,
            @RequestBody Diagnosis diagnosis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.updateDiagnosis(id, diagnosis, vetId);
    }

    @PreAuthorize("hasRole('VETERINARY','ADMIN')")
    @PutMapping("/referral/{id}")
    public Mono<Referral> updateReferral(
            @PathVariable Long id,
            @RequestBody Referral referral,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.updateReferral(id, referral, vetId);
    }

    @PreAuthorize("hasRole('VETERINARY','ADMIN')")
    @PutMapping("/treatment/{id}")
    public Mono<Treatment> updateTreatment(
            @PathVariable Long id,
            @RequestBody Treatment treatment,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.updateTreatment(id, treatment, vetId);
    }
}