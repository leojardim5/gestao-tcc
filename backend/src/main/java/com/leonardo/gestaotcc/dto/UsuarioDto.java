package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioDto {

    public static class UsuarioCreateRequest {
        @NotBlank(message = "O nome não pode estar em branco")
        private String nome;
        @Email(message = "Formato de e-mail inválido")
        @NotBlank(message = "O e-mail não pode estar em branco")
        private String email;
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        private String senha;
        private PapelUsuario papel;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }

        public PapelUsuario getPapel() {
            return papel;
        }

        public void setPapel(PapelUsuario papel) {
            this.papel = papel;
        }
    }

    public static class UsuarioUpdateRequest {
        private String nome;
        private Boolean ativo;
        private PapelUsuario papel;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Boolean getAtivo() {
            return ativo;
        }

        public void setAtivo(Boolean ativo) {
            this.ativo = ativo;
        }

        public PapelUsuario getPapel() {
            return papel;
        }

        public void setPapel(PapelUsuario papel) {
            this.papel = papel;
        }
    }

    public static class UsuarioResponse {
        private UUID id;
        private String nome;
        private String email;
        private PapelUsuario papel;
        private boolean ativo;
        private LocalDateTime criadoEm;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public PapelUsuario getPapel() {
            return papel;
        }

        public void setPapel(PapelUsuario papel) {
            this.papel = papel;
        }

        public boolean isAtivo() {
            return ativo;
        }

        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
        }

        public LocalDateTime getCriadoEm() {
            return criadoEm;
        }

        public void setCriadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
        }
    }
}