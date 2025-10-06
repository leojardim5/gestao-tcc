-- Tabela de TCCs
CREATE TABLE tccs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(255) NOT NULL,
    resumo TEXT NOT NULL,
    status status_tcc NOT NULL,
    data_inicio DATE,
    data_entrega_prevista DATE,
    aluno_id UUID NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE RESTRICT,
    orientador_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    coorientador_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp
BEFORE UPDATE ON tccs
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Tabela de Submissões
CREATE TABLE submissoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tcc_id UUID NOT NULL REFERENCES tccs(id) ON DELETE CASCADE,
    versao INT NOT NULL,
    arquivo_url VARCHAR(255) NOT NULL,
    status status_submissao NOT NULL,
    enviado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (tcc_id, versao)
);

-- Tabela de Reuniões
CREATE TABLE reunioes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tcc_id UUID NOT NULL REFERENCES tccs(id) ON DELETE CASCADE,
    data_hora TIMESTAMP WITH TIME ZONE NOT NULL,
    tema VARCHAR(255) NOT NULL,
    resumo TEXT,
    tipo tipo_reuniao NOT NULL
);

-- Tabela de Comentários
CREATE TABLE comentarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submissao_id UUID NOT NULL REFERENCES submissoes(id) ON DELETE CASCADE,
    autor_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    texto TEXT NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Tabela de Notificações
CREATE TABLE notificacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    tipo tipo_notificacao NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT false,
    criada_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
