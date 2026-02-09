package com.vet.pet.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreatePetRequest {

    @JsonProperty("client_id")
    private Long clientId;

    private String name;

    private String species;

    private String breed;

    private Integer age;

}
