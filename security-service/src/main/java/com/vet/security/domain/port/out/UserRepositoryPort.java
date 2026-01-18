package com.vet.security.domain.port.out;

import com.vet.security.domain.model.User;

public interface UserRepositoryPort {
    User findByUsername(String username);
    User save(User user);
    User findById(Long id);
}
