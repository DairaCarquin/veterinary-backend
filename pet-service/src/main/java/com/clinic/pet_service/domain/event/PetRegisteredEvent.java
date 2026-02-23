package com.clinic.pet_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetRegisteredEvent {

    private Long id;
    private String name;
    private Long ownerId;
}