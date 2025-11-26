package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.EmailNotificationData;
import com.leonardo.gestaotcc.dto.ReuniaoDto;
import com.leonardo.gestaotcc.entity.Reuniao;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.ReuniaoMapper;
import com.leonardo.gestaotcc.repository.ReuniaoRepository;
import com.leonardo.gestaotcc.repository.TccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReuniaoServiceImpl implements ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final TccRepository tccRepository;
    private final ReuniaoMapper reuniaoMapper;
    private final NotificacaoService notificacaoService;

    @Override
    @Transactional
    public ReuniaoDto.ReuniaoResponse schedule(ReuniaoDto.ReuniaoCreateRequest request) {
        Tcc tcc = tccRepository.findById(request.getTccId())
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + request.getTccId()));

        // Checar conflito simples por tcc em janela de 1h
        Instant startConflictWindow = request.getDataHora().minus(1, ChronoUnit.HOURS);
        Instant endConflictWindow = request.getDataHora().plus(1, ChronoUnit.HOURS);

        List<Reuniao> conflitos = reuniaoRepository.findByTccAndDataHoraBetween(tcc, startConflictWindow, endConflictWindow);
        if (!conflitos.isEmpty()) {
            throw new BusinessException("Já existe uma reunião agendada para este TCC dentro do período de 1 hora.");
        }

        Reuniao reuniao = reuniaoMapper.toEntity(request);
        reuniao.setTcc(tcc);
        reuniao = reuniaoRepository.save(reuniao);
        
        // Notificar aluno e orientador sobre a reunião agendada
        LocalDateTime dataHora = LocalDateTime.ofInstant(request.getDataHora(), ZoneId.systemDefault());
        String dataHoraFormatada = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        String mensagem = String.format(
            "Reunião agendada para o TCC '%s' em %s. Tema: %s",
            tcc.getTitulo(),
            dataHoraFormatada,
            request.getTema()
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
                .mensagemPersonalizada(request.getTema())
                .dataHora(dataHora)
                .status(tcc.getStatus().toString())
                .build();
        
        if (tcc.getAluno() != null) {
            notificacaoService.push(
                tcc.getAluno().getId(),
                TipoNotificacao.REUNIAO,
                mensagem,
                dadosEmail
            );
        }
        
        if (tcc.getOrientador() != null) {
            notificacaoService.push(
                tcc.getOrientador().getId(),
                TipoNotificacao.REUNIAO,
                mensagem,
                dadosEmail
            );
        }
        
        return reuniaoMapper.toResponse(reuniao);
    }

    @Override
    @Transactional
    public void cancel(UUID id) {
        if (!reuniaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reunião não encontrada com ID: " + id);
        }
        reuniaoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReuniaoDto.ReuniaoResponse> listByTcc(UUID tccId, Pageable pageable) {
        Tcc tcc = tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + tccId));

        List<Reuniao> reunioes = reuniaoRepository.findByTccId(tccId);
        List<ReuniaoDto.ReuniaoResponse> dtoList = reunioes.stream()
                .map(reuniaoMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        return new PageImpl<>(dtoList.subList(start, end), pageable, dtoList.size());

    }

    @Override
    @Transactional(readOnly = true)
    public ReuniaoDto.ReuniaoResponse get(UUID id) {
        Reuniao reuniao = reuniaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião não encontrada com ID: " + id));
        return reuniaoMapper.toResponse(reuniao);
    }
}