package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.ia.AiSuggestionDto;
import com.leonardo.gestaotcc.service.IaSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "IA", description = "Integração com IA para recomendação de orientadores")
@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IaController {

    private final IaSuggestionService iaSuggestionService;

    @Operation(
            summary = "Sugere orientadores com apoio de IA",
            description = "Utiliza a IA do Cursor para ranquear orientadores disponíveis com base nas informações do TCC."
    )
    @ApiResponse(responseCode = "200", description = "Sugestões retornadas com sucesso")
    @PostMapping("/orientadores/sugerir")
    public ResponseEntity<AiSuggestionDto.SuggestionResponse> sugerirOrientadores(
            @Valid @RequestBody AiSuggestionDto.SuggestionRequest request
    ) {
        System.out.println("🎯 ========== BACKEND: IaController.sugerirOrientadores CHAMADO ==========");
        System.out.println("📦 Request recebido:");
        System.out.println("  - Aluno ID: " + request.getAlunoId());
        System.out.println("  - Aluno Nome: " + request.getAlunoNome());
        System.out.println("  - Curso: " + request.getCurso());
        System.out.println("  - Título: " + request.getTitulo());
        System.out.println("  - Tema: " + request.getTema());
        System.out.println("  - Resumo: " + request.getResumo());
        System.out.println("  - Mensagem: " + request.getMensagem());
        System.out.println("  - Palavras-chave: " + (request.getPalavrasChave() != null ? request.getPalavrasChave() : "null"));
        System.out.println("⏰ Timestamp: " + java.time.LocalDateTime.now());
        
        try {
            AiSuggestionDto.SuggestionResponse response = iaSuggestionService.sugerirOrientadores(request);
            System.out.println("✅ BACKEND: Resposta gerada com sucesso!");
            System.out.println("  - Modelo: " + response.getModelo());
            System.out.println("  - Mensagem Sistema: " + response.getMensagemSistema());
            System.out.println("  - Número de sugestões: " + (response.getSugestoes() != null ? response.getSugestoes().size() : 0));
            if (response.getSugestoes() != null) {
                response.getSugestoes().forEach((sug, idx) -> {
                    System.out.println("    Sugestão " + (idx + 1) + ":");
                    System.out.println("      - ID: " + sug.getOrientadorId());
                    System.out.println("      - Nome: " + sug.getOrientadorNome());
                    System.out.println("      - Score: " + sug.getScore());
                    System.out.println("      - Justificativa: " + sug.getJustificativa());
                });
            }
            System.out.println("🎯 ========== FIM BACKEND CONTROLLER ==========");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ BACKEND: Erro no controller!");
            System.err.println("🚨 Exceção: " + e.getClass().getName());
            System.err.println("💬 Mensagem: " + e.getMessage());
            e.printStackTrace();
            System.err.println("🎯 ========== FIM ERRO CONTROLLER ==========");
            throw e;
        }
    }
}

