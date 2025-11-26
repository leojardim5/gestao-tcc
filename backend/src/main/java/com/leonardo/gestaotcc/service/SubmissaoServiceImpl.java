package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.EmailNotificationData;
import com.leonardo.gestaotcc.dto.SubmissaoDto;
import com.leonardo.gestaotcc.entity.Submissao;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.StatusSubmissao;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.SubmissaoMapper;
import com.leonardo.gestaotcc.repository.SubmissaoRepository;
import com.leonardo.gestaotcc.repository.TccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissaoServiceImpl implements SubmissaoService {

    private final SubmissaoRepository submissaoRepository;
    private final TccRepository tccRepository;
    private final SubmissaoMapper submissaoMapper;
    private final NotificacaoService notificacaoService;

    @Override
    @Transactional
    public SubmissaoDto.SubmissaoResponse create(SubmissaoDto.SubmissaoCreateRequest request) {
        Tcc tcc = tccRepository.findById(request.getTccId())
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + request.getTccId()));

        Optional<Submissao> ultimaSubmissao = submissaoRepository.findTopByTccOrderByVersaoDesc(tcc);
        int proximaVersao = ultimaSubmissao.map(s -> s.getVersao() + 1).orElse(1);

        Submissao submissao = submissaoMapper.toEntity(request);
        submissao.setTcc(tcc);
        submissao.setVersao(proximaVersao);
        submissao.setStatus(StatusSubmissao.EM_REVISAO);

        submissao = submissaoRepository.save(submissao);
        
        // Notificar orientador sobre nova submissão
        if (tcc.getOrientador() != null) {
            String mensagem = String.format(
                "Nova submissão (versão %d) recebida para o TCC: %s",
                proximaVersao,
                tcc.getTitulo()
            );
            
            // Preparar dados extras para o email
            EmailNotificationData dadosEmail = EmailNotificationData.builder()
                    .tituloTcc(tcc.getTitulo())
                    .tema(tcc.getTema())
                    .curso(tcc.getCurso())
                    .resumo(tcc.getResumo())
                    .nomeAluno(tcc.getAluno() != null ? tcc.getAluno().getNome() : null)
                    .emailAluno(tcc.getAluno() != null ? tcc.getAluno().getEmail() : null)
                    .nomeOrientador(tcc.getOrientador() != null ? tcc.getOrientador().getNome() : null)
                    .emailOrientador(tcc.getOrientador() != null ? tcc.getOrientador().getEmail() : null)
                    .versao(proximaVersao)
                    .status("EM_REVISAO")
                    .dataHora(LocalDateTime.now())
                    .build();
            
            notificacaoService.push(
                tcc.getOrientador().getId(),
                TipoNotificacao.COMENTARIO,
                mensagem,
                dadosEmail
            );
        }
        
        return submissaoMapper.toResponse(submissao);
    }

    @Override
    @Transactional
    public SubmissaoDto.SubmissaoResponse decide(UUID submissaoId, StatusSubmissao status) {
        if (status != StatusSubmissao.APROVADO && status != StatusSubmissao.REPROVADO) {
            throw new BusinessException("Status de decisão inválido. Apenas APROVADO ou REPROVADO são permitidos.");
        }

        Submissao submissao = submissaoRepository.findById(submissaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada com ID: " + submissaoId));

        submissao.setStatus(status);
        submissao = submissaoRepository.save(submissao);
        
        // Notificar aluno sobre a decisão da submissão
        Tcc tcc = submissao.getTcc();
        if (tcc != null && tcc.getAluno() != null) {
            String statusTexto = status == StatusSubmissao.APROVADO ? "aprovada" : "reprovada";
            String mensagem = String.format(
                "Sua submissão (versão %d) do TCC '%s' foi %s",
                submissao.getVersao(),
                tcc.getTitulo(),
                statusTexto
            );
            
            // Preparar dados extras para o email
            EmailNotificationData dadosEmail = EmailNotificationData.builder()
                    .tituloTcc(tcc.getTitulo())
                    .tema(tcc.getTema())
                    .curso(tcc.getCurso())
                    .resumo(tcc.getResumo())
                    .nomeAluno(tcc.getAluno() != null ? tcc.getAluno().getNome() : null)
                    .emailAluno(tcc.getAluno() != null ? tcc.getAluno().getEmail() : null)
                    .nomeOrientador(tcc.getOrientador() != null ? tcc.getOrientador().getNome() : null)
                    .emailOrientador(tcc.getOrientador() != null ? tcc.getOrientador().getEmail() : null)
                    .versao(submissao.getVersao())
                    .status(status.toString())
                    .dataHora(LocalDateTime.now())
                    .build();
            
            notificacaoService.push(
                tcc.getAluno().getId(),
                TipoNotificacao.COMENTARIO,
                mensagem,
                dadosEmail
            );
        }
        
        return submissaoMapper.toResponse(submissao);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissaoDto.SubmissaoResponse> listByTcc(UUID tccId, Pageable pageable) {
        Tcc tcc = tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + tccId));

        List<Submissao> submissoes = submissaoRepository.findByTccOrderByVersaoAsc(tcc);
        List<SubmissaoDto.SubmissaoResponse> dtoList = submissoes.stream()
                .map(submissaoMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        Page<SubmissaoDto.SubmissaoResponse> page = new PageImpl<>(dtoList.subList(start, end), pageable, dtoList.size());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissaoDto.SubmissaoResponse get(UUID id) {
        Submissao submissao = submissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada com ID: " + id));
        return submissaoMapper.toResponse(submissao);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissaoDto.SubmissaoResponse getLatestByTcc(UUID tccId) {
        Tcc tcc = tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + tccId));
        Submissao submissao = submissaoRepository.findTopByTccOrderByVersaoDesc(tcc)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma submissão encontrada para o TCC com ID: " + tccId));
        return submissaoMapper.toResponse(submissao);
    }
}