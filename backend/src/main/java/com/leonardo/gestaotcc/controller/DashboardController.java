package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.DashboardDto;
import com.leonardo.gestaotcc.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Estatísticas e métricas do sistema")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas gerais", description = "Retorna estatísticas completas do sistema")
    public ResponseEntity<DashboardDto> getDashboardStats() {
        DashboardDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/tccs-by-status")
    @Operation(summary = "TCCs por status", description = "Retorna contagem de TCCs agrupados por status")
    public ResponseEntity<Map<String, Long>> getTccsByStatus() {
        Map<String, Long> stats = dashboardService.getTccsByStatus();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/submissoes-by-status")
    @Operation(summary = "Submissões por status", description = "Retorna contagem de submissões agrupadas por status")
    public ResponseEntity<Map<String, Long>> getSubmissoesByStatus() {
        Map<String, Long> stats = dashboardService.getSubmissoesByStatus();
        return ResponseEntity.ok(stats);
    }
}
