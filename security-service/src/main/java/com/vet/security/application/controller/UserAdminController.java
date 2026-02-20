package com.vet.security.application.controller;

import com.vet.security.application.dto.request.CreateUserRequest;
import com.vet.security.application.dto.request.UpdatePasswordRequest;
import com.vet.security.application.dto.request.UpdateUserRequest;
import com.vet.security.application.dto.response.ApiResponse;
import com.vet.security.application.dto.response.PageResponse;
import com.vet.security.application.dto.response.UserResponse;
import com.vet.security.domain.exception.model.User;
import com.vet.security.domain.port.in.UserAdminUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para gestión de usuarios por administradores
 * Rutas bajo /auth/users según especificación
 */
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserAdminController {
    
    private final UserAdminUseCase userAdminUseCase;

    /**
     * Crear usuario
     * POST /auth/users
     * Solo ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request
    ) {
        User user = userAdminUseCase.createUser(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Usuario creado exitosamente",
                        UserResponse.from(user)
                ));
    }

    /**
     * Listar usuarios
     * GET /auth/users
     * Solo ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> listUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<User> userPage = userAdminUseCase.listUsers(username, enabled, role, pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::from);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Usuarios obtenidos exitosamente",
                PageResponse.from(responsePage)
        ));
    }

    /**
     * Obtener usuario por ID
     * GET /auth/users/{id}
     * ADMIN o el propio usuario
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        
        Optional<Long> requesterId = isAdmin ? 
                Optional.empty() : 
                Optional.of(Long.parseLong(authentication.getName()));
        
        User user = userAdminUseCase.getUserById(id, requesterId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(
                "Usuario obtenido exitosamente",
                UserResponse.from(user)
        ));
    }

    /**
     * Actualizar usuario
     * PUT /auth/users/{id}
     * Solo ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        User user = userAdminUseCase.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Usuario actualizado exitosamente",
                UserResponse.from(user)
        ));
    }

    /**
     * Borrado lógico de usuario
     * DELETE /auth/users/{id}
     * Solo ADMIN
     * Actualiza: status=0, enabled=false
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        userAdminUseCase.disableUser(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Usuario deshabilitado exitosamente (status=0, enabled=false)"
        ));
    }

    /**
     * Actualizar contraseña
     * PUT /auth/users/{userId}/password
     * Cualquier usuario autenticado puede cambiar su propia contraseña
     * 
     * Si el usuario tiene rol CLIENTE y proporciona newDni,
     * se actualiza también en client-service
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long userId,
            @RequestBody @Valid UpdatePasswordRequest request,
            Authentication authentication
    ) {
        // Verificar que el usuario solo pueda cambiar su propia contraseña
        // a menos que sea ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            Long authenticatedUserId = Long.parseLong(authentication.getName());
            if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.ok(ApiResponse.error(
                        "Solo puedes cambiar tu propia contraseña"
                ));
            }
        }
        
        userAdminUseCase.updatePassword(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Contraseña actualizada exitosamente"
        ));
    }
}
