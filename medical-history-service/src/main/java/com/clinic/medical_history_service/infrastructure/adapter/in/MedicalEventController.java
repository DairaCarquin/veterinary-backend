package com.clinic.medical_history_service.infrastructure.adapter.in;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PreAuthorize("hasRole('VETERINARY')")
    @PostMapping("/diagnosis")
    public Mono<Diagnosis> createDiagnosis(
            @RequestBody Diagnosis diagnosis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createDiagnosis(diagnosis, vetId);
    }

    @PostMapping("/analysis")
    public Mono<Analysis> createAnalysis(
            @RequestBody Analysis analysis,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createAnalysis(analysis, vetId);
    }

    @PostMapping("/referral")
    public Mono<Referral> createReferral(
            @RequestBody Referral referral,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createReferral(referral, vetId);
    }

    @PostMapping("/treatment")
    public Mono<Treatment> createTreatment(
            @RequestBody Treatment treatment,
            Authentication auth) {

        Long vetId = Long.valueOf(auth.getName());
        return service.createTreatment(treatment, vetId);
    }
}