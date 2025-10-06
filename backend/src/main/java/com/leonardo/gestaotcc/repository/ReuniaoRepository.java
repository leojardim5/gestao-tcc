package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Reuniao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, UUID> {

    Page<Reuniao> findByTccId(UUID tccId, Pageable pageable);

    List<Reuniao> findByTccIdAndDataHoraBetween(UUID tccId, Instant start, Instant end);

    boolean existsByTccAndDataHoraBetween(com.leonardo.gestaotcc.entity.Tcc tcc, Instant start, Instant end);
}
