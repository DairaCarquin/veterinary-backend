package com.vet.pet.application.service;

import com.vet.pet.application.dto.request.CreatePetRequest;
import com.vet.pet.application.dto.request.UpdatePetRequest;
import com.vet.pet.application.dto.response.PetResponse;
import com.vet.pet.domain.model.Pet;
import com.vet.pet.domain.port.in.PetUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetUseCaseImpl {

    private final PetUseCase petUseCase;

    public PetResponse createPet(CreatePetRequest request, String userRole) {
        Pet pet = Pet.builder()
                .clientId(request.getClientId())
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .age(request.getAge())
                .build();

        Pet created = petUseCase.createPet(pet, userRole);
        return toPetResponse(created);
    }

    public PetResponse getPetById(Long id) {
        return petUseCase.getPetById(id)
                .map(this::toPetResponse)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public List<PetResponse> getAllPets() {
        return petUseCase.getAllPets().stream()
                .map(this::toPetResponse)
                .toList();
    }

    public PetResponse updatePet(Long id, UpdatePetRequest request) {
        Pet pet = Pet.builder()
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .build();

        return petUseCase.updatePet(id, pet)
                .map(this::toPetResponse)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public PetResponse deletePet(Long id) {
        return petUseCase.deletePet(id)
                .map(this::toPetResponse)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    private PetResponse toPetResponse(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .clientId(pet.getClientId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .status(pet.getStatus())
                .build();
    }

}
