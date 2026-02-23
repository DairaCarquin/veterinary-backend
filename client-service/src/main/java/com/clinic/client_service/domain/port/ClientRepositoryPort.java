package com.clinic.client_service.domain.port;

import com.clinic.client_service.domain.model.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepositoryPort {

    Mono<Client> save(Client client);

    Mono<Client> findById(Long id);

    Flux<Client> findAll();
}