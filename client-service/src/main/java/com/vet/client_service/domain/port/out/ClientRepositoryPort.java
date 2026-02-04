package com.vet.client_service.domain.port.out;

import java.util.List;

import com.vet.client_service.domain.model.Client;

public interface ClientRepositoryPort {
    Client save(Client client);
    List<Client> findAll();
}