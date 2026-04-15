CREATE TABLE IF NOT EXISTS veterinarians (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    username VARCHAR(150),
    name VARCHAR(150),
    last_name VARCHAR(150),
    dni VARCHAR(20),
    specialty VARCHAR(150),
    license_number VARCHAR(100),
    email VARCHAR(150),
    phone VARCHAR(50),
    available BOOLEAN DEFAULT false,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE veterinarians ADD COLUMN IF NOT EXISTS last_name VARCHAR(150);
ALTER TABLE veterinarians ADD COLUMN IF NOT EXISTS dni VARCHAR(20);
ALTER TABLE veterinarians ADD COLUMN IF NOT EXISTS phone VARCHAR(50);
