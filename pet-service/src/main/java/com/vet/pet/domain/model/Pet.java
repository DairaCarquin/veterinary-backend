package com.vet.pet.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pet {

    private Long id;
    private Long clientId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer status;

}
