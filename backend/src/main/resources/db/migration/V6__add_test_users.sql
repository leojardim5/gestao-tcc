-- Adicionar orientador de teste para desenvolvimento
INSERT INTO usuarios (nome, email, senha_hash, papel, disponivel_para_orientacao)
VALUES (
    'Dr. João Silva',
    'joao.silva@universidade.edu',
    '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G',
    'ORIENTADOR',
    true
);

-- Adicionar aluno de teste
INSERT INTO usuarios (nome, email, senha_hash, papel, disponivel_para_orientacao)
VALUES (
    'Maria Santos',
    'maria.santos@aluno.edu',
    '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G',
    'ALUNO',
    false
);

-- Adicionar coordenador de teste
INSERT INTO usuarios (nome, email, senha_hash, papel, disponivel_para_orientacao)
VALUES (
    'Prof. Carlos Coordenador',
    'carlos.coordenador@universidade.edu',
    '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G',
    'COORDENADOR',
    true
);
