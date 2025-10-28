package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.StatusTcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TccRepository extends JpaRepository<Tcc, UUID> {
    Page<Tcc> findByAlunoId(UUID alunoId, Pageable pageable);
    Page<Tcc> findByOrientadorId(UUID orientadorId, Pageable pageable);
    Page<Tcc> findByStatus(StatusTcc status, Pageable pageable);
    Page<Tcc> findByStatusIn(List<StatusTcc> statuses, Pageable pageable);
    boolean existsByAlunoId(UUID alunoId);
    
    // Métodos com JOIN FETCH para carregar relacionamentos
    @Query("SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador")
    List<Tcc> findAllWithUsers();
    
    @Query("SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador WHERE t.aluno.id = :alunoId")
    List<Tcc> findByAlunoIdWithUsers(@Param("alunoId") UUID alunoId);
    
    @Query("SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador WHERE t.orientador.id = :orientadorId")
    List<Tcc> findByOrientadorIdWithUsers(@Param("orientadorId") UUID orientadorId);
    
    // Métodos paginados com JOIN FETCH (usando count query separada)
    @Query(value = "SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador", 
           countQuery = "SELECT COUNT(t) FROM Tcc t")
    Page<Tcc> findAllWithUsers(Pageable pageable);
    
    @Query(value = "SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador WHERE t.aluno.id = :alunoId", 
           countQuery = "SELECT COUNT(t) FROM Tcc t WHERE t.aluno.id = :alunoId")
    Page<Tcc> findByAlunoIdWithUsers(@Param("alunoId") UUID alunoId, Pageable pageable);
    
    @Query(value = "SELECT t FROM Tcc t LEFT JOIN FETCH t.aluno LEFT JOIN FETCH t.orientador LEFT JOIN FETCH t.coorientador WHERE t.orientador.id = :orientadorId", 
           countQuery = "SELECT COUNT(t) FROM Tcc t WHERE t.orientador.id = :orientadorId")
    Page<Tcc> findByOrientadorIdWithUsers(@Param("orientadorId") UUID orientadorId, Pageable pageable);
    
    // Método para buscar TCCs com convites pendentes para um orientador
    @Query("SELECT c.tcc FROM ConviteOrientacao c WHERE c.orientador.id = :orientadorId AND c.status = 'PENDENTE'")
    List<Tcc> findByConvitesPendentesParaOrientador(@Param("orientadorId") UUID orientadorId);
    
    // Métodos para contagem por status
    long countByStatus(StatusTcc status);
    
    // Método para contar TCCs próximos do prazo
    @Query("SELECT COUNT(t) FROM Tcc t WHERE t.dataEntregaPrevista BETWEEN :dataInicio AND :dataFim AND t.status = :status")
    long countByDataEntregaPrevistaBetweenAndStatus(@Param("dataInicio") LocalDate dataInicio, 
                                                   @Param("dataFim") LocalDate dataFim, 
                                                   @Param("status") StatusTcc status);
}