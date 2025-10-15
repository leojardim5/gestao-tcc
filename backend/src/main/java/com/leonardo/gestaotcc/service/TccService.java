package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface TccService {
    TccDto.TccResponse create(TccDto.TccCreateRequest request);
    TccDto.TccResponse update(UUID id, TccDto.TccUpdateRequest request);
    TccDto.TccResponse assignOrientador(UUID tccId, UUID orientadorId);
    TccDto.TccResponse changeStatus(UUID id, StatusTcc newStatus);
    Page<TccDto.TccResponse> listByUsuario(UUID usuarioId, Pageable pageable);
    TccDto.TccResponse get(UUID id);
    Page<TccDto.TccResponse> listAll(Pageable pageable, UUID authenticatedUserId, PapelUsuario authenticatedUserRole);
}