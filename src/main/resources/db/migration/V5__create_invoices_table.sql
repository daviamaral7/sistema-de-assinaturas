CREATE TABLE invoices
(
    id              BIGSERIAL PRIMARY KEY,

    subscription_id BIGINT         NOT NULL,

    amount          NUMERIC(10, 2) NOT NULL,

    due_date        DATE           NOT NULL,
    paid_at         TIMESTAMP,

    status          VARCHAR(30)    NOT NULL,

    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP      NOT NULL,

    CONSTRAINT fk_invoices_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscriptions (id)
);

CREATE INDEX idx_invoices_subscription_id ON invoices (subscription_id);
CREATE INDEX idx_invoices_status ON invoices (status);
CREATE INDEX idx_invoices_due_date ON invoices (due_date);