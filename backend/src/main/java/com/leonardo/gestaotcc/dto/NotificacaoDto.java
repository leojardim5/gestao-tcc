package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.TipoNotificacao;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificacaoDto {

    public static class NotificacaoResponse {
        private UUID id;
        private TipoNotificacao tipo;
        private String mensagem;
        private boolean lida;
        private LocalDateTime criadaEm;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public TipoNotificacao getTipo() {
            return tipo;
        }

        public void setTipo(TipoNotificacao tipo) {
            this.tipo = tipo;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public boolean isLida() {
            return lida;
        }

        public void setLida(boolean lida) {
            this.lida = lida;
        }

        public LocalDateTime getCriadaEm() {
            return criadaEm;
        }

        public void setCriadaEm(LocalDateTime criadaEm) {
            this.criadaEm = criadaEm;
        }
    }
}