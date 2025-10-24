package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.convite.ConviteResponseDto;
import com.leonardo.gestaotcc.dto.convite.EnviarConviteDto;
import com.leonardo.gestaotcc.dto.convite.ResponderConviteDto;
import com.leonardo.gestaotcc.service.ConviteOrientacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Convites de Orientação", description = "Gerenciamento de convites de orientação de TCC")
@RestController
@RequestMapping("/api/convites")
@RequiredArgsConstructor
public class ConviteOrientacaoController {

    private final ConviteOrientacaoService conviteOrientacaoService;

    @Operation(summary = "Enviar um convite de orientação", description = "Permite que um aluno envie um convite de orientação para um professor.")
    @PostMapping("/aluno/{alunoId}")
    public ResponseEntity<ConviteResponseDto> enviarConvite(@PathVariable UUID alunoId, @Valid @RequestBody EnviarConviteDto dto) {
        ConviteResponseDto response = conviteOrientacaoService.enviarConvite(dto, alunoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Responder a um convite de orientação", description = "Permite que um orientador aceite ou rejeite um convite de orientação.")
    @PutMapping("/orientador/{orientadorId}/convite/{conviteId}/responder")
    public ResponseEntity<ConviteResponseDto> responderConvite(@PathVariable UUID orientadorId, @PathVariable UUID conviteId, @Valid @RequestBody ResponderConviteDto dto) {
        ConviteResponseDto response = conviteOrientacaoService.responderConvite(conviteId, dto, orientadorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar convites pendentes para um orientador", description = "Retorna uma lista paginada de convites de orientação pendentes para um orientador específico.")
    @GetMapping("/orientador/{orientadorId}/pendentes")
    public ResponseEntity<Page<ConviteResponseDto>> listarConvitesPendentesOrientador(@PathVariable UUID orientadorId, Pageable pageable) {
        Page<ConviteResponseDto> responsePage = conviteOrientacaoService.listarConvitesPendentesPorOrientador(orientadorId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Listar convites enviados por um aluno", description = "Retorna uma lista paginada de convites de orientação enviados por um aluno específico.")
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<Page<ConviteResponseDto>> listarConvitesPorAluno(@PathVariable UUID alunoId, Pageable pageable) {
        Page<ConviteResponseDto> responsePage = conviteOrientacaoService.listarConvitesPorAluno(alunoId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Listar todos os convites para um orientador", description = "Retorna uma lista paginada de todos os convites de orientação (pendentes, aceitos, rejeitados) para um orientador específico.")
    @GetMapping("/orientador/{orientadorId}")
    public ResponseEntity<Page<ConviteResponseDto>> listarTodosConvitesOrientador(@PathVariable UUID orientadorId, Pageable pageable) {
        Page<ConviteResponseDto> responsePage = conviteOrientacaoService.listarConvitesPorOrientador(orientadorId, pageable);
        return ResponseEntity.ok(responsePage);
    }
}
