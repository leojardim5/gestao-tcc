package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Upload de Arquivos", description = "Endpoints para upload e gerenciamento de arquivos")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload de arquivo", description = "Faz upload de um arquivo para o servidor")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subfolder", defaultValue = "general") String subfolder) {
        
        if (file.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Arquivo vazio");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String fileUrl = fileStorageService.storeFile(file, subfolder);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Arquivo enviado com sucesso");
            response.put("fileUrl", fileUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao fazer upload: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Deletar arquivo", description = "Remove um arquivo do servidor")
    public ResponseEntity<Map<String, String>> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            fileStorageService.deleteFile(fileUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Arquivo deletado com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao deletar arquivo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
