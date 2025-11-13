package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.convite.ConviteResponseDto;
import com.leonardo.gestaotcc.dto.convite.EnviarConviteDto;
import com.leonardo.gestaotcc.dto.convite.ResponderConviteDto;
import com.leonardo.gestaotcc.entity.ConviteOrientacao;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.enums.StatusConvite;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.ConviteOrientacaoMapper;
import com.leonardo.gestaotcc.repository.ConviteOrientacaoRepository;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConviteOrientacaoService {

    private final ConviteOrientacaoRepository conviteOrientacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TccRepository tccRepository;
    private final ConviteOrientacaoMapper conviteOrientacaoMapper;
    private final NotificacaoService notificacaoService;
    private final GoogleDocsService googleDocsService;

    @Transactional
    public ConviteResponseDto enviarConvite(EnviarConviteDto dto, UUID alunoId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));

        Usuario orientador = usuarioRepository.findById(dto.getOrientadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado com ID: " + dto.getOrientadorId()));

        if (orientador.getPapel() != PapelUsuario.ORIENTADOR) {
            throw new BusinessException("Usuário não é um orientador.");
        }

        Tcc tcc = tccRepository.findById(dto.getTccId())
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + dto.getTccId()));

        if (!tcc.getAluno().getId().equals(alunoId)) {
            throw new BusinessException("O TCC não pertence ao aluno que está enviando o convite.");
        }

        if (conviteOrientacaoRepository.findByTccId(tcc.getId()).isPresent()) {
            throw new BusinessException("Já existe um convite de orientação para este TCC.");
        }

        ConviteOrientacao convite = ConviteOrientacao.builder()
                .aluno(aluno)
                .orientador(orientador)
                .tcc(tcc)
                .mensagem(dto.getMensagem())
                .status(StatusConvite.PENDENTE)
                .dataEnvio(LocalDateTime.now())
                .build();

        convite = conviteOrientacaoRepository.save(convite);

        // Garantir que o TCC permaneça como pendente enquanto aguarda resposta
        tcc.setStatus(com.leonardo.gestaotcc.enums.StatusTcc.PENDENTE_APROVACAO);
        tccRepository.save(tcc);

        // Enviar notificação para o orientador
        String mensagemNotificacao = String.format(
            "Você recebeu uma nova solicitação de orientação do aluno %s para o TCC: %s. " +
            "Acesse suas notificações para responder.",
            aluno.getNome(),
            tcc.getTitulo()
        );
        
        notificacaoService.push(orientador.getId(), TipoNotificacao.CONVITE_ORIENTACAO, mensagemNotificacao);

        return conviteOrientacaoMapper.toResponse(convite);
    }

    @Transactional
    public ConviteResponseDto responderConvite(UUID conviteId, ResponderConviteDto dto, UUID orientadorId) {
        ConviteOrientacao convite = conviteOrientacaoRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite de orientação não encontrado com ID: " + conviteId));

        if (!convite.getOrientador().getId().equals(orientadorId)) {
            throw new BusinessException("Você não tem permissão para responder a este convite.");
        }

        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new BusinessException("Este convite já foi respondido.");
        }

        convite.setStatus(dto.getStatus());
        convite.setDataResposta(LocalDateTime.now());

        if (dto.getStatus() == StatusConvite.ACEITO) {
            Tcc tcc = convite.getTcc();
            tcc.setOrientador(convite.getOrientador());
            tcc.setStatus(com.leonardo.gestaotcc.enums.StatusTcc.EM_ANDAMENTO); // Mudar para EM_ANDAMENTO quando aceito
            criarDocumentoGoogleSeNecessario(tcc);
            tccRepository.save(tcc);
            
            // Enviar notificação para o aluno sobre aceitação
            String mensagemAceito = String.format(
                "Sua solicitação de orientação para o TCC '%s' foi aceita pelo orientador %s! " +
                "Vocês podem começar a trabalhar juntos.",
                tcc.getTitulo(),
                convite.getOrientador().getNome()
            );
            // Notificar aluno
            notificacaoService.push(convite.getAluno().getId(), TipoNotificacao.CONVITE_ORIENTACAO, mensagemAceito);
        } else {
            // Quando rejeitado, remover convite e TCC do aluno
            Tcc tcc = convite.getTcc();

            // Criar resposta antes de deletar entidades
            ConviteResponseDto response = conviteOrientacaoMapper.toResponse(convite);

            String base = String.format(
                "Sua solicitação de orientação para o TCC '%s' foi rejeitada pelo orientador %s. O TCC foi removido e você pode criar um novo.",
                tcc.getTitulo(),
                convite.getOrientador().getNome()
            );
            String motivo = dto.getMotivo();
            String mensagemRejeitado = (motivo != null && !motivo.isBlank()) ? base + " Motivo: " + motivo : base;
            notificacaoService.push(convite.getAluno().getId(), TipoNotificacao.CONVITE_ORIENTACAO, mensagemRejeitado);

            // Excluir primeiro o convite para evitar referências, depois o TCC
            conviteOrientacaoRepository.delete(convite);
            tccRepository.delete(tcc);

            return response;
        }

        convite = conviteOrientacaoRepository.save(convite);
        return conviteOrientacaoMapper.toResponse(convite);
    }

    private void criarDocumentoGoogleSeNecessario(Tcc tcc) {
        if (tcc.getAluno() == null || tcc.getOrientador() == null) {
            return;
        }
        if (tcc.getGoogleFileId() != null) {
            return;
        }

        googleDocsService.criarDocumentoParaTcc(tcc)
                .ifPresent(metadata -> {
                    tcc.setGoogleFileId(metadata.fileId());
                    tcc.setGoogleWebViewLink(metadata.webViewLink());
                    tcc.setGoogleWebEditLink(metadata.webEditLink());
                    tcc.setGoogleDocCriadoEm(LocalDateTime.now());
                });
    }

    @Transactional(readOnly = true)
    public Page<ConviteResponseDto> listarConvitesPendentesPorOrientador(UUID orientadorId, Pageable pageable) {
        // Usar método com JOIN FETCH para carregar entidades relacionadas
        List<ConviteOrientacao> convites = conviteOrientacaoRepository.findByOrientadorIdAndStatusWithRelations(orientadorId, StatusConvite.PENDENTE);
        
        // Converter para Page manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), convites.size());
        List<ConviteOrientacao> pageContent = convites.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent.stream().map(conviteOrientacaoMapper::toResponse).toList(),
            pageable,
            convites.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<ConviteResponseDto> listarConvitesPorAluno(UUID alunoId, Pageable pageable) {
        // Usar método com JOIN FETCH para carregar entidades relacionadas
        List<ConviteOrientacao> convites = conviteOrientacaoRepository.findByAlunoIdWithRelations(alunoId);
        
        // Converter para Page manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), convites.size());
        List<ConviteOrientacao> pageContent = convites.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent.stream().map(conviteOrientacaoMapper::toResponse).toList(),
            pageable,
            convites.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<ConviteResponseDto> listarConvitesPorOrientador(UUID orientadorId, Pageable pageable) {
        // Usar método com JOIN FETCH para carregar entidades relacionadas
        List<ConviteOrientacao> convites = conviteOrientacaoRepository.findByOrientadorIdWithRelations(orientadorId);
        
        // Converter para Page manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), convites.size());
        List<ConviteOrientacao> pageContent = convites.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent.stream().map(conviteOrientacaoMapper::toResponse).toList(),
            pageable,
            convites.size()
        );
    }

    @Transactional(readOnly = true)
    public long contarConvitesPendentes(UUID orientadorId) {
        return conviteOrientacaoRepository.countByOrientadorIdAndStatus(orientadorId, StatusConvite.PENDENTE);
    }
}