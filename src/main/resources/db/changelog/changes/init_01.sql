CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS client (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255),
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    citizenship VARCHAR(100),
    status      VARCHAR(50),
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
