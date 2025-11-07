-- Recriar usuários de teste
-- Senha para todos: "password123"

-- Remover registros dependentes antes de excluir usuários
DELETE FROM tccs WHERE orientador_id IN (
    SELECT id FROM usuarios WHERE email IN (
        'aluno@email.com', 
        'orientador@email.com', 
        'coordenador@email.com',
        'ana.silva@universidade.edu',
        'carlos.mendes@universidade.edu',
        'maria.santos@estudante.edu',
        'pedro.oliveira@estudante.edu',
        'ana.costa@estudante.edu'
    )
) OR aluno_id IN (
    SELECT id FROM usuarios WHERE email IN (
        'aluno@email.com', 
        'orientador@email.com', 
        'coordenador@email.com',
        'ana.silva@universidade.edu',
        'carlos.mendes@universidade.edu',
        'maria.santos@estudante.edu',
        'pedro.oliveira@estudante.edu',
        'ana.costa@estudante.edu'
    )
) OR coorientador_id IN (
    SELECT id FROM usuarios WHERE email IN (
        'aluno@email.com', 
        'orientador@email.com', 
        'coordenador@email.com',
        'ana.silva@universidade.edu',
        'carlos.mendes@universidade.edu',
        'maria.santos@estudante.edu',
        'pedro.oliveira@estudante.edu',
        'ana.costa@estudante.edu'
    )
);

DELETE FROM usuarios WHERE email IN (
    'aluno@email.com', 
    'orientador@email.com', 
    'coordenador@email.com',
    'ana.silva@universidade.edu',
    'carlos.mendes@universidade.edu',
    'maria.santos@estudante.edu',
    'pedro.oliveira@estudante.edu',
    'ana.costa@estudante.edu'
);

-- Inserir usuários de teste
INSERT INTO usuarios (nome, email, senha_hash, papel, disponivel_para_orientacao, perfil_orientador)
VALUES
    ('Aluno Fulano', 'aluno@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', false, NULL),
    ('Orientador Ciclano', 'orientador@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, 'Especialista em engenharia de software e integração contínua.'),
    ('Coordenador Beltrano', 'coordenador@email.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'COORDENADOR', true, NULL),
    ('Dr. Ana Silva', 'ana.silva@universidade.edu', '$2a$10$byeoJVje5YbgtGug3Ta3eeDituG8urA/M35.nmpHR6vVQJLWqI.6i', 'ORIENTADOR', true, 'Pesquisa em IA aplicada à educação e analytics.'),
    ('Prof. Carlos Mendes', 'carlos.mendes@universidade.edu', '$2a$10$byeoJVje5YbgtGug3Ta3eeDituG8urA/M35.nmpHR6vVQJLWqI.6i', 'ORIENTADOR', true, 'Atuação em desenvolvimento mobile híbrido e APIs.'),
    ('Maria Santos', 'maria.santos@estudante.edu', '$2a$10$aJDftGPwA3ugnnlArxvK1OqmM3JeG4JtsgtnNc0bET8C2OrDYiStW', 'ALUNO', false, NULL),
    ('Pedro Oliveira', 'pedro.oliveira@estudante.edu', '$2a$10$aJDftGPwA3ugnnlArxvK1OqmM3JeG4JtsgtnNc0bET8C2OrDYiStW', 'ALUNO', false, NULL),
    ('Ana Costa', 'ana.costa@estudante.edu', '$2a$10$aJDftGPwA3ugnnlArxvK1OqmM3JeG4JtsgtnNc0bET8C2OrDYiStW', 'ALUNO', false, NULL);

