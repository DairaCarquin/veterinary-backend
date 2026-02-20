package com.vet.security.application.controller;

import com.vet.security.application.dto.request.CreateRoleRequest;
import com.vet.security.application.dto.request.UpdateRoleRequest;
import com.vet.security.application.dto.response.RoleResponse;
import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.port.in.RoleAdminUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleAdminController {

    private final RoleAdminUseCase roleAdminUseCase;

    /**
     * 2.1 CREAR ROL
     * POST /roles
     * Solo ADMIN
     */
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody CreateRoleRequest request) {
        Role role = roleAdminUseCase.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RoleResponse.from(role));
    }

    /**
     * 2.2 LISTAR ROLES
     * GET /roles
     * Solo ADMIN
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> listRoles() {
        List<Role> roles = roleAdminUseCase.listRoles();
        List<RoleResponse> response = roles.stream()
                .map(RoleResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 2.3 OBTENER ROL POR ID
     * GET /roles/{id}
     * Solo ADMIN
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        Role role = roleAdminUseCase.getRoleById(id);
        return ResponseEntity.ok(RoleResponse.from(role));
    }

    /**
     * 2.4 ACTUALIZAR ROL
     * PUT /roles/{id}
     * Solo ADMIN
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request
    ) {
        Role role = roleAdminUseCase.updateRole(id, request);
        return ResponseEntity.ok(RoleResponse.from(role));
    }

    /**
     * 2.5 ELIMINAR ROL
     * DELETE /roles/{id}
     * Solo ADMIN
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleAdminUseCase.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
