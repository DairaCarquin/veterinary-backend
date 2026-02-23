package com.clinic.client_service.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {

    @Id
    private Long id;

    private Long userId;

    private String username;

    private String email;

    private String phone;
}