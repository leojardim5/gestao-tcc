package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.entity.Notificacao;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.NotificacaoMapper;
import com.leonardo.gestaotcc.repository.NotificacaoRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;

    @Override
    @Transactional
    public void push(UUID usuarioId, TipoNotificacao tipo, String mensagem) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        Notificacao notificacao = Notificacao.builder()
                .usuario(usuario)
                .tipo(tipo)
                .mensagem(mensagem)
                .lida(false)
                .build();
        notificacaoRepository.save(notificacao);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com ID: " + notificacaoId));
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacaoDto.NotificacaoResponse> list(UUID usuarioId, Boolean lidas, Pageable pageable) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }

        Page<Notificacao> notificacoesPage;
        if (lidas != null) {
            notificacoesPage = notificacaoRepository.findByUsuarioIdAndLida(usuarioId, lidas, pageable);
        } else {
            notificacoesPage = notificacaoRepository.findByUsuarioId(usuarioId, pageable);
        }
        return notificacoesPage.map(notificacaoMapper::toResponseDto);
    }
}
