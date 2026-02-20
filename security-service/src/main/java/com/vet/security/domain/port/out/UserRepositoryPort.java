package com.vet.security.domain.port.out;

import com.vet.security.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryPort {
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User save(User user);
    Page<User> findAllFiltered(
            String username,
            Boolean enabled,
            String role,
            Pageable pageable);
}
