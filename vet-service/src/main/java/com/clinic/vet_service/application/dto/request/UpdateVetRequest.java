package com.clinic.vet_service.application.dto.request;

import lombok.Data;

@Data
public class UpdateVetRequest {

    private String name;
    private String specialty;
    private String licenseNumber;
    private String email;
}