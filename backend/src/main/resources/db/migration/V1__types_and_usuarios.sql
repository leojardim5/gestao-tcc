-- Prompt 1: Tipos ENUM e tabela de usuários

-- Definição de tipos ENUM
CREATE TYPE usuario_papel AS ENUM ('ALUNO', 'ORIENTADOR', 'COORDENADOR');
CREATE TYPE tcc_status AS ENUM ('EM_ANDAMENTO', 'APROVADO', 'REPROVADO', 'TRANCADO', 'ARQUIVADO');
CREATE TYPE tipo_orientacao AS ENUM ('ORIENTADOR_PRINCIPAL', 'COORIENTADOR');
CREATE TYPE presenca AS ENUM ('PRESENTE', 'AUSENTE', 'JUSTIFICADA');
CREATE TYPE tipo_notificacao AS ENUM ('SUBMISSAO', 'COMENTARIO', 'PRAZO', 'REUNIAO', 'SISTEMA');

-- Função para atualizar o campo "atualizado_em"
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tabela de usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    senha VARCHAR(200) NOT NULL,
    papel usuario_papel NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
);

-- Trigger para a tabela de usuários
CREATE TRIGGER trg_usuarios_set_updated_at
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
