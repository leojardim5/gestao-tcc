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

import javax.validation.Valid;

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
        return ResponseEntity.ok(iaSuggestionService.sugerirOrientadores(request));
    }
}

