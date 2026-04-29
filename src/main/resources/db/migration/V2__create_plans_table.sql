CREATE TABLE plans
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100)   NOT NULL UNIQUE,
    price        NUMERIC(10, 2) NOT NULL,
    max_projects INTEGER,
    active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);