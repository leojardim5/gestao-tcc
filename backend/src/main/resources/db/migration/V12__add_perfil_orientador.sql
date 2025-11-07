-- Adiciona coluna para perfil de orientadores
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS perfil_orientador VARCHAR(1000);

-- Garante valor nulo para usuários que não são orientadores
UPDATE usuarios
SET perfil_orientador = NULL
WHERE papel <> 'ORIENTADOR';

