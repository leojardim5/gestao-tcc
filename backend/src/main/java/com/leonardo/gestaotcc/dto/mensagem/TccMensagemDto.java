package com.leonardo.gestaotcc.dto.mensagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class TccMensagemDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MensagemResponse {
        private UUID id;
        private UUID autorId;
        private String autorNome;
        private String autorEmail;
        private String conteudo;
        private LocalDateTime criadoEm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarMensagemRequest {
        @NotBlank(message = "Mensagem é obrigatória")
        @Size(max = 2000, message = "Mensagem pode ter no máximo 2000 caracteres")
        private String conteudo;
    }
}

