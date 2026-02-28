package com.clinic.medical_history_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    public Mono<Map<String, Object>> findAll(
            Long appointmentId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;

        return medicalCaseRepository.search(
                appointmentId,
                petId,
                veterinarianId,
                size,
                offset)
                .collectList()
                .zipWith(medicalCaseRepository.countFiltered(
                        appointmentId,
                        petId,
                        veterinarianId))
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    public Mono<Map<String, Object>> listDiagnosis(
            Long caseId,
            Long petId,
            Long vetId,
            int page,
            int size) {

        int offset = page * size;

        return diagnosisRepository.search(caseId, petId, vetId, size, offset)
                .collectList()
                .zipWith(diagnosisRepository.countFiltered(caseId, petId, vetId))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                });
    }

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

    public Mono<Map<String, Object>> listAnalysis(
            Long caseId,
            Long petId,
            Long vetId,
            int page,
            int size) {

        int offset = page * size;

        return analysisRepository.search(caseId, petId, vetId, size, offset)
                .collectList()
                .zipWith(analysisRepository.countFiltered(caseId, petId, vetId))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                });
    }

    public Mono<Analysis> createAnalysis(Analysis analysis, Long vetId) {

        return validateCaseAndVet(analysis.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    analysis.setVeterinarianId(vetId);
                    analysis.setCreatedAt(LocalDateTime.now());
                    return analysisRepository.save(analysis);
                }));
    }

    public Mono<Map<String, Object>> listReferral(
            Long caseId,
            Long petId,
            Long vetId,
            int page,
            int size) {

        int offset = page * size;

        return referralRepository.search(caseId, petId, vetId, size, offset)
                .collectList()
                .zipWith(referralRepository.countFiltered(caseId, petId, vetId))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                });
    }

    public Mono<Referral> createReferral(Referral referral, Long vetId) {

        return validateCaseAndVet(referral.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    referral.setVeterinarianId(vetId);
                    referral.setCreatedAt(LocalDateTime.now());
                    return referralRepository.save(referral);
                }));
    }

    public Mono<Map<String, Object>> listTreatment(
            Long caseId,
            Long petId,
            Long vetId,
            int page,
            int size) {

        int offset = page * size;

        return treatmentRepository.search(caseId, petId, vetId, size, offset)
                .collectList()
                .zipWith(treatmentRepository.countFiltered(caseId, petId, vetId))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                });
    }

    public Mono<Treatment> createTreatment(Treatment treatment, Long vetId) {

        return validateCaseAndVet(treatment.getMedicalCaseId(), vetId)
                .then(Mono.defer(() -> {
                    treatment.setVeterinarianId(vetId);
                    treatment.setCreatedAt(LocalDateTime.now());
                    return treatmentRepository.save(treatment);
                }));
    }

    public Mono<Analysis> updateAnalysis(Long id, Analysis updated, Long vetId) {

        return analysisRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Análisis no encontrado")))
                .flatMap(existing -> validateCaseAndVet(existing.getMedicalCaseId(), vetId)
                        .then(Mono.defer(() -> {
                            existing.setDescription(updated.getDescription());
                            existing.setResult(updated.getResult());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return analysisRepository.save(existing);
                        })));
    }

    public Mono<Diagnosis> updateDiagnosis(Long id, Diagnosis updated, Long vetId) {

        return diagnosisRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Diagnóstico no encontrado")))
                .flatMap(existing -> validateCaseAndVet(existing.getMedicalCaseId(), vetId)
                        .then(Mono.defer(() -> {
                            existing.setDiagnosis(updated.getDiagnosis());
                            existing.setObservations(updated.getObservations());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return diagnosisRepository.save(existing);
                        })));
    }

    public Mono<Referral> updateReferral(Long id, Referral updated, Long vetId) {

        return referralRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Referencia no encontrada")))
                .flatMap(existing -> validateCaseAndVet(existing.getMedicalCaseId(), vetId)
                        .then(Mono.defer(() -> {
                            existing.setReason(updated.getReason());
                            existing.setReferredTo(updated.getReferredTo());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return referralRepository.save(existing);
                        })));
    }

    public Mono<Treatment> updateTreatment(Long id, Treatment updated, Long vetId) {

        return treatmentRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Tratamiento no encontrado")))
                .flatMap(existing -> validateCaseAndVet(existing.getMedicalCaseId(), vetId)
                        .then(Mono.defer(() -> {
                            existing.setTreatment(updated.getTreatment());
                            existing.setIndications(updated.getIndications());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return treatmentRepository.save(existing);
                        })));
    }
}