-- Script para adicionar PENDENTE_APROVACAO ao enum status_tcc
-- Execute este script diretamente no PostgreSQL

-- Adicionar novo valor ao enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_enum WHERE enumtypid = 'status_tcc'::regtype AND enumlabel = 'PENDENTE_APROVACAO') THEN
        ALTER TYPE status_tcc ADD VALUE 'PENDENTE_APROVACAO';
    END IF;
END $$;

-- Verificar se foi adicionado
SELECT unnest(enum_range(NULL::status_tcc)) as valores;
