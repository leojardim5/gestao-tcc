-- Adicionar campo disponivelParaOrientacao na tabela usuarios (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'usuarios' 
                   AND column_name = 'disponivel_para_orientacao') THEN
        ALTER TABLE usuarios ADD COLUMN disponivel_para_orientacao BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- Atualizar orientadores existentes para disponíveis
UPDATE usuarios 
SET disponivel_para_orientacao = TRUE 
WHERE papel = 'ORIENTADOR' AND disponivel_para_orientacao IS NOT NULL;
