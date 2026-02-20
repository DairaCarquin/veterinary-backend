package com.vet.security.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vet.security.infrastructure.persistence.entity.RefreshTokenEntity;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
