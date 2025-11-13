package com.leonardo.gestaotcc.dto.cronograma;

import com.leonardo.gestaotcc.enums.StatusCronogramaEtapa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CronogramaEtapaDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtapaResponse {
        private UUID id;
        private String nome;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private StatusCronogramaEtapa status;
        private LocalDateTime concluidoEm;
        private String observacao;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtapaCreateRequest {
        @NotBlank(message = "Nome da etapa é obrigatório")
        @Size(max = 255, message = "Nome da etapa deve ter no máximo 255 caracteres")
        private String nome;

        @NotNull(message = "Data de início é obrigatória")
        private LocalDate dataInicio;

        @NotNull(message = "Data de fim é obrigatória")
        private LocalDate dataFim;

        private StatusCronogramaEtapa status;

        @Size(max = 2000, message = "Observação deve ter no máximo 2000 caracteres")
        private String observacao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtapaUpdateRequest {
        @Size(max = 255, message = "Nome da etapa deve ter no máximo 255 caracteres")
        private String nome;

        private LocalDate dataInicio;

        private LocalDate dataFim;

        private StatusCronogramaEtapa status;

        @Size(max = 2000, message = "Observação deve ter no máximo 2000 caracteres")
        private String observacao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtualizarStatusRequest {
        @NotNull(message = "Status é obrigatório")
        private StatusCronogramaEtapa status;

        @Size(max = 2000, message = "Observação deve ter no máximo 2000 caracteres")
        private String observacao;
    }
}

