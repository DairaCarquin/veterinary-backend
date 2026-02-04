package com.vet.client_service.application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vet.client_service.application.dto.request.RegisterClientRequest;
import com.vet.client_service.application.service.ClientService;
import com.vet.client_service.domain.model.Client;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @PostMapping("/register")
    public Client registerClient(@RequestBody RegisterClientRequest request) {
        return service.registerClient(request);
    }

    @GetMapping
    public List<Client> list() {
        return service.list();
    }
}