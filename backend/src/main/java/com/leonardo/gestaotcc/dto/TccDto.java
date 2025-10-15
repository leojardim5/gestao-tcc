package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TccDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccCreateRequest {
        @NotNull(message = "ID do aluno é obrigatório")
        private UUID alunoId;

        @NotNull(message = "ID do orientador é obrigatório")
        private UUID orientadorId;

        @NotBlank(message = "Título é obrigatório")
        private String titulo;

        @NotBlank(message = "Resumo é obrigatório")
        private String resumo;

        @NotNull(message = "Data de início é obrigatória")
        private LocalDate dataInicio;

        private LocalDate dataEntregaPrevista;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccUpdateRequest {
        private String titulo;
        private String resumo;
        private StatusTcc status;
        private UUID orientadorId;
        private UUID coorientadorId;
        private LocalDate dataEntregaPrevista;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccResponse {
        private UUID id;
        private String titulo;
        private String resumo;
        private StatusTcc status;
        private LocalDate dataInicio;
        private LocalDate dataEntregaPrevista;
        private UUID alunoId;
        private String alunoNome;
        private UUID orientadorId;
        private String orientadorNome;
        private UUID coorientadorId;
        private String coorientadorNome;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }
}
