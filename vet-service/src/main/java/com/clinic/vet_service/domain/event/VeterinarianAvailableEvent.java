package com.clinic.vet_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeterinarianAvailableEvent {

    private Long id;
    private String name;
    private String specialty;
}