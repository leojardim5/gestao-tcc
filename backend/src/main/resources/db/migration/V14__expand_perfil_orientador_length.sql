-- Ajusta tamanho do campo de perfil para suportar descricoes extensas
ALTER TABLE usuarios
    ALTER COLUMN perfil_orientador TYPE VARCHAR(4000);

