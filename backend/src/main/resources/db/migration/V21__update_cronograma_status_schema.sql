DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_type t
                 JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'status_cronograma_etapa'
          AND e.enumlabel = 'ENTREGUE'
    ) THEN
        ALTER TYPE status_cronograma_etapa RENAME VALUE 'ENTREGUE' TO 'CONCLUIDO';
    END IF;
END $$;

ALTER TABLE cronograma_etapas
    RENAME COLUMN entregue_em TO concluido_em;

ALTER TABLE cronograma_etapas
    DROP COLUMN IF EXISTS aprovada_em;

