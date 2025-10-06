package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notificações", description = "Gerenciamento de notificações de usuários")
@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @Operation(summary = "Lista notificações de um usuário", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    public ResponseEntity<Page<NotificacaoDto.NotificacaoResponse>> listNotificacoes(
            @Parameter(description = "ID do usuário") @RequestParam UUID usuarioId,
            @Parameter(description = "Filtrar por notificações lidas (true/false). Se omitido, retorna todas.") @RequestParam(required = false) Boolean lidas,
            Pageable pageable) {
        Page<NotificacaoDto.NotificacaoResponse> responsePage = notificacaoService.list(usuarioId, lidas, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Marca uma notificação como lida", responses = {
            @ApiResponse(responseCode = "204", description = "Notificação marcada como lida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> markNotificacaoAsRead(@PathVariable UUID id) {
        notificacaoService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
