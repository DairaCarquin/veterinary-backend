package com.clinic.auth_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private Long id;

    private String username;
    private String password;
    private Long roleId;
    private String dni;
    private String phone;

    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
