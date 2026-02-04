package com.vet.client_service.domain.port.out;

public interface AuthServicePort {
    Long registerClientUser(String email, String dni);
}