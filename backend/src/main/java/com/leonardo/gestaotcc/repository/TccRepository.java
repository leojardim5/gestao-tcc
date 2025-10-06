package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.StatusTcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TccRepository extends JpaRepository<Tcc, UUID> {

    Page<Tcc> findByAlunoId(UUID alunoId, Pageable pageable);

    Page<Tcc> findByOrientadorId(UUID orientadorId, Pageable pageable);

    @Query("SELECT t FROM Tcc t WHERE t.status IN :statuses")
    Page<Tcc> searchByStatus(List<StatusTcc> statuses, Pageable pageable);

    boolean existsByAlunoIdAndStatusIn(UUID alunoId, List<StatusTcc> statuses);

    boolean existsByAlunoIdAndStatusNotIn(UUID alunoId, StatusTcc[] statuses);
}
