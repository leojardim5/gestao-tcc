-- Habilita a extensão para gerar UUIDs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Criação dos ENUMs
CREATE TYPE papel_usuario AS ENUM ('ALUNO', 'ORIENTADOR', 'COORDENADOR');
CREATE TYPE status_tcc AS ENUM ('RASCUNHO', 'EM_ANDAMENTO', 'AGUARDANDO_DEFESA', 'CONCLUIDO');
CREATE TYPE status_submissao AS ENUM ('EM_REVISAO', 'APROVADO', 'REPROVADO');
CREATE TYPE tipo_reuniao AS ENUM ('PRESENCIAL', 'ONLINE');
CREATE TYPE tipo_notificacao AS ENUM ('PRAZO', 'REUNIAO', 'COMENTARIO', 'SISTEMA');

-- Tabela de Usuários
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    papel papel_usuario NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Trigger para atualizar o campo 'atualizado_em'
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.atualizado_em = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Índices
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_papel ON usuarios(papel);
