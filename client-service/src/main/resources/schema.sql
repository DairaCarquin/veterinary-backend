CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(50)
);