ALTER TABLE usuarios
    ALTER COLUMN disponivel_para_orientacao SET DEFAULT FALSE;

-- Garantir que o usuário admin criado manualmente permanece como orientador
UPDATE usuarios
SET papel = 'ORIENTADOR',
    disponivel_para_orientacao = FALSE
WHERE email = 'admin@gestaotcc.com';

-- Criar um aluno de teste, caso ainda não exista
INSERT INTO usuarios (
    nome,
    email,
    senha_hash,
    papel,
    disponivel_para_orientacao,
    perfil_orientador,
    ativo
)
SELECT
    'Aluno Teste',
    'aluno.teste@gestaotcc.com',
    crypt('AlunoTeste@2025', gen_salt('bf')),
    'ALUNO',
    FALSE,
    NULL,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'aluno.teste@gestaotcc.com'
);

