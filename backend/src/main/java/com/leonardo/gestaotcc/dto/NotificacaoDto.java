package com.leonardo.gestaotcc.dto;

import com.leonardo.gestaotcc.enums.TipoNotificacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificacaoDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificacaoResponse {
        private UUID id;
        private UUID usuarioId;
        private TipoNotificacao tipo;
        private String mensagem;
        private boolean lida;
        private LocalDateTime criadaEm;
    }
}
