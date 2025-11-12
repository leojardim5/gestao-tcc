package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.cronograma.CronogramaEtapaDto;
import com.leonardo.gestaotcc.dto.cronograma.CronogramaResumoDto;
import com.leonardo.gestaotcc.service.CronogramaEtapaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tccs/{tccId}/cronograma")
@RequiredArgsConstructor
public class CronogramaEtapaController {

    private final CronogramaEtapaService cronogramaEtapaService;

    @GetMapping
    @Operation(summary = "Lista o cronograma de etapas do TCC")
    public ResponseEntity<List<CronogramaEtapaDto.EtapaResponse>> listar(@PathVariable UUID tccId) {
        return ResponseEntity.ok(cronogramaEtapaService.listarPorTcc(tccId));
    }

    @PostMapping("/etapas")
    @PreAuthorize("hasAnyRole('ORIENTADOR','COORDENADOR')")
    @Operation(summary = "Cria uma nova etapa para o cronograma")
    public ResponseEntity<CronogramaEtapaDto.EtapaResponse> criarEtapa(
            @PathVariable UUID tccId,
            @Valid @RequestBody CronogramaEtapaDto.EtapaCreateRequest request
    ) {
        CronogramaEtapaDto.EtapaResponse response = cronogramaEtapaService.criarOuAtualizarEtapa(tccId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/etapas/{etapaId}/status")
    @PreAuthorize("hasAnyRole('ALUNO','ORIENTADOR','COORDENADOR')")
    @Operation(summary = "Atualiza o status de uma etapa do cronograma")
    public ResponseEntity<CronogramaEtapaDto.EtapaResponse> atualizarStatus(
            @PathVariable UUID tccId,
            @PathVariable UUID etapaId,
            @Valid @RequestBody CronogramaEtapaDto.AtualizarStatusRequest request
    ) {
        CronogramaEtapaDto.EtapaResponse response = cronogramaEtapaService.atualizarStatus(
                tccId,
                etapaId,
                request.getStatus(),
                request.getObservacao()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumo")
    @Operation(summary = "Retorna um resumo das etapas do cronograma")
    public ResponseEntity<CronogramaResumoDto> resumo(@PathVariable UUID tccId) {
        return ResponseEntity.ok(cronogramaEtapaService.obterResumo(tccId));
    }
}

