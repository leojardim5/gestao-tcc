package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.exception.BusinessException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.mapper.TccMapper;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TccServiceImpl implements TccService {

    private final TccRepository tccRepository;
    private final UsuarioRepository usuarioRepository;
    private final TccMapper tccMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public TccDto.TccResponse create(TccDto.TccCreateRequest request) {
        Usuario aluno = usuarioRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + request.getAlunoId()));

        if (aluno.getPapel() != PapelUsuario.ALUNO) {
            throw new BusinessException("O usuário com ID " + request.getAlunoId() + " não é um aluno.");
        }

        // TEMPORÁRIO: Permitir múltiplos TCCs por aluno para debug
        // if (tccRepository.existsByAlunoId(request.getAlunoId())) {
        //     throw new ConflictException("Aluno já possui um TCC ativo.");
        // }

        Usuario orientadorSolicitado = null;
        if (request.getOrientadorId() != null) {
            Usuario orientadorSelecionado = usuarioRepository.findById(request.getOrientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado com ID: " + request.getOrientadorId()));

            if (orientadorSelecionado.getPapel() != PapelUsuario.ORIENTADOR && orientadorSelecionado.getPapel() != PapelUsuario.COORDENADOR) {
                throw new BusinessException("O usuário com ID " + request.getOrientadorId() + " não é um orientador ou coordenador.");
            }

            orientadorSolicitado = orientadorSelecionado;
        }

        // O TCC fica aguardando aprovação do orientador indicado
        StatusTcc statusInicial = StatusTcc.PENDENTE_APROVACAO;

        Tcc tcc = Tcc.builder()
                .titulo(request.getTitulo())
                .tema(request.getTema())
                .resumo(request.getTema())
                .curso(request.getCurso())
                .dataInicio(request.getDataInicio())
                .dataEntregaPrevista(request.getDataEntregaPrevista())
                .aluno(aluno)
                .orientador(orientadorSolicitado)
                .status(statusInicial)
                .build();

        tcc = tccRepository.save(tcc);
        return tccMapper.toResponse(tcc);
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
        return tccMapper.toResponse(tcc);
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
        return tccMapper.toResponse(tcc);
    }

    @Override
    @Transactional
    public TccDto.TccResponse changeStatus(UUID id, StatusTcc newStatus) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));
        tcc.setStatus(newStatus);
        tcc = tccRepository.save(tcc);
        return tccMapper.toResponse(tcc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TccDto.TccResponse> listByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (usuario.getPapel() == PapelUsuario.ALUNO) {
            return tccRepository.findByAlunoIdWithUsers(usuarioId, pageable).map(tccMapper::toResponse);
        } else if (usuario.getPapel() == PapelUsuario.ORIENTADOR) {
            return tccRepository.findByOrientadorIdWithUsers(usuarioId, pageable).map(tccMapper::toResponse);
        } else {
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TccDto.TccResponse get(UUID id, UUID authenticatedUserId, PapelUsuario authenticatedUserRole) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));
        
        // Verificar se o usuário tem permissão para ver este TCC
        if (authenticatedUserId != null && authenticatedUserRole != null) {
            boolean hasPermission = false;
            
            if (authenticatedUserRole == PapelUsuario.ALUNO) {
                // Aluno pode ver apenas seus próprios TCCs
                hasPermission = tcc.getAluno().getId().equals(authenticatedUserId);
            } else if (authenticatedUserRole == PapelUsuario.ORIENTADOR) {
                // Orientador pode ver TCCs onde ele é orientador
                hasPermission = tcc.getOrientador() != null && tcc.getOrientador().getId().equals(authenticatedUserId);
            } else if (authenticatedUserRole == PapelUsuario.COORDENADOR) {
                // Coordenador pode ver todos os TCCs
                hasPermission = true;
            }
            
            if (!hasPermission) {
                throw new ResourceNotFoundException("TCC não encontrado com ID: " + id);
            }
        }
        
        return tccMapper.toResponse(tcc);
    }

    @Transactional(readOnly = true)
    public Page<TccDto.TccResponse> listAll(Pageable pageable, UUID authenticatedUserId, PapelUsuario authenticatedUserRole) {
        // Se não há usuário autenticado, retornar todos os TCCs (para debug/desenvolvimento)
        if (authenticatedUserId == null || authenticatedUserRole == null) {
            return tccRepository.findAllWithUsers(pageable).map(tccMapper::toResponse);
        }
        
        if (authenticatedUserRole == PapelUsuario.ALUNO) {
            // Aluno vê seus próprios TCCs (independente do status)
            return tccRepository.findByAlunoIdWithUsers(authenticatedUserId, pageable).map(tccMapper::toResponse);
        } else if (authenticatedUserRole == PapelUsuario.ORIENTADOR) {
            // Orientador vê TCCs onde ele é orientador OU onde tem convite pendente
            // Primeiro buscar TCCs onde ele é orientador
            Page<Tcc> tccsComoOrientador = tccRepository.findByOrientadorIdWithUsers(authenticatedUserId, pageable);
            
            // Depois buscar TCCs com convites pendentes para ele
            List<Tcc> tccsComConvitePendente = tccRepository.findByConvitesPendentesParaOrientador(authenticatedUserId);
            
            // Combinar os resultados
            List<Tcc> todosTccs = new ArrayList<>();
            todosTccs.addAll(tccsComoOrientador.getContent());
            
            // Adicionar TCCs com convites pendentes que não estão na primeira lista
            for (Tcc tcc : tccsComConvitePendente) {
                boolean jaExiste = todosTccs.stream().anyMatch(t -> t.getId().equals(tcc.getId()));
                if (!jaExiste) {
                    todosTccs.add(tcc);
                }
            }
            
            // Aplicar paginação manualmente
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), todosTccs.size());
            List<Tcc> pageContent = todosTccs.subList(start, end);
            
            // Carregar entidades relacionadas para cada TCC
            List<Tcc> tccsWithUsers = pageContent.stream()
                    .map(tcc -> tccRepository.findById(tcc.getId()).orElse(tcc))
                    .toList();
            
            return new org.springframework.data.domain.PageImpl<>(
                    tccsWithUsers.stream().map(tccMapper::toResponse).toList(),
                    pageable,
                    todosTccs.size()
            );
        } else if (authenticatedUserRole == PapelUsuario.COORDENADOR) {
            // COORDENADOR pode ver todos os TCCs
            return tccRepository.findAllWithUsers(pageable).map(tccMapper::toResponse);
        } else {
            // Qualquer outro papel retorna página vazia por segurança
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Tcc tcc = tccRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TCC não encontrado com ID: " + id));
        
        // Verificar se o TCC pode ser deletado (apenas se estiver em RASCUNHO ou PENDENTE_APROVACAO)
        if (tcc.getStatus() != StatusTcc.RASCUNHO && tcc.getStatus() != StatusTcc.PENDENTE_APROVACAO) {
            throw new BusinessException("Apenas TCCs em status RASCUNHO ou PENDENTE_APROVACAO podem ser deletados.");
        }
        
        // Deletar registros relacionados primeiro (para evitar constraint violation)
        String tccId = id.toString();
        
        // Deletar convites relacionados
        jdbcTemplate.execute("DELETE FROM convites_orientacao WHERE tcc_id = '" + tccId + "';");
        
        // Deletar submissoes relacionadas
        jdbcTemplate.execute("DELETE FROM submissoes WHERE tcc_id = '" + tccId + "';");
        
        // Deletar reunioes relacionadas
        jdbcTemplate.execute("DELETE FROM reunioes WHERE tcc_id = '" + tccId + "';");
        
        // Deletar comentarios relacionados (se existirem)
        jdbcTemplate.execute("DELETE FROM comentarios WHERE submissao_id IN (SELECT id FROM submissoes WHERE tcc_id = '" + tccId + "');");
        
        // Finalmente deletar o TCC
        tccRepository.delete(tcc);
    }
}