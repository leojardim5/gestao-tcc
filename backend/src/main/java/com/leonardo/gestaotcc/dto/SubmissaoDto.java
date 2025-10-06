package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.StatusSubmissao;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmissaoDto {

    public static class SubmissaoCreateRequest {
        @NotBlank(message = "O ID do TCC não pode estar em branco")
        private UUID tccId;
        @NotBlank(message = "A URL do arquivo não pode estar em branco")
        private String arquivoUrl;

        public UUID getTccId() {
            return tccId;
        }

        public void setTccId(UUID tccId) {
            this.tccId = tccId;
        }

        public String getArquivoUrl() {
            return arquivoUrl;
        }

        public void setArquivoUrl(String arquivoUrl) {
            this.arquivoUrl = arquivoUrl;
        }
    }

    public static class SubmissaoDecisionRequest {
        private StatusSubmissao status;

        public StatusSubmissao getStatus() {
            return status;
        }

        public void setStatus(StatusSubmissao status) {
            this.status = status;
        }
    }

    public static class SubmissaoResponse {
        private UUID id;
        private UUID tccId;
        private Integer versao;
        private StatusSubmissao status;
        private String arquivoUrl;
        private LocalDateTime enviadoEm;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getTccId() {
            return tccId;
        }

        public void setTccId(UUID tccId) {
            this.tccId = tccId;
        }

        public Integer getVersao() {
            return versao;
        }

        public void setVersao(Integer versao) {
            this.versao = versao;
        }

        public StatusSubmissao getStatus() {
            return status;
        }

        public void setStatus(StatusSubmissao status) {
            this.status = status;
        }

        public String getArquivoUrl() {
            return arquivoUrl;
        }

        public void setArquivoUrl(String arquivoUrl) {
            this.arquivoUrl = arquivoUrl;
        }

        public LocalDateTime getEnviadoEm() {
            return enviadoEm;
        }

        public void setEnviadoEm(LocalDateTime enviadoEm) {
            this.enviadoEm = enviadoEm;
        }
    }
}