package com.vet.pet.domain.port.in;

import com.vet.pet.domain.model.Pet;
import java.util.List;
import java.util.Optional;

public interface PetUseCase {

    Pet createPet(Pet pet, String userRole);

    Optional<Pet> getPetById(Long id);

    List<Pet> getAllPets();

    Optional<Pet> updatePet(Long id, Pet pet);

    Optional<Pet> deletePet(Long id);

}
