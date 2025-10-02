-- Prompt 4: Seed de dados para ambiente de desenvolvimento

-- Usar CTEs (Common Table Expressions) para evitar IDs fixos

-- 1. Inserir usuários e capturar seus IDs
WITH ins_usuarios AS (
    INSERT INTO usuarios (nome, email, senha, papel)
    VALUES
        ('Aluno Fulano de Tal', 'aluno.fulano@unirio.br', 'hash_fake_bcrypt_password', 'ALUNO'),
        ('Orientador Ciclano da Silva', 'orientador.ciclano@unirio.br', 'hash_fake_bcrypt_password', 'ORIENTADOR'),
        ('Coordenador Beltrano de Souza', 'coordenador.beltrano@unirio.br', 'hash_fake_bcrypt_password', 'COORDENADOR')
    RETURNING id, papel
),
-- 2. Inserir TCC para o aluno
aluno_data AS (SELECT id FROM ins_usuarios WHERE papel = 'ALUNO'),
orientador_data AS (SELECT id FROM ins_usuarios WHERE papel = 'ORIENTADOR'),
ins_tcc AS (
    INSERT INTO tccs (titulo, descricao, aluno_id)
    SELECT 'Desenvolvimento de um Sistema de Gestão de TCCs', 'Este projeto visa criar uma plataforma web para gerenciar o ciclo de vida dos Trabalhos de Conclusão de Curso.', id FROM aluno_data
    RETURNING id
),
-- 3. Relacionar orientador ao TCC
ins_orientador_tcc AS (
    INSERT INTO tcc_orientadores (tcc_id, orientador_id, tipo)
    SELECT ins_tcc.id, orientador_data.id, 'ORIENTADOR_PRINCIPAL' FROM ins_tcc, orientador_data
),
-- 4. Criar um prazo futuro para o TCC
ins_prazo AS (
    INSERT INTO prazos (tcc_id, titulo, descricao, data_limite, criado_por)
    SELECT ins_tcc.id, 'Entrega da Primeira Versão do Documento', 'Submeter a versão inicial contendo introdução, revisão de literatura e metodologia.', NOW() + INTERVAL '5 days', orientador_data.id FROM ins_tcc, orientador_data
),
-- 5. Criar uma reunião futura
ins_reuniao AS (
    INSERT INTO reunioes (tcc_id, data_hora, duracao_min, local, pauta)
    SELECT id, NOW() + INTERVAL '2 days', 60, 'Sala de Videoconferência 1', 'Alinhamento inicial do projeto, definição de cronograma e discussão da metodologia.' FROM ins_tcc
    RETURNING id
),
-- 6. Marcar participação na reunião
ins_participantes AS (
    INSERT INTO participacao_reuniao (reuniao_id, usuario_id, presenca)
    SELECT ins_reuniao.id, aluno_data.id, 'PRESENTE' FROM ins_reuniao, aluno_data
    UNION ALL
    SELECT ins_reuniao.id, orientador_data.id, 'PRESENTE' FROM ins_reuniao, orientador_data
),
-- 7. Criar uma submissão para o TCC
ins_submissao AS (
    INSERT INTO submissoes (tcc_id, numero, arquivo_url, enviado_por)
    SELECT ins_tcc.id, 1, '/data/seeds/tcc_1_submissao_1.pdf', aluno_data.id FROM ins_tcc, aluno_data
    RETURNING id, tcc_id
),
-- 8. Criar um comentário do orientador na submissão
ins_comentario AS (
    INSERT INTO comentarios (submissao_id, autor_id, texto)
    SELECT ins_submissao.id, orientador_data.id, 'O documento inicial está bom, mas por favor, detalhe melhor a seção de metodologia e adicione mais referências na revisão de literatura.' FROM ins_submissao, orientador_data
)
-- 9. Criar notificação para o aluno sobre o comentário
INSERT INTO notificacoes (usuario_id, tipo, mensagem, entidade, referencia_id)
SELECT aluno_data.id, 'COMENTARIO', 'Seu orientador deixou um novo comentário na Submissão Nº 1.', 'SUBMISSAO', ins_submissao.id FROM aluno_data, ins_submissao;
