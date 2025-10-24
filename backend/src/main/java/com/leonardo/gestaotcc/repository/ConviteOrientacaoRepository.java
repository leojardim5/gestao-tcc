package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.ConviteOrientacao;
import com.leonardo.gestaotcc.enums.StatusConvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConviteOrientacaoRepository extends JpaRepository<ConviteOrientacao, UUID> {
    Page<ConviteOrientacao> findByOrientadorIdAndStatus(UUID orientadorId, StatusConvite status, Pageable pageable);
    Page<ConviteOrientacao> findByAlunoId(UUID alunoId, Pageable pageable);
    Page<ConviteOrientacao> findByOrientadorId(UUID orientadorId, Pageable pageable);
    Optional<ConviteOrientacao> findByTccId(UUID tccId);
}
