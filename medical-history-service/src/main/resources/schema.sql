CREATE TABLE medical_case (
    id SERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE analysis (
    id SERIAL PRIMARY KEY,
    medical_case_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    result TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnosis (
    id SERIAL PRIMARY KEY,
    medical_case_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    diagnosis TEXT NOT NULL,
    observations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referral (
    id SERIAL PRIMARY KEY,
    medical_case_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    referred_to VARCHAR(255),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE treatment (
    id SERIAL PRIMARY KEY,
    medical_case_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    treatment TEXT NOT NULL,
    indications TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);