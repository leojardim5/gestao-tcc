-- Garantir que o valor CONVITE_ORIENTACAO existe no enum tipo_notificacao
DO $$
BEGIN
    -- Tentar adicionar o valor, ignorando se já existir
    BEGIN
        ALTER TYPE tipo_notificacao ADD VALUE 'CONVITE_ORIENTACAO';
    EXCEPTION
        WHEN duplicate_object THEN
            -- Valor já existe, continuar
            NULL;
    END;
END $$;
