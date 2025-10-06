package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ConflictException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.TccMapper;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TccServiceImpl implements TccService {

    private final TccRepository tccRepository;
    private final UsuarioRepository usuarioRepository;
    private final TccMapper tccMapper;

    @Override
    @Transactional
    public TccDto.TccResponse create(TccDto.TccCreateRequest request) {
        Usuario aluno = usuarioRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + request.getAlunoId()));

        if (aluno.getPapel() != PapelUsuario.ALUNO) {
            throw new BusinessException("O usuário com ID " + request.getAlunoId() + " não é um aluno.");
        }

        if (tccRepository.existsByAlunoId(request.getAlunoId())) {
            throw new ConflictException("Aluno já possui um TCC ativo.");
        }

        Usuario orientador = usuarioRepository.findById(request.getOrientadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado com ID: " + request.getOrientadorId()));

        if (orientador.getPapel() != PapelUsuario.ORIENTADOR && orientador.getPapel() != PapelUsuario.COORDENADOR) {
            throw new BusinessException("O usuário com ID " + request.getOrientadorId() + " não é um orientador ou coordenador.");
        }

        Tcc tcc = tccMapper.toEntity(request);
        tcc.setAluno(aluno);
        tcc.setOrientador(orientador);
        tcc.setStatus(StatusTcc.RASCUNHO);

        tcc = tccRepository.save(tcc);
        return tccMapper.toResponseDto(tcc);
    }

    @Override
    @Transactional
    public TccDto.TccResponse update(UUID id, TccDto.TccUpdateRequest request) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));

        if (request.getOrientadorId() != null) {
            Usuario newOrientador = usuarioRepository.findById(request.getOrientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo orientador não encontrado com ID: " + request.getOrientadorId()));
            if (newOrientador.getPapel() != PapelUsuario.ORIENTADOR && newOrientador.getPapel() != PapelUsuario.COORDENADOR) {
                throw new BusinessException("O usuário com ID " + request.getOrientadorId() + " não é um orientador ou coordenador.");
            }
            tcc.setOrientador(newOrientador);
        }

        if (request.getCoorientadorId() != null) {
            Usuario newCoorientador = usuarioRepository.findById(request.getCoorientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo coorientador não encontrado com ID: " + request.getCoorientadorId()));
            if (newCoorientador.getPapel() != PapelUsuario.ORIENTADOR && newCoorientador.getPapel() != PapelUsuario.COORDENADOR) {
                throw new BusinessException("O usuário com ID " + request.getCoorientadorId() + " não é um orientador ou coordenador.");
            }
            tcc.setCoorientador(newCoorientador);
        }

        tccMapper.updateEntityFromDto(request, tcc);
        tcc = tccRepository.save(tcc);
        return tccMapper.toResponseDto(tcc);
    }

    @Override
    @Transactional
    public TccDto.TccResponse assignOrientador(UUID tccId, UUID orientadorId) {
        Tcc tcc = tccRepository.findById(tccId)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + tccId));

        Usuario orientador = usuarioRepository.findById(orientadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado com ID: " + orientadorId));

        if (orientador.getPapel() != PapelUsuario.ORIENTADOR && orientador.getPapel() != PapelUsuario.COORDENADOR) {
            throw new BusinessException("O usuário com ID " + orientadorId + " não é um orientador ou coordenador.");
        }

        tcc.setOrientador(orientador);
        tcc = tccRepository.save(tcc);
        return tccMapper.toResponseDto(tcc);
    }

    @Override
    @Transactional
    public TccDto.TccResponse changeStatus(UUID id, StatusTcc newStatus) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));
        tcc.setStatus(newStatus);
        tcc = tccRepository.save(tcc);
        return tccMapper.toResponseDto(tcc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TccDto.TccResponse> listByAluno(UUID alunoId, Pageable pageable) {
        return tccRepository.findByAlunoId(alunoId)
                .map(tcc -> tccRepository.findAll(Pageable.unpaged()).map(tccMapper::toResponseDto))
                .orElse(Page.empty());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TccDto.TccResponse> listByOrientador(UUID orientadorId, Pageable pageable) {
        return tccRepository.findByOrientadorId(orientadorId, pageable).map(tccMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TccDto.TccResponse get(UUID id) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));
        return tccMapper.toResponseDto(tcc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TccDto.TccResponse> listAll(Pageable pageable) {
        return tccRepository.findAll(pageable).map(tccMapper::toResponseDto);
    }
}
