package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.UsuarioDto;
import com.leonardo.gestaotcc.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Usuários", description = "Gerenciamento de usuários")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Cria um novo usuário", responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    @PostMapping
    public ResponseEntity<UsuarioDto.UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioDto.UsuarioCreateRequest request) {
        UsuarioDto.UsuarioResponse response = usuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualiza um usuário existente", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto.UsuarioResponse> updateUsuario(@PathVariable UUID id, @Valid @RequestBody UsuarioDto.UsuarioUpdateRequest request) {
        UsuarioDto.UsuarioResponse response = usuarioService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativa um usuário", responses = {
            @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUsuario(@PathVariable UUID id) {
        usuarioService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca um usuário por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto.UsuarioResponse> getUsuarioById(@PathVariable UUID id) {
        UsuarioDto.UsuarioResponse response = usuarioService.get(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista todos os usuários paginados", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<UsuarioDto.UsuarioResponse>> listUsuarios(Pageable pageable) {
        Page<UsuarioDto.UsuarioResponse> responsePage = usuarioService.list(pageable);
        return ResponseEntity.ok(responsePage);
    }
}
