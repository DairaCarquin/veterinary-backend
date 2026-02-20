package com.vet.security.domain.port.in;

import com.vet.security.application.dto.request.CreateUserRequest;
import com.vet.security.application.dto.request.UpdatePasswordRequest;
import com.vet.security.application.dto.request.UpdateUserRequest;
import com.vet.security.domain.exception.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserAdminUseCase {
    User createUser(CreateUserRequest request);
    Page<User> listUsers(String username, Boolean enabled, String role, Pageable pageable);
    User getUserById(Long id, Optional<Long> requesterId, boolean isAdmin);
    User updateUser(Long id, UpdateUserRequest request);
    void disableUser(Long id);
    void updatePassword(Long userId, UpdatePasswordRequest request);
}
