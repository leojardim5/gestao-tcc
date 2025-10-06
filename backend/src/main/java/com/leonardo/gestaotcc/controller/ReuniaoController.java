package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.ReuniaoDto;
import com.leonardo.gestaotcc.service.ReuniaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Reuniões", description = "Gerenciamento de reuniões de TCC")
@RestController
@RequestMapping("/api/reunioes")
@RequiredArgsConstructor
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    @Operation(summary = "Agenda uma nova reunião", responses = {
            @ApiResponse(responseCode = "201", description = "Reunião agendada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou conflito de horário"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @PostMapping
    public ResponseEntity<ReuniaoDto.ReuniaoResponse> scheduleReuniao(@Valid @RequestBody ReuniaoDto.ReuniaoCreateRequest request) {
        ReuniaoDto.ReuniaoResponse response = reuniaoService.schedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cancela uma reunião", responses = {
            @ApiResponse(responseCode = "204", description = "Reunião cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reunião não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReuniao(@PathVariable UUID id) {
        reuniaoService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista reuniões por ID do TCC", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de reuniões retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @GetMapping(params = "tccId")
    public ResponseEntity<Page<ReuniaoDto.ReuniaoResponse>> listReunioesByTcc(
            @Parameter(description = "ID do TCC") @RequestParam UUID tccId,
            Pageable pageable) {
        Page<ReuniaoDto.ReuniaoResponse> responsePage = reuniaoService.listByTcc(tccId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Busca uma reunião por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Reunião encontrada"),
            @ApiResponse(responseCode = "404", description = "Reunião não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoDto.ReuniaoResponse> getReuniaoById(@PathVariable UUID id) {
        ReuniaoDto.ReuniaoResponse response = reuniaoService.get(id);
        return ResponseEntity.ok(response);
    }
}
