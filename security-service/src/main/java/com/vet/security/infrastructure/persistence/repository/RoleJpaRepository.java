package com.vet.security.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vet.security.infrastructure.persistence.entity.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);
    
    Set<RoleEntity> findByNameIn(Set<String> names);
    
    boolean existsByName(String name);
    
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END " +
           "FROM UserEntity u JOIN u.roles r WHERE r.id = :roleId")
    boolean isRoleInUse(@Param("roleId") Long roleId);
}
