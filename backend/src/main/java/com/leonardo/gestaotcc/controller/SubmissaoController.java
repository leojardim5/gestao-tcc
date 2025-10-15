package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.SubmissaoDto;
import com.leonardo.gestaotcc.service.SubmissaoService;
import com.leonardo.gestaotcc.service.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Submissões", description = "Gerenciamento de submissões de TCC")
@RestController
@RequestMapping("/api/submissoes")
@RequiredArgsConstructor
public class SubmissaoController {

    private final SubmissaoService submissaoService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "Cria uma nova submissão para um TCC", responses = {
            @ApiResponse(responseCode = "201", description = "Submissão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @PostMapping
    public ResponseEntity<SubmissaoDto.SubmissaoResponse> createSubmissao(@Valid @RequestBody SubmissaoDto.SubmissaoCreateRequest request) {
        SubmissaoDto.SubmissaoResponse response = submissaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Decide o status de uma submissão (APROVADO/REPROVADO)", responses = {
            @ApiResponse(responseCode = "200", description = "Status da submissão atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status de decisão inválido"),
            @ApiResponse(responseCode = "404", description = "Submissão não encontrada")
    })
    @PatchMapping("/{id}/decisao")
    public ResponseEntity<SubmissaoDto.SubmissaoResponse> decideSubmissao(@PathVariable UUID id, @Valid @RequestBody SubmissaoDto.SubmissaoDecisionRequest request) {
        SubmissaoDto.SubmissaoResponse response = submissaoService.decide(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista submissões por ID do TCC", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de submissões retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @GetMapping(params = "tccId")
    public ResponseEntity<Page<SubmissaoDto.SubmissaoResponse>> listSubmissoesByTcc(
            @Parameter(description = "ID do TCC") @RequestParam UUID tccId,
            Pageable pageable) {
        Page<SubmissaoDto.SubmissaoResponse> responsePage = submissaoService.listByTcc(tccId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Busca uma submissão por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Submissão encontrada"),
            @ApiResponse(responseCode = "404", description = "Submissão não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubmissaoDto.SubmissaoResponse> getSubmissaoById(@PathVariable UUID id) {
        SubmissaoDto.SubmissaoResponse response = submissaoService.get(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca a última submissão de um TCC", responses = {
            @ApiResponse(responseCode = "200", description = "Última submissão encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma submissão encontrada para o TCC")
    })
    @GetMapping("/latest")
    public ResponseEntity<SubmissaoDto.SubmissaoResponse> getLatestSubmissaoByTcc(@RequestParam UUID tccId) {
        SubmissaoDto.SubmissaoResponse response = submissaoService.getLatestByTcc(tccId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cria uma submissão com upload de arquivo", responses = {
            @ApiResponse(responseCode = "201", description = "Submissão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou arquivo inválido"),
            @ApiResponse(responseCode = "404", description = "TCC não encontrado")
    })
    @PostMapping("/upload")
    public ResponseEntity<SubmissaoDto.SubmissaoResponse> createSubmissaoWithFile(
            @RequestParam("tccId") UUID tccId,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo não pode estar vazio");
        }

        // Validar tipo de arquivo (apenas PDF)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Apenas arquivos PDF são permitidos");
        }

        // Fazer upload do arquivo
        String fileUrl = fileStorageService.storeFile(file, "submissoes");

        // Criar submissão com o arquivo
        SubmissaoDto.SubmissaoCreateRequest request = new SubmissaoDto.SubmissaoCreateRequest();
        request.setTccId(tccId);
        request.setArquivoUrl(fileUrl);

        SubmissaoDto.SubmissaoResponse response = submissaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
