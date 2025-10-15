package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Gerenciamento de notificações em tempo real")
public class NotificationWebSocketController {

    private final NotificationService notificationService;

    @GetMapping("/user/{usuarioId}")
    @Operation(summary = "Obter notificações do usuário", description = "Retorna todas as notificações de um usuário")
    public ResponseEntity<List<NotificacaoDto.NotificacaoResponse>> getUserNotifications(@PathVariable UUID usuarioId) {
        List<NotificacaoDto.NotificacaoResponse> notifications = notificationService.getUserNotifications(usuarioId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificacaoId}/read")
    @Operation(summary = "Marcar notificação como lida", description = "Marca uma notificação específica como lida")
    public ResponseEntity<NotificacaoDto.NotificacaoResponse> markAsRead(@PathVariable UUID notificacaoId) {
        NotificacaoDto.NotificacaoResponse notification = notificationService.markAsRead(notificacaoId);
        return ResponseEntity.ok(notification);
    }

    @PatchMapping("/user/{usuarioId}/read-all")
    @Operation(summary = "Marcar todas as notificações como lidas", description = "Marca todas as notificações de um usuário como lidas")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID usuarioId) {
        notificationService.markAllAsRead(usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{usuarioId}/unread-count")
    @Operation(summary = "Contar notificações não lidas", description = "Retorna o número de notificações não lidas de um usuário")
    public ResponseEntity<Long> getUnreadCount(@PathVariable UUID usuarioId) {
        long count = notificationService.getUnreadCount(usuarioId);
        return ResponseEntity.ok(count);
    }

    @MessageMapping("/notifications.subscribe")
    @SendToUser("/queue/notifications")
    public String subscribeToNotifications() {
        return "Subscribed to notifications";
    }

    @MessageMapping("/notifications.send")
    @SendTo("/topic/global")
    public String sendGlobalMessage(String message) {
        return "Global message: " + message;
    }
}
