package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.StatusSubmissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmissaoDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissaoCreateRequest {
        @NotNull(message = "ID do TCC é obrigatório")
        private UUID tccId;

        @NotBlank(message = "URL do arquivo é obrigatória")
        private String arquivoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissaoDecisionRequest {
        @NotNull(message = "Status da decisão é obrigatório")
        private StatusSubmissao status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissaoResponse {
        private UUID id;
        private UUID tccId;
        private Integer versao;
        private String arquivoUrl;
        private StatusSubmissao status;
        private LocalDateTime enviadoEm;
    }
}
