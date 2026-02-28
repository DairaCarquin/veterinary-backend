CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    reason TEXT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);