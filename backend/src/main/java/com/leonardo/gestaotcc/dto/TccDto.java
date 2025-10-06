package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TccDto {

    public static class TccCreateRequest {
        @NotBlank(message = "O ID do aluno não pode estar em branco")
        private UUID alunoId;
        @NotBlank(message = "O ID do orientador não pode estar em branco")
        private UUID orientadorId;
        @NotBlank(message = "O título não pode estar em branco")
        private String titulo;
        @NotBlank(message = "O resumo não pode estar em branco")
        private String resumo;
        private LocalDate dataInicio;
        private LocalDate dataEntregaPrevista;

        public UUID getAlunoId() {
            return alunoId;
        }

        public void setAlunoId(UUID alunoId) {
            this.alunoId = alunoId;
        }

        public UUID getOrientadorId() {
            return orientadorId;
        }

        public void setOrientadorId(UUID orientadorId) {
            this.orientadorId = orientadorId;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getResumo() {
            return resumo;
        }

        public void setResumo(String resumo) {
            this.resumo = resumo;
        }

        public LocalDate getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
        }

        public LocalDate getDataEntregaPrevista() {
            return dataEntregaPrevista;
        }

        public void setDataEntregaPrevista(LocalDate dataEntregaPrevista) {
            this.dataEntregaPrevista = dataEntregaPrevista;
        }
    }

    public static class TccUpdateRequest {
        private String titulo;
        private String resumo;
        private StatusTcc status;
        private UUID orientadorId;
        private UUID coorientadorId;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getResumo() {
            return resumo;
        }

        public void setResumo(String resumo) {
            this.resumo = resumo;
        }

        public StatusTcc getStatus() {
            return status;
        }

        public void setStatus(StatusTcc status) {
            this.status = status;
        }

        public UUID getOrientadorId() {
            return orientadorId;
        }

        public void setOrientadorId(UUID orientadorId) {
            this.orientadorId = orientadorId;
        }

        public UUID getCoorientadorId() {
            return coorientadorId;
        }

        public void setCoorientadorId(UUID coorientadorId) {
            this.coorientadorId = coorientadorId;
        }
    }

    public static class TccResponse {
        private UUID id;
        private String titulo;
        private String resumo;
        private StatusTcc status;
        private UsuarioDto.UsuarioResponse aluno;
        private UsuarioDto.UsuarioResponse orientador;
        private UsuarioDto.UsuarioResponse coorientador;
        private LocalDate dataInicio;
        private LocalDate dataEntregaPrevista;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getResumo() {
            return resumo;
        }

        public void setResumo(String resumo) {
            this.resumo = resumo;
        }

        public StatusTcc getStatus() {
            return status;
        }

        public void setStatus(StatusTcc status) {
            this.status = status;
        }

        public UsuarioDto.UsuarioResponse getAluno() {
            return aluno;
        }

        public void setAluno(UsuarioDto.UsuarioResponse aluno) {
            this.aluno = aluno;
        }

        public UsuarioDto.UsuarioResponse getOrientador() {
            return orientador;
        }

        public void setOrientador(UsuarioDto.UsuarioResponse orientador) {
            this.orientador = orientador;
        }

        public UsuarioDto.UsuarioResponse getCoorientador() {
            return coorientador;
        }

        public void setCoorientador(UsuarioDto.UsuarioResponse coorientador) {
            this.coorientador = coorientador;
        }

        public LocalDate getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
        }

        public LocalDate getDataEntregaPrevista() {
            return dataEntregaPrevista;
        }

        public void setDataEntregaPrevista(LocalDate dataEntregaPrevista) {
            this.dataEntregaPrevista = dataEntregaPrevista;
        }

        public LocalDateTime getCriadoEm() {
            return criadoEm;
        }

        public void setCriadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
        }

        public LocalDateTime getAtualizadoEm() {
            return atualizadoEm;
        }

        public void setAtualizadoEm(LocalDateTime atualizadoEm) {
            this.atualizadoEm = atualizadoEm;
        }
    }
}