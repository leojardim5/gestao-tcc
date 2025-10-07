package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.StatusTcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}