-- Senha para todos os usuários: "password" -> hash: $2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G
-- Este é um hash de exemplo. Para produção, use um mecanismo seguro para gerar e armazenar senhas.

-- Inserir usuários
INSERT INTO usuarios (nome, email, senha_hash, papel)
VALUES
    ('Aluno Fulano', 'aluno@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO'),
    ('Orientador Ciclano', 'orientador@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR'),
    ('Coordenador Beltrano', 'coordenador@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'COORDENADOR');

-- Inserir TCC
DO $$
DECLARE
    aluno_id_var UUID;
    orientador_id_var UUID;
    tcc_id_var UUID;
    submissao_id_var UUID;
BEGIN
    -- Obter IDs
    SELECT id INTO aluno_id_var FROM usuarios WHERE email = 'aluno@email.com';
    SELECT id INTO orientador_id_var FROM usuarios WHERE email = 'orientador@email.com';

    -- Inserir TCC
    INSERT INTO tccs (titulo, resumo, status, aluno_id, orientador_id, data_inicio, data_entrega_prevista)
    VALUES (
        'Análise e Desenvolvimento de um Sistema de Gestão de TCC',
        'Este trabalho descreve a concepção e implementação de um sistema web para auxiliar na gestão de Trabalhos de Conclusão de Curso.',
        'EM_ANDAMENTO',
        aluno_id_var,
        orientador_id_var,
        '2025-03-01',
        '2025-11-30'
    ) RETURNING id INTO tcc_id_var;

    -- Inserir Submissão
    INSERT INTO submissoes (tcc_id, versao, arquivo_url, status)
    VALUES (tcc_id_var, 1, 'http://example.com/submissao_v1.pdf', 'EM_REVISAO')
    RETURNING id INTO submissao_id_var;

    -- Inserir Reunião
    INSERT INTO reunioes (tcc_id, data_hora, tema, tipo)
    VALUES (tcc_id_var, '2025-10-15T14:00:00Z', 'Revisão da Introdução', 'ONLINE');

    -- Inserir Notificação para o orientador sobre a nova submissão
    INSERT INTO notificacoes (usuario_id, tipo, mensagem)
    VALUES (orientador_id_var, 'COMENTARIO', 'O aluno Aluno Fulano enviou a versão 1 do TCC.');
END $$;
