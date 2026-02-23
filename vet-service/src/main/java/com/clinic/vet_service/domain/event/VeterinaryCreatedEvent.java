package com.clinic.vet_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeterinaryCreatedEvent {

    private Long id;
    private String username;
    private String role;
}