package com.clinic.client_service.infrastructure.adapter.in;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.client_service.application.dto.ApiResponse;
import com.clinic.client_service.application.service.ClientService;
import com.clinic.client_service.domain.model.Client;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Mono<ApiResponse<List<Client>>> getAll() {
        return service.findAll()
                .collectList()
                .map(list -> ApiResponse.<List<Client>>builder()
                        .status(200)
                        .description("Lista de clientes")
                        .data(list)
                        .build());
    }
}