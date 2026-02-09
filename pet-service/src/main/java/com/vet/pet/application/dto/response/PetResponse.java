package com.vet.pet.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PetResponse {

    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    private String name;

    private String species;

    private String breed;

    private Integer age;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    private Integer status;

}
