package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.dto.workspace.TccWorkspaceDto;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.List;

public interface TccService {
    TccDto.TccResponse create(TccDto.TccCreateRequest request);
    TccDto.TccResponse update(UUID id, TccDto.TccUpdateRequest request);
    TccDto.TccResponse assignOrientador(UUID tccId, UUID orientadorId);
    TccDto.TccResponse changeStatus(UUID id, StatusTcc newStatus);
    Page<TccDto.TccResponse> listByUsuario(UUID usuarioId, Pageable pageable);
    TccDto.TccResponse get(UUID id, UUID authenticatedUserId, PapelUsuario authenticatedUserRole);
    void delete(UUID id);
    Page<TccDto.TccResponse> listAll(Pageable pageable, UUID authenticatedUserId, PapelUsuario authenticatedUserRole);
    TccWorkspaceDto.Overview getWorkspaceOverview(UUID id);
    TccWorkspaceDto.Overview ensureGoogleDocument(UUID id);
    List<TccWorkspaceDto.DocComment> listGoogleDocComments(UUID id);
}