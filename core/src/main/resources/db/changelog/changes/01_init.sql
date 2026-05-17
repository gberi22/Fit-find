CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ai_history_seq START WITH 1 INCREMENT BY 1;

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

CREATE TABLE IF NOT EXISTS system_configuration
(
    id                            BIGSERIAL PRIMARY KEY,
    token_validity_minutes        BIGINT,
    created_at                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_configuration (id, token_validity_minutes)
SELECT 1, 60
WHERE NOT EXISTS (SELECT 1 FROM system_configuration);

CREATE TABLE IF NOT EXISTS ai_history (
    id          BIGSERIAL PRIMARY KEY,
    client_id   BIGINT NOT NULL REFERENCES client(id),
    request     JSONB NOT NULL,
    response    JSONB NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
