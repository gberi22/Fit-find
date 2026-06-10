CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ai_history_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS products_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS looks_seq START WITH 1 INCREMENT BY 1;

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

CREATE TABLE IF NOT EXISTS products (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    price      VARCHAR(50),
    url        VARCHAR(500) NOT NULL UNIQUE,
    category   VARCHAR(50),
    image_url  VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS looks (
    id              BIGSERIAL PRIMARY KEY,
    client_id       BIGINT NOT NULL REFERENCES client(id),
    gender          VARCHAR(10) NOT NULL,
    size            VARCHAR(10),
    styles          JSONB NOT NULL,
    budget_min      DECIMAL(10, 2),
    budget_max      DECIMAL(10, 2),
    image_mime_type VARCHAR(50),
    image_key       UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    is_published    BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS saved_looks (
    client_id BIGINT NOT NULL REFERENCES client(id),
    look_id   BIGINT NOT NULL REFERENCES looks(id) ON DELETE CASCADE,
    PRIMARY KEY (client_id, look_id)
);

CREATE TABLE IF NOT EXISTS look_products (
    look_id    BIGINT NOT NULL REFERENCES looks(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    PRIMARY KEY (look_id, product_id)
);

CREATE TABLE IF NOT EXISTS look_images (
    look_id BIGINT PRIMARY KEY REFERENCES looks(id) ON DELETE CASCADE,
    image   BYTEA NOT NULL
);
