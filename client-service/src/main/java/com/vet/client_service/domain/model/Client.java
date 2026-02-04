package com.vet.client_service.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class Client {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String dni;
    private String phone;
    private List<Long> petIds;
}