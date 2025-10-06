package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.ComentarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ComentarioService {
    ComentarioDto.ComentarioResponse add(ComentarioDto.ComentarioCreateRequest request);
    void delete(UUID id);
    Page<ComentarioDto.ComentarioResponse> listBySubmissao(UUID submissaoId, Pageable pageable);
}