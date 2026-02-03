package com.vet.client_service.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vet.client_service.infrastructure.persistence.entity.ClientEntity;

public interface ClientJpaRepository
        extends JpaRepository<ClientEntity, Long> {
}