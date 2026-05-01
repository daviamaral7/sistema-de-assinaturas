CREATE TABLE payments
(
    id             BIGSERIAL PRIMARY KEY,

    invoice_id     BIGINT         NOT NULL,

    amount         NUMERIC(10, 2) NOT NULL,

    payment_method VARCHAR(30)    NOT NULL,
    status         VARCHAR(30)    NOT NULL,

    paid_at        TIMESTAMP      NOT NULL,

    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP      NOT NULL,

    CONSTRAINT fk_payments_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES invoices (id)
);

CREATE INDEX idx_payments_invoice_id ON payments (invoice_id);
CREATE INDEX idx_payments_status ON payments (status);