package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.service.ConviteOrientacaoService;
import com.leonardo.gestaotcc.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Badge de Notificações", description = "Contadores para badges de notificações na sidebar")
@RestController
@RequestMapping("/api/notification-badge")
@RequiredArgsConstructor
public class NotificationBadgeController {

    private final ConviteOrientacaoService conviteOrientacaoService;
    private final NotificationService notificationService;

    @Operation(summary = "Obter contador total de notificações", description = "Retorna o número total de notificações pendentes (convites + notificações não lidas)")
    @GetMapping("/user/{usuarioId}/total")
    public ResponseEntity<Map<String, Object>> getTotalNotificationCount(@PathVariable UUID usuarioId) {
        // Contar convites pendentes
        long convitesPendentes = conviteOrientacaoService.contarConvitesPendentes(usuarioId);
        
        // Contar notificações não lidas
        long notificacoesNaoLidas = notificationService.getUnreadCount(usuarioId);
        
        // Total geral
        long total = convitesPendentes + notificacoesNaoLidas;
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("convitesPendentes", convitesPendentes);
        response.put("notificacoesNaoLidas", notificacoesNaoLidas);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter apenas o número total", description = "Retorna apenas o número total de notificações pendentes")
    @GetMapping("/user/{usuarioId}/count")
    public ResponseEntity<Long> getNotificationCount(@PathVariable UUID usuarioId) {
        long convitesPendentes = conviteOrientacaoService.contarConvitesPendentes(usuarioId);
        long notificacoesNaoLidas = notificationService.getUnreadCount(usuarioId);
        long total = convitesPendentes + notificacoesNaoLidas;
        
        return ResponseEntity.ok(total);
    }
}
