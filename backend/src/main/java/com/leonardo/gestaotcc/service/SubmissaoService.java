package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.SubmissaoDto;
import com.leonardo.gestaotcc.enums.StatusSubmissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SubmissaoService {
    SubmissaoDto.SubmissaoResponse create(SubmissaoDto.SubmissaoCreateRequest request);
    SubmissaoDto.SubmissaoResponse decide(UUID submissaoId, StatusSubmissao status);
    Page<SubmissaoDto.SubmissaoResponse> listByTcc(UUID tccId, Pageable pageable);
    SubmissaoDto.SubmissaoResponse get(UUID id);
    SubmissaoDto.SubmissaoResponse getLatestByTcc(UUID tccId);
}