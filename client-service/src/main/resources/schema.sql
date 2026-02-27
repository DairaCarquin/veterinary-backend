CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(50),
    dni VARCHAR(20) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);