package com.clinic.medical_history_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinic.medical_history_service.domain.model.Analysis;
import com.clinic.medical_history_service.domain.model.Diagnosis;
import com.clinic.medical_history_service.domain.model.MedicalCase;
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
            String role,
            Long userId,
            Long appointmentId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        if ("CLIENT".equals(role)) {
            return resolveClientId(userId)
                    .flatMap(clientId -> buildCaseResponse(appointmentId, clientId, petId, vetFilter, page, size, offset));
        }

        Long clientFilter = null;
        return buildCaseResponse(appointmentId, clientFilter, petId, vetFilter, page, size, offset);
    }

    private Mono<Map<String, Object>> buildCaseResponse(Long appointmentId,
            Long clientFilter,
            Long petId,
            Long vetFilter,
            int page,
            int size,
            int offset) {
        return medicalCaseRepository.search(
                appointmentId,
                clientFilter,
                petId,
                vetFilter,
                size,
                offset)
                .collectList()
                .zipWith(medicalCaseRepository.countFiltered(
                        appointmentId,
                        clientFilter,
                        petId,
                        vetFilter))
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
            String role,
            Long userId,
            Long caseId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        return resolveClientFilter(role, userId)
                .flatMap(clientFilter -> diagnosisRepository.search(caseId, clientFilter.orElse(null), petId, vetFilter, size, offset)
                .collectList()
                .zipWith(diagnosisRepository.countFiltered(caseId, clientFilter.orElse(null), petId, vetFilter))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                }));
    }

    public Mono<Diagnosis> createDiagnosis(Diagnosis diagnosis, String role, Long userId) {
        return getAccessibleCase(diagnosis.getMedicalCaseId(), role, userId)
                .flatMap(caseEntity -> {
                    diagnosis.setPetId(caseEntity.getPetId());
                    diagnosis.setVeterinarianId(resolveEventVeterinarianId(caseEntity, role, userId));
                    diagnosis.setCreatedAt(LocalDateTime.now());
                    return diagnosisRepository.save(diagnosis);
                });
    }

    public Mono<Map<String, Object>> listAnalysis(
            String role,
            Long userId,
            Long caseId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        return resolveClientFilter(role, userId)
                .flatMap(clientFilter -> analysisRepository.search(caseId, clientFilter.orElse(null), petId, vetFilter, size, offset)
                .collectList()
                .zipWith(analysisRepository.countFiltered(caseId, clientFilter.orElse(null), petId, vetFilter))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                }));
    }

    public Mono<Analysis> createAnalysis(Analysis analysis, String role, Long userId) {
        return getAccessibleCase(analysis.getMedicalCaseId(), role, userId)
                .flatMap(caseEntity -> {
                    analysis.setPetId(caseEntity.getPetId());
                    analysis.setVeterinarianId(resolveEventVeterinarianId(caseEntity, role, userId));
                    analysis.setCreatedAt(LocalDateTime.now());
                    return analysisRepository.save(analysis);
                });
    }

    public Mono<Map<String, Object>> listReferral(
            String role,
            Long userId,
            Long caseId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        return resolveClientFilter(role, userId)
                .flatMap(clientFilter -> referralRepository.search(caseId, clientFilter.orElse(null), petId, vetFilter, size, offset)
                .collectList()
                .zipWith(referralRepository.countFiltered(caseId, clientFilter.orElse(null), petId, vetFilter))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                }));
    }

    public Mono<Referral> createReferral(Referral referral, String role, Long userId) {
        return getAccessibleCase(referral.getMedicalCaseId(), role, userId)
                .flatMap(caseEntity -> {
                    referral.setPetId(caseEntity.getPetId());
                    referral.setVeterinarianId(resolveEventVeterinarianId(caseEntity, role, userId));
                    referral.setCreatedAt(LocalDateTime.now());
                    return referralRepository.save(referral);
                });
    }

    public Mono<Map<String, Object>> listTreatment(
            String role,
            Long userId,
            Long caseId,
            Long petId,
            Long veterinarianId,
            int page,
            int size) {

        int offset = page * size;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        return resolveClientFilter(role, userId)
                .flatMap(clientFilter -> treatmentRepository.search(caseId, clientFilter.orElse(null), petId, vetFilter, size, offset)
                .collectList()
                .zipWith(treatmentRepository.countFiltered(caseId, clientFilter.orElse(null), petId, vetFilter))
                .map(tuple -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("data", tuple.getT1());
                    res.put("total", tuple.getT2());
                    res.put("page", page);
                    res.put("size", size);
                    return res;
                }));
    }

    public Mono<Treatment> createTreatment(Treatment treatment, String role, Long userId) {
        return getAccessibleCase(treatment.getMedicalCaseId(), role, userId)
                .flatMap(caseEntity -> {
                    treatment.setPetId(caseEntity.getPetId());
                    treatment.setVeterinarianId(resolveEventVeterinarianId(caseEntity, role, userId));
                    treatment.setCreatedAt(LocalDateTime.now());
                    return treatmentRepository.save(treatment);
                });
    }

    public Mono<Analysis> updateAnalysis(Long id, Analysis updated, String role, Long userId) {
        return analysisRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Analisis no encontrado")))
                .flatMap(existing -> validateCaseAccess(existing.getMedicalCaseId(), role, userId)
                        .then(Mono.defer(() -> {
                            existing.setDescription(updated.getDescription());
                            existing.setResult(updated.getResult());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return analysisRepository.save(existing);
                        })));
    }

    public Mono<Diagnosis> updateDiagnosis(Long id, Diagnosis updated, String role, Long userId) {
        return diagnosisRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Diagnostico no encontrado")))
                .flatMap(existing -> validateCaseAccess(existing.getMedicalCaseId(), role, userId)
                        .then(Mono.defer(() -> {
                            existing.setDiagnosis(updated.getDiagnosis());
                            existing.setObservations(updated.getObservations());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return diagnosisRepository.save(existing);
                        })));
    }

    public Mono<Referral> updateReferral(Long id, Referral updated, String role, Long userId) {
        return referralRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Referencia no encontrada")))
                .flatMap(existing -> validateCaseAccess(existing.getMedicalCaseId(), role, userId)
                        .then(Mono.defer(() -> {
                            existing.setReason(updated.getReason());
                            existing.setReferredTo(updated.getReferredTo());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return referralRepository.save(existing);
                        })));
    }

    public Mono<Treatment> updateTreatment(Long id, Treatment updated, String role, Long userId) {
        return treatmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Tratamiento no encontrado")))
                .flatMap(existing -> validateCaseAccess(existing.getMedicalCaseId(), role, userId)
                        .then(Mono.defer(() -> {
                            existing.setTreatment(updated.getTreatment());
                            existing.setIndications(updated.getIndications());
                            existing.setUpdatedAt(LocalDateTime.now());
                            return treatmentRepository.save(existing);
                        })));
    }

    private Mono<Void> validateCaseAccess(Long caseId, String role, Long userId) {
        return getAccessibleCase(caseId, role, userId).then();
    }

    private Mono<MedicalCase> getAccessibleCase(Long caseId, String role, Long userId) {
        return medicalCaseRepository.findById(caseId)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Caso medico no existe")))
                .flatMap(caseEntity -> {
                    if ("ADMIN".equals(role)) {
                        return Mono.just(caseEntity);
                    }

                    if ("CLIENT".equals(role)) {
                        return resolveClientId(userId)
                                .flatMap(clientId -> {
                                    if (!caseEntity.getClientId().equals(clientId)) {
                                        return Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "No autorizado para este historial"));
                                    }

                                    return Mono.just(caseEntity);
                                });
                    }

                    if ("VETERINARY".equals(role) && !caseEntity.getVeterinarianId().equals(userId)) {
                        return Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "No autorizado para este caso"));
                    }

                    return Mono.just(caseEntity);
                });
    }

    private Long resolveEventVeterinarianId(MedicalCase caseEntity, String role, Long userId) {
        return "VETERINARY".equals(role) ? userId : caseEntity.getVeterinarianId();
    }

    private Mono<Optional<Long>> resolveClientFilter(String role, Long userId) {
        if ("CLIENT".equals(role)) {
            return resolveClientId(userId).map(Optional::of);
        }

        return Mono.just(Optional.empty());
    }

    private Mono<Long> resolveClientId(Long userId) {
        return medicalCaseRepository.findClientIdByUserId(userId)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.FORBIDDEN,
                        "No existe un cliente asociado al usuario autenticado")));
    }
}
