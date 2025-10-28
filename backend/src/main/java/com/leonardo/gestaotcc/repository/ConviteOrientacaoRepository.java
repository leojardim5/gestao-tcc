package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.ConviteOrientacao;
import com.leonardo.gestaotcc.enums.StatusConvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConviteOrientacaoRepository extends JpaRepository<ConviteOrientacao, UUID> {
    Page<ConviteOrientacao> findByOrientadorIdAndStatus(UUID orientadorId, StatusConvite status, Pageable pageable);
    Page<ConviteOrientacao> findByAlunoId(UUID alunoId, Pageable pageable);
    Page<ConviteOrientacao> findByOrientadorId(UUID orientadorId, Pageable pageable);
    Optional<ConviteOrientacao> findByTccId(UUID tccId);

    // Métodos com JOIN FETCH para carregar entidades relacionadas
    @Query("SELECT c FROM ConviteOrientacao c LEFT JOIN FETCH c.tcc LEFT JOIN FETCH c.aluno LEFT JOIN FETCH c.orientador")
    List<ConviteOrientacao> findAllWithRelations();

    @Query("SELECT c FROM ConviteOrientacao c LEFT JOIN FETCH c.tcc LEFT JOIN FETCH c.aluno LEFT JOIN FETCH c.orientador WHERE c.orientador.id = :orientadorId")
    List<ConviteOrientacao> findByOrientadorIdWithRelations(@Param("orientadorId") UUID orientadorId);

    @Query("SELECT c FROM ConviteOrientacao c LEFT JOIN FETCH c.tcc LEFT JOIN FETCH c.aluno LEFT JOIN FETCH c.orientador WHERE c.aluno.id = :alunoId")
    List<ConviteOrientacao> findByAlunoIdWithRelations(@Param("alunoId") UUID alunoId);

    @Query("SELECT c FROM ConviteOrientacao c LEFT JOIN FETCH c.tcc LEFT JOIN FETCH c.aluno LEFT JOIN FETCH c.orientador WHERE c.orientador.id = :orientadorId AND c.status = :status")
    List<ConviteOrientacao> findByOrientadorIdAndStatusWithRelations(@Param("orientadorId") UUID orientadorId, @Param("status") StatusConvite status);

    // Método para contar convites pendentes
    long countByOrientadorIdAndStatus(UUID orientadorId, StatusConvite status);
}
