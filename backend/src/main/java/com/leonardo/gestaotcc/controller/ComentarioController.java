package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.ComentarioDto;
import com.leonardo.gestaotcc.service.ComentarioService;
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

@Tag(name = "Comentários", description = "Gerenciamento de comentários em submissões")
@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @Operation(summary = "Adiciona um novo comentário a uma submissão", responses = {
            @ApiResponse(responseCode = "201", description = "Comentário adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Submissão ou Autor não encontrado")
    })
    @PostMapping
    public ResponseEntity<ComentarioDto.ComentarioResponse> addComentario(@Valid @RequestBody ComentarioDto.ComentarioCreateRequest request) {
        ComentarioDto.ComentarioResponse response = comentarioService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Exclui um comentário", responses = {
            @ApiResponse(responseCode = "204", description = "Comentário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable UUID id) {
        comentarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista comentários por ID da submissão", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Submissão não encontrada")
    })
    @GetMapping(params = "submissaoId")
    public ResponseEntity<Page<ComentarioDto.ComentarioResponse>> listComentariosBySubmissao(
            @Parameter(description = "ID da submissão") @RequestParam UUID submissaoId,
            Pageable pageable) {
        Page<ComentarioDto.ComentarioResponse> responsePage = comentarioService.listBySubmissao(submissaoId, pageable);
        return ResponseEntity.ok(responsePage);
    }
}
