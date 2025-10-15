package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.DashboardDto;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.enums.StatusSubmissao;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.SubmissaoRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TccRepository tccRepository;
    private final SubmissaoRepository submissaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoRepository notificacaoRepository;

    public DashboardDto getDashboardStats() {
        // Estatísticas de TCCs
        long totalTccs = tccRepository.count();
        long tccsRascunho = tccRepository.countByStatus(StatusTcc.RASCUNHO);
        long tccsEmAndamento = tccRepository.countByStatus(StatusTcc.EM_ANDAMENTO);
        long tccsAguardandoDefesa = tccRepository.countByStatus(StatusTcc.AGUARDANDO_DEFESA);
        long tccsConcluidos = tccRepository.countByStatus(StatusTcc.CONCLUIDO);

        // Estatísticas de Submissões
        long totalSubmissoes = submissaoRepository.count();
        long submissoesEmRevisao = submissaoRepository.countByStatus(StatusSubmissao.EM_REVISAO);
        long submissoesAprovadas = submissaoRepository.countByStatus(StatusSubmissao.APROVADO);
        long submissoesReprovadas = submissaoRepository.countByStatus(StatusSubmissao.REPROVADO);

        // Estatísticas de Usuários
        long totalUsuarios = usuarioRepository.count();
        long usuariosAtivos = usuarioRepository.countByAtivoTrue();

        // Notificações não lidas
        long notificacoesNaoLidas = notificacaoRepository.countByLidaFalse();

        // TCCs próximos do prazo (próximos 30 dias)
        LocalDate prazoLimite = LocalDate.now().plusDays(30);
        long tccsProximosPrazo = tccRepository.countByDataEntregaPrevistaBetweenAndStatus(
                LocalDate.now(), prazoLimite, StatusTcc.EM_ANDAMENTO);

        // Criar mapa de estatísticas
        Map<String, Object> stats = new HashMap<>();
        stats.put("tccs", Map.of(
                "total", totalTccs,
                "rascunho", tccsRascunho,
                "emAndamento", tccsEmAndamento,
                "aguardandoDefesa", tccsAguardandoDefesa,
                "concluidos", tccsConcluidos,
                "proximosPrazo", tccsProximosPrazo
        ));

        stats.put("submissoes", Map.of(
                "total", totalSubmissoes,
                "emRevisao", submissoesEmRevisao,
                "aprovadas", submissoesAprovadas,
                "reprovadas", submissoesReprovadas
        ));

        stats.put("usuarios", Map.of(
                "total", totalUsuarios,
                "ativos", usuariosAtivos
        ));

        stats.put("notificacoes", Map.of(
                "naoLidas", notificacoesNaoLidas
        ));

        return DashboardDto.builder()
                .stats(stats)
                .build();
    }

    public Map<String, Long> getTccsByStatus() {
        Map<String, Long> statusCount = new HashMap<>();
        for (StatusTcc status : StatusTcc.values()) {
            statusCount.put(status.name(), tccRepository.countByStatus(status));
        }
        return statusCount;
    }

    public Map<String, Long> getSubmissoesByStatus() {
        Map<String, Long> statusCount = new HashMap<>();
        for (StatusSubmissao status : StatusSubmissao.values()) {
            statusCount.put(status.name(), submissaoRepository.countByStatus(status));
        }
        return statusCount;
    }
}
