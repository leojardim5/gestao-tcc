package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioCreateRequest {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String senha;

        @NotNull(message = "Papel é obrigatório")
        private PapelUsuario papel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioUpdateRequest {
        private String nome;
        private Boolean ativo;
        private PapelUsuario papel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioResponse {
        private UUID id;
        private String nome;
        private String email;
        private PapelUsuario papel;
        private boolean ativo;
        private LocalDateTime criadoEm;
    }
}
