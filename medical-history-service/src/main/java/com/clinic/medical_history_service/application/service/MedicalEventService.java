package com.clinic.medical_history_service.application.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinic.medical_history_service.domain.model.Analysis;
import com.clinic.medical_history_service.domain.model.Diagnosis;
import com.clinic.medical_history_service.domain.model.Referral;
import com.clinic.medical_history_service.domain.model.Treatment;
import com.clinic.medical_history_service.domain.port.MedicalCaseRepositoryPort;
import com.clinic.medical_history_service.infrastructure.adapter.out.R2dbcAnalysisRepository;
import com.clinic.medical_history_service.infrastructure.adapter.out.R2dbcDiagnosisRepository;
import com.clinic.medical_history_service.infrastructure.adapter.out.R2dbcReferralRepository;
import com.clinic.medical_history_service.infrastructure.adapter.out.R2dbcTreatmentRepository;
import com.clinic.medical_history_service.infrastructure.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MedicalEventService {

    private final MedicalCaseRepositoryPort medicalCaseRepository;
    private final R2dbcDiagnosisRepository diagnosisRepository;
    private final R2dbcAnalysisRepository analysisRepository;
    private final R2dbcReferralRepository referralRepository;
    private final R2dbcTreatmentRepository treatmentRepository;

    public Mono<Void> validateCaseAndVet(Long caseId, Long vetId) {

        return medicalCaseRepository.findById(caseId)
                .switchIfEmpty(Mono.error(
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Caso médico no existe")))
                .flatMap(caseEntity -> {
                    if (!caseEntity.getVeterinarianId().equals(vetId)) {
                        return Mono.error(
                                new BusinessException(HttpStatus.FORBIDDEN,
                                        "No autorizado para este caso"));
                    }
                    return Mono.empty();
                });
    }

    public Mono<Diagnosis> createDiagnosis(Diagnosis diagnosis, Long vetId) {

        return validateCaseAndVet(diagnosis.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    diagnosis.setVeterinarianId(vetId);
                    diagnosis.setCreatedAt(LocalDateTime.now());
                    return diagnosisRepository.save(diagnosis);
                }));
    }

    public Mono<Analysis> createAnalysis(Analysis analysis, Long vetId) {

        return validateCaseAndVet(analysis.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    analysis.setVeterinarianId(vetId);
                    analysis.setCreatedAt(LocalDateTime.now());
                    return analysisRepository.save(analysis);
                }));
    }

    public Mono<Referral> createReferral(Referral referral, Long vetId) {

        return validateCaseAndVet(referral.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    referral.setVeterinarianId(vetId);
                    referral.setCreatedAt(LocalDateTime.now());
                    return referralRepository.save(referral);
                }));
    }

    public Mono<Treatment> createTreatment(Treatment treatment, Long vetId) {

        return validateCaseAndVet(treatment.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    treatment.setVeterinarianId(vetId);
                    treatment.setCreatedAt(LocalDateTime.now());
                    return treatmentRepository.save(treatment);
                }));
    }
}