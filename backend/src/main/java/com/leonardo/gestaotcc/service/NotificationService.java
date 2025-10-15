package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.entity.Notificacao;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.mapper.NotificacaoMapper;
import com.leonardo.gestaotcc.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoMapper notificacaoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificacaoDto.NotificacaoResponse createNotification(
            UUID usuarioId, 
            TipoNotificacao tipo, 
            String mensagem) {
        
        Notificacao notificacao = Notificacao.builder()
                .usuarioId(usuarioId)
                .tipo(tipo)
                .mensagem(mensagem)
                .lida(false)
                .criadaEm(LocalDateTime.now())
                .build();

        Notificacao saved = notificacaoRepository.save(notificacao);
        
        // Enviar notificação em tempo real
        sendRealTimeNotification(usuarioId, notificacaoMapper.toResponse(saved));
        
        return notificacaoMapper.toResponse(saved);
    }

    public void sendRealTimeNotification(UUID usuarioId, NotificacaoDto.NotificacaoResponse notificacao) {
        messagingTemplate.convertAndSendToUser(
                usuarioId.toString(),
                "/queue/notifications",
                notificacao
        );
    }

    public void sendGlobalNotification(String message) {
        messagingTemplate.convertAndSend("/topic/global", message);
    }

    public List<NotificacaoDto.NotificacaoResponse> getUserNotifications(UUID usuarioId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIdOrderByCriadaEmDesc(usuarioId);
        return notificacoes.stream()
                .map(notificacaoMapper::toResponse)
                .toList();
    }

    public NotificacaoDto.NotificacaoResponse markAsRead(UUID notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        
        notificacao.setLida(true);
        Notificacao saved = notificacaoRepository.save(notificacao);
        
        return notificacaoMapper.toResponse(saved);
    }

    public void markAllAsRead(UUID usuarioId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId);
        notificacoes.forEach(notificacao -> notificacao.setLida(true));
        notificacaoRepository.saveAll(notificacoes);
    }

    public long getUnreadCount(UUID usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    // Métodos para criar notificações específicas
    public void notifyNewSubmission(UUID orientadorId, String tccTitulo) {
        createNotification(
                orientadorId,
                TipoNotificacao.COMENTARIO,
                "Nova submissão recebida para o TCC: " + tccTitulo
        );
    }

    public void notifySubmissionDecision(UUID alunoId, String tccTitulo, String status) {
        createNotification(
                alunoId,
                TipoNotificacao.COMENTARIO,
                "Sua submissão do TCC '" + tccTitulo + "' foi " + status.toLowerCase()
        );
    }

    public void notifyMeetingScheduled(UUID usuarioId, String tccTitulo, String dataHora) {
        createNotification(
                usuarioId,
                TipoNotificacao.REUNIAO,
                "Reunião agendada para o TCC '" + tccTitulo + "' em " + dataHora
        );
    }

    public void notifyDeadlineApproaching(UUID usuarioId, String tccTitulo, int diasRestantes) {
        createNotification(
                usuarioId,
                TipoNotificacao.PRAZO,
                "Prazo do TCC '" + tccTitulo + "' se aproxima! Restam " + diasRestantes + " dias."
        );
    }
}
