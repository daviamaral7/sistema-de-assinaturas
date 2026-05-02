CREATE TABLE projects
(
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,

    CONSTRAINT fk_projects_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers (id)
);

CREATE INDEX idx_projects_customer_id ON projects (customer_id);