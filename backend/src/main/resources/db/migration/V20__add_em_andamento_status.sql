DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
                 JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'status_cronograma_etapa'
          AND e.enumlabel = 'EM_ANDAMENTO'
    ) THEN
        ALTER TYPE status_cronograma_etapa ADD VALUE 'EM_ANDAMENTO';
    END IF;
END $$;

