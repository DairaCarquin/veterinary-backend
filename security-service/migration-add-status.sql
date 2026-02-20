-- ================================================
-- MIGRACIÓN: Agregar campo status a users
-- ================================================


ALTER TABLE users 
ADD COLUMN IF NOT EXISTS status INT NOT NULL DEFAULT 1;

-- Crear índice para mejorar consultas por status
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- Actualizar usuarios existentes para que tengan status = 1
UPDATE users SET status = 1 WHERE status IS NULL OR status = 0;

-- Comentarios para documentación
COMMENT ON COLUMN users.status IS '1 = activo, 0 = inactivo (borrado lógico)';

-- ================================================
-- Verificación
-- ================================================

-- Ver estructura actualizada de la tabla
\d users

-- Verificar que todos los usuarios tienen status
SELECT id, username, enabled, status, created_at 
FROM users 
LIMIT 10;
