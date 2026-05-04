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
