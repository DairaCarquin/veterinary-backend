package com.vet.security.application.service;

import com.vet.security.application.dto.request.CreateRoleRequest;
import com.vet.security.application.dto.request.UpdateRoleRequest;
import com.vet.security.domain.exception.BadRequestException;
import com.vet.security.domain.exception.ConflictException;
import com.vet.security.domain.exception.ErrorCode;
import com.vet.security.domain.exception.NotFoundException;
import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.port.in.RoleAdminUseCase;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleAdminUseCaseImpl implements RoleAdminUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RoleAdminUseCaseImpl.class);
    
    private final RoleRepositoryPort roleRepositoryPort;

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        logger.info("Creating role: {}", request.name());
        
        // Validar que el nombre no esté vacío
        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }

        // Normalizar nombre (agregar ROLE_ si no lo tiene y convertir a mayúsculas)
        String roleName = Role.normalizeRoleName(request.name());
        
        if (roleName == null) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }

        // Validar que esté en mayúsculas
        if (!roleName.equals(roleName.toUpperCase())) {
            throw new BadRequestException(ErrorCode.ROLE_006);
        }
        
        // Validar convención ROLE_*
        if (!Role.isValidRoleName(roleName)) {
            throw new BadRequestException(ErrorCode.ROLE_007);
        }

        // Validar que no exista
        if (roleRepositoryPort.existsByName(roleName)) {
            throw new ConflictException(ErrorCode.ROLE_003);
        }

        // Crear y guardar el rol
        Role role = Role.builder()
                .name(roleName)
                .build();

        Role savedRole = roleRepositoryPort.save(role);
        logger.info("Role created successfully: {}", savedRole.getName());
        return savedRole;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> listRoles() {
        logger.info("Listing all roles");
        return roleRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        logger.info("Getting role by id: {}", id);
        return roleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_002));
    }

    @Override
    @Transactional
    public Role updateRole(Long id, UpdateRoleRequest request) {
        logger.info("Updating role: {}", id);
        
        // Verificar que el rol existe
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_002));

        // Verificar que no sea un rol protegido
        if (role.isProtected()) {
            throw new BadRequestException(ErrorCode.ROLE_004);
        }

        // Validar el nuevo nombre
        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }

        // Normalizar nombre
        String newName = Role.normalizeRoleName(request.name());
        
        if (newName == null) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }
        
        // Validar que esté en mayúsculas
        if (!newName.equals(newName.toUpperCase())) {
            throw new BadRequestException(ErrorCode.ROLE_006);
        }
        
        // Validar convención ROLE_*
        if (!Role.isValidRoleName(newName)) {
            throw new BadRequestException(ErrorCode.ROLE_007);
        }

        // Verificar que el nuevo nombre no esté en uso (si cambió)
        if (!role.getName().equals(newName) && roleRepositoryPort.existsByName(newName)) {
            throw new ConflictException(ErrorCode.ROLE_003);
        }

        // Actualizar y guardar
        role.setName(newName);
        Role updatedRole = roleRepositoryPort.save(role);
        
        logger.info("Role updated successfully: {}", updatedRole.getName());
        return updatedRole;
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        logger.info("Deleting role: {}", id);
        
        // Verificar que el rol existe
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_002));

        // Verificar que no sea un rol protegido
        if (role.isProtected()) {
            throw new BadRequestException(ErrorCode.ROLE_004);
        }

        // Verificar que no esté en uso
        if (roleRepositoryPort.isRoleInUse(id)) {
            throw new BadRequestException(ErrorCode.ROLE_005);
        }

        // Eliminar el rol
        roleRepositoryPort.deleteById(id);
        logger.info("Role deleted successfully: {}", id);
    }
}
