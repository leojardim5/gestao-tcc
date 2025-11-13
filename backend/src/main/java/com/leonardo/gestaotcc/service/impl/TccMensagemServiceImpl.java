package com.leonardo.gestaotcc.service.impl;

import com.leonardo.gestaotcc.dto.mensagem.TccMensagemDto;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.TccMensagem;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.TccMensagemMapper;
import com.leonardo.gestaotcc.repository.TccMensagemRepository;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.security.SecurityUtils;
import com.leonardo.gestaotcc.service.NotificacaoService;
import com.leonardo.gestaotcc.service.TccMensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TccMensagemServiceImpl implements TccMensagemService {

    private final TccRepository tccRepository;
    private final TccMensagemRepository mensagemRepository;
    private final TccMensagemMapper mensagemMapper;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    @Override
    @Transactional(readOnly = true)
    public List<TccMensagemDto.MensagemResponse> listarMensagens(UUID tccId) {
        Tcc tcc = buscarTcc(tccId);
        garantirPermissaoLeitura(tcc);
        return mensagemRepository.findByTccOrderByCriadoEmAsc(tcc).stream()
                .map(mensagemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TccMensagemDto.MensagemResponse criarMensagem(UUID tccId, TccMensagemDto.CriarMensagemRequest request) {
        Tcc tcc = buscarTcc(tccId);
        var userDetails = SecurityUtils.getCurrentUserDetails()
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado"));
        var usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!StringUtils.hasText(request.getConteudo())) {
            throw new BusinessException("Mensagem não pode ser vazia.");
        }

        PapelUsuario papel = usuario.getPapel();
        boolean autorizado = switch (papel) {
            case ALUNO -> tcc.getAluno() != null && tcc.getAluno().getId().equals(usuario.getId());
            case ORIENTADOR -> (tcc.getOrientador() != null && tcc.getOrientador().getId().equals(usuario.getId()))
                    || (tcc.getCoorientador() != null && tcc.getCoorientador().getId().equals(usuario.getId()));
            default -> false;
        };

        if (!autorizado) {
            throw new ResourceNotFoundException("TCC não encontrado");
        }

        TccMensagem mensagem = TccMensagem.builder()
                .tcc(tcc)
                .autor(usuario)
                .conteudo(request.getConteudo().trim())
                .build();

        mensagem = mensagemRepository.save(mensagem);

        notificarDestinatario(tcc, usuario, mensagem);

        return mensagemMapper.toResponse(mensagem);
    }

    private Tcc buscarTcc(UUID tccId) {
        return tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado"));
    }

    private void garantirPermissaoLeitura(Tcc tcc) {
        var usuarioOpt = SecurityUtils.getCurrentUserDetails();
        if (usuarioOpt.isEmpty()) {
            throw new ResourceNotFoundException("TCC não encontrado");
        }
        var usuario = usuarioOpt.get();
        boolean permitido = switch (usuario.getPapel()) {
            case ALUNO -> tcc.getAluno() != null && tcc.getAluno().getId().equals(usuario.getId());
            case ORIENTADOR -> (tcc.getOrientador() != null && tcc.getOrientador().getId().equals(usuario.getId()))
                    || (tcc.getCoorientador() != null && tcc.getCoorientador().getId().equals(usuario.getId()));
            case COORDENADOR -> true;
        };
        if (!permitido) {
            throw new ResourceNotFoundException("TCC não encontrado");
        }
    }

    private void notificarDestinatario(Tcc tcc, Usuario autor, TccMensagem mensagem) {
        if (autor.getPapel() == PapelUsuario.ALUNO && tcc.getOrientador() != null) {
            notificacaoService.push(
                    tcc.getOrientador().getId(),
                    TipoNotificacao.SISTEMA,
                    String.format("Nova mensagem do aluno %s: \"%s\"", autor.getNome(), preview(mensagem.getConteudo()))
            );
            return;
        }

        if (autor.getPapel() == PapelUsuario.ORIENTADOR && tcc.getAluno() != null) {
            notificacaoService.push(
                    tcc.getAluno().getId(),
                    TipoNotificacao.SISTEMA,
                    String.format("Nova mensagem do orientador %s: \"%s\"", autor.getNome(), preview(mensagem.getConteudo()))
            );
        }
    }

    private String preview(String conteudo) {
        if (conteudo == null) {
            return "";
        }
        String trimmed = conteudo.trim();
        return trimmed.length() > 120 ? trimmed.substring(0, 117) + "..." : trimmed;
    }
}

