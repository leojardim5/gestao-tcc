package com.leonardo.gestaotcc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComentarioDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComentarioCreateRequest {
        @NotNull(message = "ID da submissão é obrigatório")
        private UUID submissaoId;

        @NotNull(message = "ID do autor é obrigatório")
        private UUID autorId;

        @NotBlank(message = "Texto do comentário é obrigatório")
        private String texto;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComentarioResponse {
        private UUID id;
        private UUID submissaoId;
        private UUID autorId;
        private String autorNome;
        private String texto;
        private LocalDateTime criadoEm;
    }
}
