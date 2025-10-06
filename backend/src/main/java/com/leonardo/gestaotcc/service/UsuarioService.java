package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsuarioService {
    UsuarioDto.UsuarioResponse create(UsuarioDto.UsuarioCreateRequest request);
    UsuarioDto.UsuarioResponse update(UUID id, UsuarioDto.UsuarioUpdateRequest request);
    void deactivate(UUID id);
    UsuarioDto.UsuarioResponse get(UUID id);
    Page<UsuarioDto.UsuarioResponse> list(Pageable pageable);
}