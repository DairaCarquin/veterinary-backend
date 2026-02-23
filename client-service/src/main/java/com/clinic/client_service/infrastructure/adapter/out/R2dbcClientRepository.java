package com.clinic.client_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.clinic.client_service.domain.model.Client;

@Repository
public interface R2dbcClientRepository extends ReactiveCrudRepository<Client, Long> {
}