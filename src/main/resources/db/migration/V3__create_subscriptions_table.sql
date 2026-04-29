CREATE TABLE subscriptions
(
    id                BIGSERIAL PRIMARY KEY,

    customer_id       BIGINT      NOT NULL,
    plan_id           BIGINT      NOT NULL,

    status            VARCHAR(30) NOT NULL,

    start_date        DATE        NOT NULL,
    end_date          DATE,
    next_billing_date DATE        NOT NULL,

    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,

    CONSTRAINT fk_subscriptions_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers (id),

    CONSTRAINT fk_subscriptions_plan
        FOREIGN KEY (plan_id)
            REFERENCES plans (id)
);

CREATE INDEX idx_subscriptions_customer_id ON subscriptions(customer_id);
CREATE INDEX idx_subscriptions_plan_id ON subscriptions(plan_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);