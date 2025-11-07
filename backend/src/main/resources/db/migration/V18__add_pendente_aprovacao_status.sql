DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        INNER JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'status_tcc'
          AND e.enumlabel = 'PENDENTE_APROVACAO'
    ) THEN
        ALTER TYPE status_tcc ADD VALUE 'PENDENTE_APROVACAO';
    END IF;
END
$$;

