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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TccRepository extends JpaRepository<Tcc, UUID> {
    Page<Tcc> findByAlunoId(UUID alunoId, Pageable pageable);
    Page<Tcc> findByOrientadorId(UUID orientadorId, Pageable pageable);
    Page<Tcc> findByStatus(StatusTcc status, Pageable pageable);
    Page<Tcc> findByStatusIn(List<StatusTcc> statuses, Pageable pageable);
    boolean existsByAlunoId(UUID alunoId);
    
    // Métodos para contagem por status
    long countByStatus(StatusTcc status);
    
    // Método para contar TCCs próximos do prazo
    @Query("SELECT COUNT(t) FROM Tcc t WHERE t.dataEntregaPrevista BETWEEN :dataInicio AND :dataFim AND t.status = :status")
    long countByDataEntregaPrevistaBetweenAndStatus(@Param("dataInicio") LocalDate dataInicio, 
                                                   @Param("dataFim") LocalDate dataFim, 
                                                   @Param("status") StatusTcc status);
}