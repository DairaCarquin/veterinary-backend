package com.vet.security.domain.port.in;

import com.vet.security.application.dto.request.CreateRoleRequest;
import com.vet.security.application.dto.request.UpdateRoleRequest;
import com.vet.security.domain.exception.model.Role;

import java.util.List;

public interface RoleAdminUseCase {
    Role createRole(CreateRoleRequest request);
    List<Role> listRoles();
    Role getRoleById(Long id);
    Role updateRole(Long id, UpdateRoleRequest request);
    void deleteRole(Long id);
}
