package com.vet.client_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vet.client_service.application.dto.request.RegisterClientRequest;
import com.vet.client_service.domain.model.Client;
import com.vet.client_service.domain.port.out.ClientRepositoryPort;
import com.vet.client_service.domain.port.out.SecurityClientPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepositoryPort repository;
    private final SecurityClientPort securityClientPort;

    public Client registerClient(RegisterClientRequest request) {

        Long userId = securityClientPort.registerClientUser(
                request.email(),
                request.dni()
        );

        Client client = new Client();
        client.setUserId(userId);
        client.setName(request.name());
        client.setEmail(request.email());
        client.setDni(request.dni());
        client.setPhone(request.phone());

        return repository.save(client);
    }

    public List<Client> list() {
        return repository.findAll();
    }
}
