CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,
    dni VARCHAR(20),
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE RESTRICT
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS dni VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username_unique ON users (username);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_dni_unique ON users (dni) WHERE dni IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone_unique ON users (phone) WHERE phone IS NOT NULL;

INSERT INTO roles (name) VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('CLIENT')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('VETERINARY')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, password, role_id, dni, phone, enabled)
SELECT
    'admin@masterdog.com',
    '$2a$10$S5eq4uNZeqhwCTvqH7YAh.AZ9QJRwajYopjuiEDvorHcF8swRQgX6',
    r.id,
    '00000000',
    '900000000',
    TRUE
FROM roles r
WHERE r.name = 'ADMIN'
ON CONFLICT (username) DO NOTHING;
