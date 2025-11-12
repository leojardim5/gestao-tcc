package com.leonardo.gestaotcc.service.impl;

import com.leonardo.gestaotcc.dto.cronograma.CronogramaEtapaDto;
import com.leonardo.gestaotcc.dto.cronograma.CronogramaResumoDto;
import com.leonardo.gestaotcc.entity.CronogramaEtapa;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.enums.StatusCronogramaEtapa;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.CronogramaEtapaMapper;
import com.leonardo.gestaotcc.repository.CronogramaEtapaRepository;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.security.SecurityUtils;
import com.leonardo.gestaotcc.service.CronogramaEtapaService;
import com.leonardo.gestaotcc.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CronogramaEtapaServiceImpl implements CronogramaEtapaService {

    private final CronogramaEtapaRepository cronogramaEtapaRepository;
    private final TccRepository tccRepository;
    private final CronogramaEtapaMapper mapper;
    private final NotificacaoService notificacaoService;

    @Override
    @Transactional(readOnly = true)
    public List<CronogramaEtapaDto.EtapaResponse> listarPorTcc(UUID tccId) {
        Tcc tcc = buscarTcc(tccId);
        garantirAcessoVisualizacao(tcc);
        return cronogramaEtapaRepository.findByTccOrderByDataInicioAsc(tcc).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CronogramaEtapaDto.EtapaResponse criarOuAtualizarEtapa(UUID tccId, CronogramaEtapaDto.EtapaCreateRequest request) {
        Tcc tcc = buscarTcc(tccId);
        garantirAcessoOrientadorOuCoordenador(tcc);

        CronogramaEtapa etapa = mapper.toEntity(request);
        etapa.setTcc(tcc);
        etapa = cronogramaEtapaRepository.save(etapa);
        return mapper.toResponse(etapa);
    }

    @Override
    @Transactional
    public CronogramaEtapaDto.EtapaResponse atualizarStatus(UUID tccId, UUID etapaId, StatusCronogramaEtapa novoStatus, String observacao) {
        CronogramaEtapa etapa = buscarEtapaDoTcc(tccId, etapaId);
        Usuario usuario = SecurityUtils.getCurrentUserDetails()
                .orElseThrow(() -> new ResourceNotFoundException("Etapa não encontrada"));

        validarTransicao(etapa, novoStatus, observacao, usuario);

        StatusCronogramaEtapa statusAnterior = etapa.getStatus();

        etapa.setStatus(novoStatus);
        if (novoStatus == StatusCronogramaEtapa.CONCLUIDO) {
            etapa.setConcluidoEm(LocalDateTime.now());
        } else if (statusAnterior == StatusCronogramaEtapa.CONCLUIDO) {
            etapa.setConcluidoEm(null);
        }

        if (observacao != null && !observacao.isBlank()) {
            etapa.setObservacao(observacao);
        }

        etapa = cronogramaEtapaRepository.save(etapa);

        tratarNotificacoes(etapa, statusAnterior, novoStatus, usuario.getPapel());

        return mapper.toResponse(etapa);
    }

    @Override
    @Transactional(readOnly = true)
    public CronogramaResumoDto obterResumo(UUID tccId) {
        Map<UUID, CronogramaResumoDto> resumos = obterResumos(List.of(tccId));
        return resumos.getOrDefault(tccId, CronogramaResumoDto.builder()
                .tccId(tccId)
                .total(0)
                .pendentes(0)
                .emAndamento(0)
                .concluidas(0)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, CronogramaResumoDto> obterResumos(List<UUID> tccIds) {
        if (tccIds == null || tccIds.isEmpty()) {
            return Map.of();
        }

        List<UUID> distinctIds = tccIds.stream().distinct().toList();
        Set<UUID> tccsPermitidos = distinctIds.stream()
                .map(this::buscarTcc)
                .filter(this::temPermissaoDeVisualizacao)
                .map(Tcc::getId)
                .collect(Collectors.toSet());

        if (tccsPermitidos.isEmpty()) {
            return Map.of();
        }

        List<CronogramaEtapaRepository.CronogramaStatusCount> counts = cronogramaEtapaRepository.countByTccIds(distinctIds);
        Map<UUID, CronogramaResumoDto> resultado = new HashMap<>();

        tccsPermitidos.forEach(id -> resultado.put(id, CronogramaResumoDto.builder()
                .tccId(id)
                .total(0)
                .pendentes(0)
                .emAndamento(0)
                .concluidas(0)
                .build()));

        for (CronogramaEtapaRepository.CronogramaStatusCount registro : counts) {
            UUID tccId = registro.getTccId();
            if (!resultado.containsKey(tccId)) {
                continue;
            }
            CronogramaResumoDto dto = resultado.get(tccId);
            long quantidade = registro.getQuantidade();
            dto.setTotal(dto.getTotal() + quantidade);
            switch (registro.getStatus()) {
                case PENDENTE, ATRASADA -> dto.setPendentes(dto.getPendentes() + quantidade);
                case EM_ANDAMENTO -> dto.setEmAndamento(dto.getEmAndamento() + quantidade);
                case CONCLUIDO -> dto.setConcluidas(dto.getConcluidas() + quantidade);
            }
        }

        return resultado;
    }

    private Tcc buscarTcc(UUID tccId) {
        return tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado"));
    }

    private CronogramaEtapa buscarEtapaDoTcc(UUID tccId, UUID etapaId) {
        CronogramaEtapa etapa = cronogramaEtapaRepository.findById(etapaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etapa do cronograma não encontrada"));
        if (!etapa.getTcc().getId().equals(tccId)) {
            throw new ResourceNotFoundException("Etapa não pertence ao TCC informado");
        }
        return etapa;
    }

    private void garantirAcessoVisualizacao(Tcc tcc) {
        if (!temPermissaoDeVisualizacao(tcc)) {
            throw new ResourceNotFoundException("TCC não encontrado");
        }
    }

    private boolean temPermissaoDeVisualizacao(Tcc tcc) {
        var usuarioOpt = SecurityUtils.getCurrentUserDetails();
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        var usuario = usuarioOpt.get();
        if (usuario.getPapel() == PapelUsuario.COORDENADOR) {
            return true;
        }
        UUID userId = usuario.getId();
        return switch (usuario.getPapel()) {
            case ALUNO -> tcc.getAluno() != null && tcc.getAluno().getId().equals(userId);
            case ORIENTADOR -> (tcc.getOrientador() != null && tcc.getOrientador().getId().equals(userId))
                    || (tcc.getCoorientador() != null && tcc.getCoorientador().getId().equals(userId));
            default -> false;
        };
    }

    private void garantirAcessoOrientadorOuCoordenador(Tcc tcc) {
        var usuarioOpt = SecurityUtils.getCurrentUserDetails();
        if (usuarioOpt.isEmpty()) {
            throw new ResourceNotFoundException("Etapa não encontrada");
        }
        var usuario = usuarioOpt.get();
        if (usuario.getPapel() == PapelUsuario.COORDENADOR) {
            return;
        }
        boolean permitido = usuario.getPapel() == PapelUsuario.ORIENTADOR
                && (
                (tcc.getOrientador() != null && tcc.getOrientador().getId().equals(usuario.getId()))
                        || (tcc.getCoorientador() != null && tcc.getCoorientador().getId().equals(usuario.getId()))
        );
        if (!permitido) {
            throw new ResourceNotFoundException("Etapa não encontrada");
        }
    }

    private void validarTransicao(CronogramaEtapa etapa,
                                  StatusCronogramaEtapa novoStatus,
                                  String observacao,
                                  Usuario usuario) {
        StatusCronogramaEtapa statusAtual = etapa.getStatus();
        PapelUsuario papelUsuario = usuario.getPapel();

        if (papelUsuario == PapelUsuario.ALUNO) {
            if (etapa.getTcc().getAluno() == null
                    || !etapa.getTcc().getAluno().getId().equals(usuario.getId())) {
                throw new ResourceNotFoundException("Etapa não encontrada");
            }
            boolean podeIniciar = (statusAtual == StatusCronogramaEtapa.PENDENTE
                    || statusAtual == StatusCronogramaEtapa.ATRASADA)
                    && novoStatus == StatusCronogramaEtapa.EM_ANDAMENTO;
            boolean podeConcluir = statusAtual == StatusCronogramaEtapa.EM_ANDAMENTO
                    && novoStatus == StatusCronogramaEtapa.CONCLUIDO;

            if (podeIniciar || podeConcluir) {
                return;
            }

            throw new BusinessException("Você não pode mover a etapa para este status.");
        }

        if (papelUsuario == PapelUsuario.ORIENTADOR) {
            boolean autorizado = (etapa.getTcc().getOrientador() != null && etapa.getTcc().getOrientador().getId().equals(usuario.getId()))
                    || (etapa.getTcc().getCoorientador() != null && etapa.getTcc().getCoorientador().getId().equals(usuario.getId()));
            if (!autorizado) {
                throw new ResourceNotFoundException("Etapa não encontrada");
            }
            if (statusAtual == StatusCronogramaEtapa.CONCLUIDO
                    && novoStatus == StatusCronogramaEtapa.EM_ANDAMENTO
                    && (observacao == null || observacao.isBlank())) {
                throw new BusinessException("Informe uma observação ao devolver uma etapa para Fazendo.");
            }
            // Orientador pode mover para qualquer status
            return;
        }

        if (papelUsuario == PapelUsuario.COORDENADOR) {
            return;
        }

        throw new ResourceNotFoundException("Etapa não encontrada");
    }

    private void tratarNotificacoes(CronogramaEtapa etapa,
                                    StatusCronogramaEtapa statusAnterior,
                                    StatusCronogramaEtapa novoStatus,
                                    PapelUsuario papelAtual) {
        Tcc tcc = etapa.getTcc();

        if (novoStatus == StatusCronogramaEtapa.CONCLUIDO && papelAtual == PapelUsuario.ALUNO) {
            if (tcc.getOrientador() != null && tcc.getAluno() != null) {
                notificacaoService.push(
                        tcc.getOrientador().getId(),
                        TipoNotificacao.SISTEMA,
                        String.format("O aluno %s marcou a etapa '%s' como concluída.", tcc.getAluno().getNome(), etapa.getNome())
                );
            }
            return;
        }

        if (statusAnterior == StatusCronogramaEtapa.CONCLUIDO
                && novoStatus != StatusCronogramaEtapa.CONCLUIDO
                && papelAtual == PapelUsuario.ORIENTADOR
                && tcc.getAluno() != null
                && tcc.getOrientador() != null) {
            notificacaoService.push(
                    tcc.getAluno().getId(),
                    TipoNotificacao.SISTEMA,
                    String.format("O orientador %s solicitou ajustes na etapa '%s'.", tcc.getOrientador().getNome(), etapa.getNome())
            );
        }
    }
}
