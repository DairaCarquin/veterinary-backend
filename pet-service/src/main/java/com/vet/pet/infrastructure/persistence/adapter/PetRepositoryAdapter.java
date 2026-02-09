package com.vet.pet.infrastructure.persistence.adapter;

import com.vet.pet.domain.model.Pet;
import com.vet.pet.domain.port.in.PetUseCase;
import com.vet.pet.infrastructure.kafka.PetEventProducer;
import com.vet.pet.infrastructure.persistence.entity.PetEntity;
import com.vet.pet.infrastructure.persistence.repository.PetJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetRepositoryAdapter implements PetUseCase {

    private final PetJpaRepository petJpaRepository;
    private final PetEventProducer petEventProducer;

    @Override
    public Pet createPet(Pet pet, String userRole) {
        // Validate user role
        if (!isAdminOrVeterinarian(userRole)) {
            throw new IllegalArgumentException("Only ADMIN or VETERINARIAN can create pets");
        }

        // Set default clientId if not provided
        if (pet.getClientId() == null) {
            pet.setClientId(1L);
        }

        // Set initial status
        pet.setStatus(1);

        PetEntity petEntity = fromDomain(pet);
        PetEntity savedEntity = petJpaRepository.save(petEntity);

        Pet savedPet = toDomain(savedEntity);

        // Publish event
        petEventProducer.publishPetRegistered(savedPet);

        return savedPet;
    }

    @Override
    public Optional<Pet> getPetById(Long id) {
        return petJpaRepository.findById(id)
                .filter(entity -> entity.getStatus() == 1)
                .map(this::toDomain);
    }

    @Override
    public List<Pet> getAllPets() {
        return petJpaRepository.findAll().stream()
                .filter(entity -> entity.getStatus() == 1)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Pet> updatePet(Long id, Pet pet) {
        return petJpaRepository.findById(id).map(existingEntity -> {
            // Only allow updates to specific fields
            if (pet.getName() != null) {
                existingEntity.setName(pet.getName());
            }
            if (pet.getBreed() != null) {
                existingEntity.setBreed(pet.getBreed());
            }
            if (pet.getAge() != null) {
                existingEntity.setAge(pet.getAge());
            }

            PetEntity updatedEntity = petJpaRepository.save(existingEntity);
            return toDomain(updatedEntity);
        });
    }

    @Override
    public Optional<Pet> deletePet(Long id) {
        return petJpaRepository.findById(id).map(entity -> {
            entity.setStatus(0);
            PetEntity deletedEntity = petJpaRepository.save(entity);
            return toDomain(deletedEntity);
        });
    }

    private Pet toDomain(PetEntity entity) {
        return Pet.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .name(entity.getName())
                .species(entity.getSpecies())
                .breed(entity.getBreed())
                .age(entity.getAge())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .status(entity.getStatus())
                .build();
    }

    private PetEntity fromDomain(Pet domain) {
        PetEntity.PetEntityBuilder builder = PetEntity.builder()
                .clientId(domain.getClientId())
                .name(domain.getName())
                .species(domain.getSpecies())
                .breed(domain.getBreed())
                .age(domain.getAge())
                .status(domain.getStatus());

        if (domain.getId() != null) {
            builder.id(domain.getId());
        }
        if (domain.getCreatedAt() != null) {
            builder.createdAt(domain.getCreatedAt());
        }
        if (domain.getUpdatedAt() != null) {
            builder.updatedAt(domain.getUpdatedAt());
        }

        return builder.build();
    }

    private boolean isAdminOrVeterinarian(String role) {
        return "ADMIN".equals(role) || "VETERINARIAN".equals(role);
    }

}
