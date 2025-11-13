package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.mensagem.TccMensagemDto;
import com.leonardo.gestaotcc.service.TccMensagemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tccs/{tccId}/mensagens")
@RequiredArgsConstructor
public class TccMensagemController {

    private final TccMensagemService mensagemService;

    @GetMapping
    @Operation(summary = "Lista as mensagens trocadas entre aluno e orientador para o TCC")
    public ResponseEntity<List<TccMensagemDto.MensagemResponse>> listar(@PathVariable UUID tccId) {
        return ResponseEntity.ok(mensagemService.listarMensagens(tccId));
    }

    @PostMapping
    @Operation(summary = "Envia uma nova mensagem no contexto do TCC")
    public ResponseEntity<TccMensagemDto.MensagemResponse> criar(
            @PathVariable UUID tccId,
            @Valid @RequestBody TccMensagemDto.CriarMensagemRequest request
    ) {
        return ResponseEntity.ok(mensagemService.criarMensagem(tccId, request));
    }
}

