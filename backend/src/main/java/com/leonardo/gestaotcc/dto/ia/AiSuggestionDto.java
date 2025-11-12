package com.leonardo.gestaotcc.dto.ia;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public final class AiSuggestionDto {

    private AiSuggestionDto() {
    }

    @Data
    @Builder
    public static class SuggestionRequest {
        @NotNull
        private UUID alunoId;
        @NotBlank
        private String alunoNome;
        @NotBlank
        private String curso;
        @NotBlank
        private String titulo;
        @NotBlank
        private String tema;
        private String resumo;
        private String mensagem;
        private List<String> palavrasChave;
    }

    @Data
    @Builder
    public static class OrientadorResumo {
        private UUID id;
        private String nome;
        private String email;
        private String perfil;
        private boolean disponivel;
        private List<String> areas;
    }

    @Data
    @Builder
    public static class AiSuggestion {
        private UUID orientadorId;
        private String orientadorNome;
        private double score;
        private String justificativa;
        private List<String> destaques;
    }

    @Data
    @Builder
    public static class SuggestionResponse {
        private List<AiSuggestion> sugestoes;
        private String modelo;
        private String mensagemSistema;
    }
}

