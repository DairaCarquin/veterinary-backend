package com.vet.client_service.application.dto.request;

import java.util.List;

public record RegisterClientRequest(
        String name,
        String email,
        String dni,
        String phone,
        List<Long> petIds
) {}