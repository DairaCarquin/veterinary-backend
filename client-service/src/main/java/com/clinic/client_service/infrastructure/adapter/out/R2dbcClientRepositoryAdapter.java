package com.clinic.client_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.client_service.domain.model.Client;
import com.clinic.client_service.domain.port.ClientRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcClientRepositoryAdapter implements ClientRepositoryPort {

    private final R2dbcClientRepository repository;

    @Override
    public Mono<Client> save(Client client) {
        return repository.save(client);
    }

    @Override
    public Mono<Client> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Client> findAll() {
        return repository.findAll();
    }
}