package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.ComentarioDto;
import com.leonardo.gestaotcc.entity.Comentario;
import com.leonardo.gestaotcc.entity.Submissao;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.ComentarioMapper;
import com.leonardo.gestaotcc.repository.ComentarioRepository;
import com.leonardo.gestaotcc.repository.SubmissaoRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final SubmissaoRepository submissaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioMapper comentarioMapper;

    @Override
    @Transactional
    public ComentarioDto.ComentarioResponse add(ComentarioDto.ComentarioCreateRequest request) {
        Submissao submissao = submissaoRepository.findById(request.getSubmissaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada com ID: " + request.getSubmissaoId()));

        Usuario autor = usuarioRepository.findById(request.getAutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com ID: " + request.getAutorId()));

        Comentario comentario = comentarioMapper.toEntity(request);
        comentario.setSubmissao(submissao);
        comentario.setAutor(autor);
        comentario = comentarioRepository.save(comentario);
        return comentarioMapper.toResponse(comentario);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!comentarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comentário não encontrado com ID: " + id);
        }
        comentarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComentarioDto.ComentarioResponse> listBySubmissao(UUID submissaoId, Pageable pageable) {
        Submissao submissao = submissaoRepository.findById(submissaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada com ID: " + submissaoId));

        List<Comentario> comentarios = comentarioRepository.findBySubmissaoOrderByCriadoEmAsc(submissao);
        List<ComentarioDto.ComentarioResponse> dtoList = comentarios.stream()
                .map(comentarioMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        return new PageImpl<>(dtoList.subList(start, end), pageable, dtoList.size());

    }
}
