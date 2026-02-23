package com.clinic.client_service.application.service;

import org.springframework.stereotype.Service;

import com.clinic.client_service.domain.model.Client;
import com.clinic.client_service.domain.port.ClientRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepositoryPort repository;

    public Mono<Client> create(Client client) {
        return repository.save(client);
    }

    public Flux<Client> findAll() {
        return repository.findAll();
    }
}