package com.vet.client_service.domain.port.out;

public interface SecurityClientPort {
    Long registerClientUser(String email, String dni);
}
