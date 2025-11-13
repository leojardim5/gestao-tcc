CREATE TYPE status_cronograma_etapa AS ENUM ('PENDENTE', 'ENTREGUE', 'APROVADA', 'ATRASADA');

CREATE TABLE cronograma_etapas (
    id UUID PRIMARY KEY,
    tcc_id UUID NOT NULL REFERENCES tccs(id) ON DELETE CASCADE,
    nome VARCHAR(255) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    status status_cronograma_etapa NOT NULL DEFAULT 'PENDENTE',
    entregue_em TIMESTAMP,
    aprovada_em TIMESTAMP,
    observacao TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cronograma_etapas_tcc ON cronograma_etapas(tcc_id);
CREATE INDEX idx_cronograma_etapas_status ON cronograma_etapas(status);

CREATE OR REPLACE FUNCTION cronograma_etapas_updated_at_trigger()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cronograma_etapas_updated_at
BEFORE UPDATE ON cronograma_etapas
FOR EACH ROW
EXECUTE FUNCTION cronograma_etapas_updated_at_trigger();

