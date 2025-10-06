package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.enums.StatusTcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TccService {

    TccDto.TccResponse create(TccDto.TccCreateRequest tccCreateRequest);

    TccDto.TccResponse update(UUID id, TccDto.TccUpdateRequest tccUpdateRequest);

    TccDto.TccResponse assignOrientador(UUID tccId, UUID orientadorId);

    TccDto.TccResponse changeStatus(UUID id, StatusTcc status);

    Page<TccDto.TccResponse> listByAluno(UUID alunoId, Pageable pageable);

    Page<TccDto.TccResponse> listByOrientador(UUID orientadorId, Pageable pageable);

    Page<TccDto.TccResponse> listAll(Pageable pageable);

    TccDto.TccResponse getById(UUID id);
}
