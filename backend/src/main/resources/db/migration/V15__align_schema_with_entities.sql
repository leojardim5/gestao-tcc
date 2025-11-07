-- Ensure domain tables have the audit columns expected by the JPA entities

ALTER TABLE comentarios
    ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP WITH TIME ZONE;

UPDATE comentarios
SET atualizado_em = criado_em
WHERE atualizado_em IS NULL;

ALTER TABLE notificacoes
    ADD COLUMN IF NOT EXISTS atualizada_em TIMESTAMP WITH TIME ZONE;

UPDATE notificacoes
SET atualizada_em = criada_em
WHERE atualizada_em IS NULL;

ALTER TABLE reunioes
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP WITH TIME ZONE;

UPDATE reunioes
SET criado_em = COALESCE(criado_em, NOW()),
    atualizado_em = COALESCE(atualizado_em, criado_em);

ALTER TABLE tccs
    ADD COLUMN IF NOT EXISTS tema VARCHAR(255),
    ADD COLUMN IF NOT EXISTS curso VARCHAR(255);

UPDATE tccs
SET tema = COALESCE(tema, titulo),
    curso = COALESCE(curso, 'Curso não informado');

ALTER TABLE tccs
    ALTER COLUMN tema SET NOT NULL,
    ALTER COLUMN curso SET NOT NULL;

