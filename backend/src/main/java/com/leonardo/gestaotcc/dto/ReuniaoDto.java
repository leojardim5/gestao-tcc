package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.TipoReuniao;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class ReuniaoDto {

    public static class ReuniaoCreateRequest {
        @NotBlank(message = "O ID do TCC não pode estar em branco")
        private UUID tccId;
        private Instant dataHora;
        @NotBlank(message = "O tema não pode estar em branco")
        private String tema;
        private String resumo;
        private TipoReuniao tipo;

        public UUID getTccId() {
            return tccId;
        }

        public void setTccId(UUID tccId) {
            this.tccId = tccId;
        }

        public Instant getDataHora() {
            return dataHora;
        }

        public void setDataHora(Instant dataHora) {
            this.dataHora = dataHora;
        }

        public String getTema() {
            return tema;
        }

        public void setTema(String tema) {
            this.tema = tema;
        }

        public String getResumo() {
            return resumo;
        }

        public void setResumo(String resumo) {
            this.resumo = resumo;
        }

        public TipoReuniao getTipo() {
            return tipo;
        }

        public void setTipo(TipoReuniao tipo) {
            this.tipo = tipo;
        }
    }

    public static class ReuniaoResponse {
        private UUID id;
        private UUID tccId;
        private Instant dataHora;
        private String tema;
        private String resumo;
        private TipoReuniao tipo;

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

        public Instant getDataHora() {
            return dataHora;
        }

        public void setDataHora(Instant dataHora) {
            this.dataHora = dataHora;
        }

        public String getTema() {
            return tema;
        }

        public void setTema(String tema) {
            this.tema = tema;
        }

        public String getResumo() {
            return resumo;
        }

        public void setResumo(String resumo) {
            this.resumo = resumo;
        }

        public TipoReuniao getTipo() {
            return tipo;
        }

        public void setTipo(TipoReuniao tipo) {
            this.tipo = tipo;
        }
    }
}