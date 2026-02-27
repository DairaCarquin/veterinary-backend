package com.clinic.client_service.application.dto;

import lombok.Data;

@Data
public class ClientRequest {
public Long userId;
private String username; 
private String email;     
private String phone;
}
