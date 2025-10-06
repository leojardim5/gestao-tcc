package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.UsuarioDto;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.exception.ConflictException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.UsuarioMapper;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioDto.UsuarioResponse create(UsuarioDto.UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email já cadastrado.");
        }
        // TODO: Implement password encoding
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenhaHash(request.getSenha()); // Temporário, será substituído por senha encodada
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuario);
    }

    @Override
    @Transactional
    public UsuarioDto.UsuarioResponse update(UUID id, UsuarioDto.UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        usuarioMapper.updateEntityFromDto(request, usuario);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuario);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto.UsuarioResponse get(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.toResponseDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDto.UsuarioResponse> list(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toResponseDto);
    }
}
