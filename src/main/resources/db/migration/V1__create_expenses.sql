CREATE TABLE expenses (
    id          BIGSERIAL PRIMARY KEY,
    raw_text    TEXT          NOT NULL,
    merchant    VARCHAR(120),
    amount      NUMERIC(12,2) NOT NULL,
    category    VARCHAR(40)   NOT NULL,
    spent_on    DATE          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_expenses_spent_on ON expenses (spent_on);
CREATE INDEX idx_expenses_category ON expenses (category);
