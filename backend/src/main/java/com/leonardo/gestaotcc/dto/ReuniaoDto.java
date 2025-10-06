package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.TipoReuniao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReuniaoDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReuniaoCreateRequest {
        @NotNull(message = "ID do TCC é obrigatório")
        private UUID tccId;

        @NotNull(message = "Data e hora da reunião são obrigatórias")
        private Instant dataHora;

        @NotBlank(message = "Tema da reunião é obrigatório")
        private String tema;

        private String resumo;

        @NotNull(message = "Tipo de reunião é obrigatório")
        private TipoReuniao tipo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReuniaoResponse {
        private UUID id;
        private UUID tccId;
        private Instant dataHora;
        private String tema;
        private String resumo;
        private TipoReuniao tipo;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }
}
