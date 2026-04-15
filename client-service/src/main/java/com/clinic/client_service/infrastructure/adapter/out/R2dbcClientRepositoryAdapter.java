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
    public Mono<Long> count() {
        return repository.countByEnabledTrue();
    }

    @Override
    public Flux<Client> findByEnabledTrue() {
        return repository.findByEnabledTrue();
    }

    @Override
    public Flux<Client> findAll(String name, String dni, String email, int limit, int offset) {
        return repository.findAll(name, dni, email, limit, offset);
    }

    @Override
    public Mono<Long> countFiltered(String name, String dni, String email) {
        return repository.countFiltered(name, dni, email);
    }
}
