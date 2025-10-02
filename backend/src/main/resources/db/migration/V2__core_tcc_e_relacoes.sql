-- Prompt 2: Tabelas centrais do sistema de TCC

-- A) Tabela de TCCs
CREATE TABLE tccs (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    aluno_id BIGINT NOT NULL,
    status tcc_status NOT NULL DEFAULT 'EM_ANDAMENTO',
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_tccs_aluno_id UNIQUE (aluno_id),
    CONSTRAINT fk_tccs_aluno FOREIGN KEY (aluno_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE TRIGGER trg_tccs_set_updated_at
BEFORE UPDATE ON tccs
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- B) Tabela de relação TCC-Orientador (N:N)
CREATE TABLE tcc_orientadores (
    id BIGSERIAL PRIMARY KEY,
    tcc_id BIGINT NOT NULL,
    orientador_id BIGINT NOT NULL,
    tipo tipo_orientacao NOT NULL,
    CONSTRAINT uq_tcc_orientador UNIQUE (tcc_id, orientador_id),
    CONSTRAINT fk_tcc_orientadores_tcc FOREIGN KEY (tcc_id) REFERENCES tccs(id) ON DELETE CASCADE,
    CONSTRAINT fk_tcc_orientadores_orientador FOREIGN KEY (orientador_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE INDEX idx_tcc_orientadores_tcc_id ON tcc_orientadores(tcc_id);

-- C) Tabela de Prazos
CREATE TABLE prazos (
    id BIGSERIAL PRIMARY KEY,
    tcc_id BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    data_limite TIMESTAMP NOT NULL,
    cumprido BOOLEAN NOT NULL DEFAULT FALSE,
    criado_por BIGINT,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_prazos_tcc FOREIGN KEY (tcc_id) REFERENCES tccs(id) ON DELETE CASCADE,
    CONSTRAINT fk_prazos_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE INDEX idx_prazos_tcc_data_limite ON prazos(tcc_id, data_limite);

-- D) Tabela de Reuniões
CREATE TABLE reunioes (
    id BIGSERIAL PRIMARY KEY,
    tcc_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    duracao_min INTEGER,
    local VARCHAR(200),
    link_vc VARCHAR(300),
    pauta TEXT,
    ata TEXT,
    CONSTRAINT fk_reunioes_tcc FOREIGN KEY (tcc_id) REFERENCES tccs(id) ON DELETE CASCADE
);

CREATE INDEX idx_reunioes_tcc_data_hora ON reunioes(tcc_id, data_hora);

-- E) Tabela de Participação em Reunião
CREATE TABLE participacao_reuniao (
    id BIGSERIAL PRIMARY KEY,
    reuniao_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    presenca presenca NOT NULL DEFAULT 'PRESENTE',
    CONSTRAINT uq_participacao_reuniao_usuario UNIQUE (reuniao_id, usuario_id),
    CONSTRAINT fk_participacao_reuniao_reuniao FOREIGN KEY (reuniao_id) REFERENCES reunioes(id) ON DELETE CASCADE,
    CONSTRAINT fk_participacao_reuniao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE INDEX idx_participacao_reuniao_reuniao_id ON participacao_reuniao(reuniao_id);

-- F) Tabela de Submissões
CREATE TABLE submissoes (
    id BIGSERIAL PRIMARY KEY,
    tcc_id BIGINT NOT NULL,
    numero INTEGER NOT NULL,
    arquivo_url TEXT NOT NULL,
    arquivo_hash VARCHAR(64),
    enviado_por BIGINT NOT NULL,
    enviado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_submissoes_tcc_numero UNIQUE (tcc_id, numero),
    CONSTRAINT fk_submissoes_tcc FOREIGN KEY (tcc_id) REFERENCES tccs(id) ON DELETE CASCADE,
    CONSTRAINT fk_submissoes_enviado_por FOREIGN KEY (enviado_por) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE INDEX idx_submissoes_tcc_id ON submissoes(tcc_id);

-- G) Tabela de Comentários
CREATE TABLE comentarios (
    id BIGSERIAL PRIMARY KEY,
    submissao_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    texto TEXT NOT NULL,
    resolvido BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_comentarios_submissao FOREIGN KEY (submissao_id) REFERENCES submissoes(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentarios_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE INDEX idx_comentarios_submissao_id ON comentarios(submissao_id);

-- H) Tabela de Notificações
CREATE TABLE notificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo tipo_notificacao NOT NULL,
    mensagem TEXT NOT NULL,
    entidade VARCHAR(50),
    referencia_id BIGINT,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    criada_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notificacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_notificacoes_usuario_lida ON notificacoes(usuario_id, lida);
