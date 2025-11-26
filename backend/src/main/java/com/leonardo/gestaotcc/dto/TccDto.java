package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TccDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccCreateRequest {
        @NotNull(message = "ID do aluno é obrigatório")
        private UUID alunoId;

        private UUID orientadorId;

        @NotBlank(message = "Título é obrigatório")
        private String titulo;

        @NotBlank(message = "Tema é obrigatório")
        private String tema;

        @NotBlank(message = "Curso é obrigatório")
        private String curso;

        @NotNull(message = "Data de início é obrigatória")
        private LocalDate dataInicio;

        private LocalDate dataEntregaPrevista;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccUpdateRequest {
        private String titulo;
        private String tema;
        private String curso;
        private StatusTcc status;
        private UUID orientadorId;
        private UUID coorientadorId;
        private LocalDate dataEntregaPrevista;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TccResponse {
        private UUID id;
        private String titulo;
        private String tema;
        private String curso;
        private StatusTcc status;
        private LocalDate dataInicio;
        private LocalDate dataEntregaPrevista;
        private UUID alunoId;
        private String alunoNome;
        private UUID orientadorId;
        private String orientadorNome;
        private UUID coorientadorId;
        private String coorientadorNome;
        private String googleFileId;
        private String googleWebViewLink;
        private String googleWebEditLink;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }
}