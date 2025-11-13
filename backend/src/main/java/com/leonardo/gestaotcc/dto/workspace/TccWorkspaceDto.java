package com.leonardo.gestaotcc.dto.workspace;

import com.leonardo.gestaotcc.enums.StatusTcc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TccWorkspaceDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Overview {
        private UUID id;
        private String titulo;
        private String tema;
        private String curso;
        private StatusTcc status;
        private LocalDate dataInicio;
        private LocalDate dataEntregaPrevista;
        private AlunoInfo aluno;
        private OrientadorInfo orientador;
        private OrientadorInfo coorientador;
        private String googleFileId;
        private String googleWebViewLink;
        private String googleWebEditLink;
        private LocalDateTime googleDocCriadoEm;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlunoInfo {
        private UUID id;
        private String nome;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrientadorInfo {
        private UUID id;
        private String nome;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocComment {
        private String id;
        private String authorName;
        private String authorPhotoUrl;
        private String content;
        private boolean resolved;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

