CREATE TABLE IF NOT EXISTS tcc_mensagens
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tcc_id     UUID            NOT NULL REFERENCES tccs (id) ON DELETE CASCADE,
    autor_id   UUID            NOT NULL REFERENCES usuarios (id),
    conteudo   TEXT            NOT NULL,
    criado_em  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tcc_mensagens_tcc_id ON tcc_mensagens (tcc_id);
CREATE INDEX IF NOT EXISTS idx_tcc_mensagens_criado_em ON tcc_mensagens (criado_em);

