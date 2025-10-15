package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.service.TccService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.security.CustomUserDetails; // Assuming this class exists

@Tag(name = "TCCs", description = "Gerenciamento de Trabalhos de Conclusão de Curso")
@RestController
@RequestMapping("/tccs")
@RequiredArgsConstructor
public class TccController {

    private final TccService tccService;

    @Operation(summary = "Cria um novo TCC", responses = {
            @ApiResponse(responseCode = "201", description = "TCC criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Aluno ou Orientador não encontrado"),
            @ApiResponse(responseCode = "409", description = "Aluno já possui um TCC ativo")
    })
    @PostMapping
    public ResponseEntity<TccDto.TccResponse> createTcc(@Valid @RequestBody TccDto.TccCreateRequest request) {
        TccDto.TccResponse response = tccService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualiza um TCC existente", responses = {
            @ApiResponse(responseCode = "200", description = "TCC atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "TCC, Orientador ou Coorientador não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TccDto.TccResponse> updateTcc(@PathVariable UUID id, @Valid @RequestBody TccDto.TccUpdateRequest request) {
        TccDto.TccResponse response = tccService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Altera o status de um TCC", responses = {
            @ApiResponse(responseCode = "200", description = "Status do TCC alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TccDto.TccResponse> changeTccStatus(@PathVariable UUID id, @RequestParam StatusTcc newStatus) {
        TccDto.TccResponse response = tccService.changeStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atribui um orientador a um TCC", responses = {
            @ApiResponse(responseCode = "200", description = "Orientador atribuído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não é um orientador ou coordenador"),
            @ApiResponse(responseCode = "404", description = "TCC ou Orientador não encontrado")
    })
    @PatchMapping("/{tccId}/orientador/{orientadorId}")
    public ResponseEntity<TccDto.TccResponse> assignOrientador(@PathVariable UUID tccId, @PathVariable UUID orientadorId) {
        TccDto.TccResponse response = tccService.assignOrientador(tccId, orientadorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca um TCC por ID", responses = {
            @ApiResponse(responseCode = "200", description = "TCC encontrado"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TccDto.TccResponse> getTccById(@PathVariable UUID id) {
        TccDto.TccResponse response = tccService.get(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista TCCs por ID de usuário (aluno ou orientador)", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de TCCs retornada com sucesso")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<TccDto.TccResponse>> listTccsByUsuario(
            @Parameter(description = "ID do usuário (pode ser Aluno ou Orientador)") @RequestParam UUID usuarioId,
            Pageable pageable) {
        Page<TccDto.TccResponse> responsePage = tccService.listByUsuario(usuarioId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Lista todos os TCCs paginados", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de TCCs retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<TccDto.TccResponse>> listAllTccs(Pageable pageable, Authentication authentication) {
        UUID authenticatedUserId = null;
        PapelUsuario authenticatedUserRole = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                authenticatedUserId = userDetails.getId();
                authenticatedUserRole = userDetails.getPapel();
            } else {
                // Handle cases where principal is not CustomUserDetails (e.g., anonymous user, String username)
                // For now, we'll leave authenticatedUserId and authenticatedUserRole as null
            }
        }

        Page<TccDto.TccResponse> responsePage = tccService.listAll(pageable, authenticatedUserId, authenticatedUserRole);
        return ResponseEntity.ok(responsePage);
    }
}
