package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Referral;
import reactor.core.publisher.Flux;

public interface R2dbcReferralRepository extends ReactiveCrudRepository<Referral, Long> {

    Flux<Referral> findByMedicalCaseId(Long medicalCaseId);

    Flux<Referral> findByVeterinarianId(Long veterinarianId);

    Flux<Referral> findByPetId(Long petId);
}