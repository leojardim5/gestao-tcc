-- FORÇAR ADIÇÃO DO VALOR CONVITE_ORIENTACAO AO ENUM
-- Remover constraint temporariamente
ALTER TABLE notificacoes DROP CONSTRAINT IF EXISTS notificacoes_tipo_check;

-- Adicionar valor ao enum (ignorar se já existir)
DO $$
BEGIN
    BEGIN
        ALTER TYPE tipo_notificacao ADD VALUE 'CONVITE_ORIENTACAO';
    EXCEPTION
        WHEN duplicate_object THEN
            -- Valor já existe, continuar
            NULL;
    END;
END $$;

-- Recriar constraint
ALTER TABLE notificacoes ADD CONSTRAINT notificacoes_tipo_check 
    CHECK (tipo IN ('PRAZO', 'REUNIAO', 'COMENTARIO', 'SISTEMA', 'CONVITE_ORIENTACAO'));
