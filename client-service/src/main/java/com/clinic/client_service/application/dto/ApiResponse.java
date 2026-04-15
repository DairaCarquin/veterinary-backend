package com.clinic.client_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private String description;
    private T data;
    private Long total;
    private Integer page;
    private Integer size;
}
