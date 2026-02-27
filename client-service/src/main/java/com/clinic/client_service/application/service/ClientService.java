package com.clinic.client_service.application.service;

import java.time.LocalDateTime;

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

    public Mono<Long> countClients() {
        return repository.count();
    }

    public Mono<Client> update(Long id, Client updated) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setPhone(updated.getPhone());
                    existing.setEmail(updated.getEmail());
                    existing.setDni(updated.getDni());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                });
    }

    public Mono<Void> toggleEnabled(Long id, boolean enabled) {
        return repository.findById(id)
                .flatMap(client -> {
                    client.setEnabled(enabled);
                    client.setUpdatedAt(LocalDateTime.now());
                    return repository.save(client);
                })
                .then();
    }

    public Flux<Client> findAll(String name, String dni, int page, int size) {
        int offset = page * size;
        return repository.findAll(name, dni, size, offset);
    }
}