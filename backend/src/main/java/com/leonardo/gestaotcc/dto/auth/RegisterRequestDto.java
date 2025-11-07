package com.leonardo.gestaotcc.dto.auth;

import com.leonardo.gestaotcc.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "Papel é obrigatório")
    private PapelUsuario papel;

    @Size(max = 4000, message = "Perfil do orientador deve ter no máximo 4000 caracteres")
    private String perfilOrientador;
}