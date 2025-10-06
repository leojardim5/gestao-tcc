package com.leonardo.gestaotcc.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComentarioDto {

    public static class ComentarioCreateRequest {
        @NotBlank(message = "O ID da submissão não pode estar em branco")
        private UUID submissaoId;
        @NotBlank(message = "O texto não pode estar em branco")
        private String texto;

        public UUID getSubmissaoId() {
            return submissaoId;
        }

        public void setSubmissaoId(UUID submissaoId) {
            this.submissaoId = submissaoId;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }
    }

    public static class ComentarioResponse {
        private UUID id;
        private UUID submissaoId;
        private UsuarioDto.UsuarioResponse autor;
        private String texto;
        private LocalDateTime criadoEm;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getSubmissaoId() {
            return submissaoId;
        }

        public void setSubmissaoId(UUID submissaoId) {
            this.submissaoId = submissaoId;
        }

        public UsuarioDto.UsuarioResponse getAutor() {
            return autor;
        }

        public void setAutor(UsuarioDto.UsuarioResponse autor) {
            this.autor = autor;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        public LocalDateTime getCriadoEm() {
            return criadoEm;
        }

        public void setCriadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
        }
    }
}