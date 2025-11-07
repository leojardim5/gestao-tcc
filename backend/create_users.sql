-- Criar usuários de teste
INSERT INTO usuarios (nome, email, senha_hash, papel, disponivel_para_orientacao, perfil_orientador) VALUES 
('João Silva Aluno', 'joao.aluno@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', false, NULL),
('Maria Santos Aluna', 'maria.aluna@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', false, NULL),
('Prof. Carlos Orientador', 'carlos.orientador@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, 'Foco em arquitetura limpa, TDD e liderança técnica.'),
('Prof. Ana Orientadora', 'ana.orientadora@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, 'Atuação em UX research, design systems e prototipação rápida.');
