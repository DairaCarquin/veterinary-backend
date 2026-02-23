package com.clinic.client_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private String description;
    private T data;
}