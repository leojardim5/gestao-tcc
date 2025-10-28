-- Script para inserir usuários de teste (2 alunos e 2 orientadores)
-- Senha para todos: "password" -> hash: $2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G

-- Inserir alunos
INSERT INTO usuarios (id, nome, email, senha_hash, papel, ativo, disponivel_para_orientacao, criado_em, atualizado_em) VALUES
(gen_random_uuid(), 'João Silva Aluno', 'joao.aluno@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', true, false, NOW(), NOW()),
(gen_random_uuid(), 'Maria Santos Aluna', 'maria.aluna@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', true, false, NOW(), NOW());

-- Inserir orientadores
INSERT INTO usuarios (id, nome, email, senha_hash, papel, ativo, disponivel_para_orientacao, criado_em, atualizado_em) VALUES
(gen_random_uuid(), 'Prof. Carlos Orientador', 'carlos.orientador@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, true, NOW(), NOW()),
(gen_random_uuid(), 'Prof. Ana Orientadora', 'ana.orientadora@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, true, NOW(), NOW());

-- Verificar se os usuários foram inseridos
SELECT id, nome, email, papel, ativo, disponivel_para_orientacao, criado_em 
FROM usuarios 
WHERE email IN (
    'joao.aluno@teste.com', 
    'maria.aluna@teste.com', 
    'carlos.orientador@teste.com', 
    'ana.orientadora@teste.com'
)
ORDER BY papel, nome;
