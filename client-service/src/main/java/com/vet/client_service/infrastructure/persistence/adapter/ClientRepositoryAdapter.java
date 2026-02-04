package com.vet.client_service.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.vet.client_service.domain.model.Client;
import com.vet.client_service.domain.port.out.ClientRepositoryPort;
import com.vet.client_service.infrastructure.persistence.entity.ClientEntity;
import com.vet.client_service.infrastructure.persistence.repository.ClientJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    private final ClientJpaRepository repository;

    @Override
    public Client save(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setUserId(client.getUserId());
        entity.setName(client.getName());
        entity.setEmail(client.getEmail());
        entity.setDni(client.getDni());
        entity.setPhone(client.getPhone());
        entity.setPetIds(client.getPetIds());
        ClientEntity saved = repository.save(entity);

        client.setId(saved.getId());
        return client;
    }

    @Override
    public List<Client> findAll() {
        return repository.findAll().stream().map(entity -> {
            Client c = new Client();
            c.setId(entity.getId());
            c.setName(entity.getName());
            c.setEmail(entity.getEmail());
            c.setPhone(entity.getPhone());
            c.setPetIds(entity.getPetIds());
            return c;
        }).toList();
    }
}