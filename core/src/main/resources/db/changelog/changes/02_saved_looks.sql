CREATE TABLE IF NOT EXISTS saved_looks (
    client_id BIGINT NOT NULL REFERENCES client(id),
    look_id   BIGINT NOT NULL REFERENCES looks(id) ON DELETE CASCADE,
    PRIMARY KEY (client_id, look_id)
);
