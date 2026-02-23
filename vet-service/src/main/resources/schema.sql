CREATE TABLE IF NOT EXISTS veterinarians (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    username VARCHAR(150),
    name VARCHAR(150),
    specialty VARCHAR(150),
    license_number VARCHAR(100),
    email VARCHAR(150),
    available BOOLEAN DEFAULT false
);