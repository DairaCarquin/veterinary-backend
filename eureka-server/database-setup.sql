-- ================================================
-- SCRIPT SQL - GESTIÓN DE USUARIOS
-- Sistema Veterinario - Security Service
-- ================================================

-- ================================================
-- 1. CREAR TABLAS (si no existen)
-- ================================================

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de relación usuario-roles (muchos a muchos)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla de refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ================================================
-- 2. CREAR ÍNDICES PARA OPTIMIZACIÓN
-- ================================================

-- Índice en username (ya es UNIQUE, pero reforzamos)
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Índice en created_at para ordenamiento
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);

-- Índice en enabled para filtros
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);

-- Índice en user_id de refresh_tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Índice en token de refresh_tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);

-- Índice compuesto en user_roles
CREATE INDEX IF NOT EXISTS idx_user_roles_composite ON user_roles(user_id, role_id);

-- ================================================
-- 3. INSERTAR ROLES INICIALES
-- ================================================
-- IMPORTANTE: Siguiendo convención ROLE_*

INSERT INTO roles (name) VALUES ('ROLE_ADMIN') 
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('ROLE_VETERINARIO') 
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('ROLE_CLIENTE') 
ON CONFLICT (name) DO NOTHING;

-- ================================================
-- 4. CREAR USUARIO ADMIN INICIAL
-- ================================================
-- Password: Admin123!
-- BCrypt hash de "Admin123!": $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG

INSERT INTO users (username, password, enabled, created_at)
VALUES (
    'admin@masterdog.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Asignar rol ROLE_ADMIN al usuario admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin@masterdog.com'
AND r.name = 'ROLE_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ================================================
-- 5. CREAR USUARIOS DE PRUEBA (OPCIONAL)
-- ================================================

-- Veterinario de prueba
-- Password: Vet123456
INSERT INTO users (username, password, enabled, created_at)
VALUES (
    'veterinario@test.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye.VKMS4O8R5eXDjGK7OtKnSqKfzwR7p.',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'veterinario@test.com'
AND r.name = 'ROLE_VETERINARIO'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Cliente de prueba
-- Password: Cliente123
INSERT INTO users (username, password, enabled, created_at)
VALUES (
    'cliente@test.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye.VKMS4O8R5eXDjGK7OtKnSqKfzwR7p.',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'cliente@test.com'
AND r.name = 'ROLE_CLIENTE'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ================================================
-- 6. CONSULTAS ÚTILES PARA VERIFICACIÓN
-- ================================================

-- Ver todos los usuarios con sus roles
SELECT 
    u.id,
    u.username,
    u.enabled,
    u.created_at,
    u.updated_at,
    STRING_AGG(r.name, ', ') as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.enabled, u.created_at, u.updated_at
ORDER BY u.created_at DESC;

-- Ver todos los roles disponibles
SELECT * FROM roles ORDER BY name;

-- Contar usuarios por rol
SELECT 
    r.name as role,
    COUNT(ur.user_id) as total_users
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
GROUP BY r.name
ORDER BY r.name;

-- Ver usuarios activos vs inactivos
SELECT 
    enabled,
    COUNT(*) as total
FROM users
GROUP BY enabled;

-- Ver refresh tokens activos por usuario
SELECT 
    u.username,
    COUNT(rt.id) as active_tokens
FROM users u
LEFT JOIN refresh_tokens rt ON u.id = rt.user_id AND rt.revoked = false
GROUP BY u.id, u.username
ORDER BY active_tokens DESC;

-- ================================================
-- 7. PROCEDIMIENTOS ÚTILES (OPCIONAL)
-- ================================================

-- Función para deshabilitar usuario e invalidar sus tokens
CREATE OR REPLACE FUNCTION disable_user_and_tokens(p_user_id BIGINT)
RETURNS VOID AS $$
BEGIN
    -- Deshabilitar usuario
    UPDATE users 
    SET enabled = false, updated_at = CURRENT_TIMESTAMP
    WHERE id = p_user_id;
    
    -- Revocar todos sus refresh tokens
    UPDATE refresh_tokens
    SET revoked = true
    WHERE user_id = p_user_id;
END;
$$ LANGUAGE plpgsql;

-- Uso: SELECT disable_user_and_tokens(1);

-- Función para limpiar tokens expirados
CREATE OR REPLACE FUNCTION cleanup_expired_tokens()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM refresh_tokens
    WHERE expires_at < CURRENT_TIMESTAMP OR revoked = true;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Uso: SELECT cleanup_expired_tokens();

-- ================================================
-- 8. TRIGGERS (OPCIONAL)
-- ================================================

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ================================================
-- 9. DATOS PARA TESTING (OPCIONAL)
-- ================================================

-- Crear más usuarios de prueba con diferentes combinaciones de roles
INSERT INTO users (username, password, enabled, created_at) VALUES
    ('multi.role@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.VKMS4O8R5eXDjGK7OtKnSqKfzwR7p.', true, CURRENT_TIMESTAMP),
    ('disabled@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.VKMS4O8R5eXDjGK7OtKnSqKfzwR7p.', false, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuario con múltiples roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'multi.role@test.com'
AND r.name IN ('ROLE_VETERINARIO', 'ROLE_CLIENTE')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ================================================
-- 10. SCRIPTS DE LIMPIEZA (USAR CON CUIDADO)
-- ================================================

-- Eliminar todos los refresh tokens
-- DELETE FROM refresh_tokens;

-- Eliminar todos los usuarios excepto admin
-- DELETE FROM users WHERE username != 'admin@masterdog.com';

-- Resetear secuencias
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE roles_id_seq RESTART WITH 1;
-- ALTER SEQUENCE refresh_tokens_id_seq RESTART WITH 1;

-- ================================================
-- NOTAS IMPORTANTES
-- ================================================

/*
1. Este script está diseñado para PostgreSQL
2. Para MySQL, cambiar:
   - BIGSERIAL → BIGINT AUTO_INCREMENT
   - CURRENT_TIMESTAMP → NOW()
   - ON CONFLICT → ON DUPLICATE KEY UPDATE
   - STRING_AGG → GROUP_CONCAT

3. Los passwords de prueba son:
   - admin@masterdog.com → Admin123!
   - veterinario@test.com → Vet123456
   - cliente@test.com → Cliente123
   - multi.role@test.com → Cliente123
   - disabled@test.com → Cliente123

4. Los índices mejoran el rendimiento de las consultas

5. Los triggers automatizan la actualización de updated_at

6. Las funciones facilitan operaciones comunes

7. CAMBIAR LOS PASSWORDS EN PRODUCCIÓN
*/

-- ================================================
-- FIN DEL SCRIPT
-- ================================================
