-- Índices para chaves estrangeiras e colunas de busca

-- Tabela: tccs
CREATE INDEX idx_tccs_aluno_id ON tccs(aluno_id);
CREATE INDEX idx_tccs_orientador_id ON tccs(orientador_id);
CREATE INDEX idx_tccs_status ON tccs(status);

-- Tabela: submissoes
CREATE INDEX idx_submissoes_tcc_id ON submissoes(tcc_id);

-- Tabela: reunioes
CREATE INDEX idx_reunioes_tcc_id ON reunioes(tcc_id);

-- Tabela: comentarios
CREATE INDEX idx_comentarios_submissao_id ON comentarios(submissao_id);
CREATE INDEX idx_comentarios_autor_id ON comentarios(autor_id);

-- Tabela: notificacoes
CREATE INDEX idx_notificacoes_usuario_id ON notificacoes(usuario_id);

-- View para a última submissão de cada TCC
CREATE OR REPLACE VIEW v_ultima_submissao AS
SELECT DISTINCT ON (s.tcc_id)
    s.tcc_id,
    s.id AS submissao_id,
    s.versao,
    s.status,
    s.enviado_em
FROM
    submissoes s
ORDER BY
    s.tcc_id, s.versao DESC;
