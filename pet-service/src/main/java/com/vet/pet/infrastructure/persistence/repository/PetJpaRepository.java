package com.vet.pet.infrastructure.persistence.repository;

import com.vet.pet.infrastructure.persistence.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetJpaRepository extends JpaRepository<PetEntity, Long> {

}
