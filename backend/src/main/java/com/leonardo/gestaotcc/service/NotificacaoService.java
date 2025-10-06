package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificacaoService {

    void push(UUID usuarioId, TipoNotificacao tipo, String mensagem);

    void markAsRead(UUID notificacaoId);

    Page<NotificacaoDto.NotificacaoResponse> list(UUID usuarioId, Boolean lidas, Pageable pageable);
}
