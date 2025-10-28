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
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.security.CustomUserDetails; // Assuming this class exists
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Tag(name = "TCCs", description = "Gerenciamento de Trabalhos de Conclusão de Curso")
@RestController
@RequestMapping("/api/tccs")
@RequiredArgsConstructor
public class TccController {

    private final TccService tccService;
    private final UsuarioRepository usuarioRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

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
    public ResponseEntity<TccDto.TccResponse> getTccById(@PathVariable UUID id, Authentication authentication) {
        UUID authenticatedUserId = null;
        PapelUsuario authenticatedUserRole = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                authenticatedUserId = userDetails.getId();
                authenticatedUserRole = userDetails.getPapel();
            } else if (principal instanceof String) {
                String email = (String) principal;
                try {
                    Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
                    authenticatedUserId = usuario.getId();
                    authenticatedUserRole = usuario.getPapel();
                } catch (Exception e) {
                    System.out.println("Erro ao carregar usuário por email: " + e.getMessage());
                }
            }
        }

        TccDto.TccResponse response = tccService.get(id, authenticatedUserId, authenticatedUserRole);
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

    @Operation(summary = "Deleta um TCC", responses = {
            @ApiResponse(responseCode = "204", description = "TCC deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para deletar este TCC")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTcc(@PathVariable UUID id) {
        tccService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/debug")
    public ResponseEntity<Page<TccDto.TccResponse>> debugAllTccs(Pageable pageable) {
        Page<TccDto.TccResponse> responsePage = tccService.listAll(pageable, null, null);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/create-test-users")
    public ResponseEntity<String> createTestUsers() {
        try {
            // Criar usuários de teste
                jdbcTemplate.execute("""
                    INSERT INTO usuarios (id, nome, email, senha_hash, papel, ativo, disponivel_para_orientacao) VALUES
                    (gen_random_uuid(), 'João Silva Aluno', 'joao.aluno@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', true, false),
                    (gen_random_uuid(), 'Maria Santos Aluna', 'maria.aluna@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ALUNO', true, false),
                    (gen_random_uuid(), 'Prof. Carlos Orientador', 'carlos.orientador@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, true),
                    (gen_random_uuid(), 'Prof. Ana Orientadora', 'ana.orientadora@teste.com', '$2a$10$3Z.dY4f.N1s/C2A8p.rJ5ee3c2G.fS2T6ED3.N.GN.dF.j2E.aB.G', 'ORIENTADOR', true, true)
                    """);
            return ResponseEntity.ok("Usuários de teste criados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar usuários: " + e.getMessage());
        }
    }

    @GetMapping("/test-login")
    public ResponseEntity<String> testLogin() {
        try {
            // Testar se o usuário existe
            var usuario = usuarioRepository.findByEmail("joao.aluno@teste.com");
            if (usuario.isPresent()) {
                return ResponseEntity.ok("Usuário encontrado: " + usuario.get().getNome() + " - Hash: " + usuario.get().getSenhaHash());
            } else {
                return ResponseEntity.ok("Usuário não encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<TccDto.TccResponse>> listAllTccs(Pageable pageable, Authentication authentication) {
        UUID authenticatedUserId = null;
        PapelUsuario authenticatedUserRole = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            System.out.println("DEBUG: Principal type: " + principal.getClass().getName());
            System.out.println("DEBUG: Principal: " + principal);
            
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                authenticatedUserId = userDetails.getId();
                authenticatedUserRole = userDetails.getPapel();
                System.out.println("DEBUG: Found CustomUserDetails - ID: " + authenticatedUserId + ", Role: " + authenticatedUserRole);
            } else if (principal instanceof String) {
                // Principal is the email (username) - load user details
                String email = (String) principal;
                System.out.println("DEBUG: Found String principal - Email: " + email);
                try {
                    Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
                    authenticatedUserId = usuario.getId();
                    authenticatedUserRole = usuario.getPapel();
                    System.out.println("DEBUG: Loaded user from email - ID: " + authenticatedUserId + ", Role: " + authenticatedUserRole);
                } catch (Exception e) {
                    System.out.println("Erro ao carregar usuário por email: " + e.getMessage());
                }
            } else {
                System.out.println("DEBUG: Principal is neither CustomUserDetails nor String - type: " + principal.getClass().getName());
            }
        } else {
            System.out.println("DEBUG: Authentication is null or not authenticated");
        }

        System.out.println("DEBUG: Final authenticatedUserId: " + authenticatedUserId + ", authenticatedUserRole: " + authenticatedUserRole);
        Page<TccDto.TccResponse> responsePage = tccService.listAll(pageable, authenticatedUserId, authenticatedUserRole);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/test-password")
    public ResponseEntity<String> testPassword() {
        try {
            Usuario usuario = usuarioRepository.findByEmail("joao.aluno@teste.com")
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            
            // Testar se a senha "password" corresponde ao hash
            boolean matches = passwordEncoder.matches("password", usuario.getSenhaHash());
            
            return ResponseEntity.ok("Usuário: " + usuario.getNome() + 
                    " - Hash: " + usuario.getSenhaHash() + 
                    " - Senha 'password' matches: " + matches);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
}