package com.clinic.client_service.infrastructure.adapter.in;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<Long>> count() {
        return service.countClients()
                .map(total -> ApiResponse.<Long>builder()
                        .status(200)
                        .description("Total de clientes activos")
                        .data(total)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<List<Client>>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dni,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.findAll(name, dni, page, size)
                .collectList()
                .map(list -> ApiResponse.<List<Client>>builder()
                        .status(200)
                        .description("Lista paginada de clientes")
                        .data(list)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<Client>> update(
            @PathVariable Long id,
            @RequestBody Client client) {
        return service.update(id, client)
                .map(updated -> ApiResponse.<Client>builder()
                        .status(200)
                        .description("Cliente actualizado")
                        .data(updated)
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<Void>> toggle(
            @PathVariable Long id,
            @RequestParam boolean enabled) {

        return service.toggleEnabled(id, enabled)
                .thenReturn(ApiResponse.<Void>builder()
                        .status(200)
                        .description(enabled ? "Cliente habilitado" : "Cliente deshabilitado")
                        .build());
    }
}