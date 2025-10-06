package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Reuniao;
import com.leonardo.gestaotcc.entity.Tcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, UUID> {
    List<Reuniao> findByTccAndDataHoraBetween(Tcc tcc, Instant start, Instant end);
    List<Reuniao> findByTccId(UUID tccId);
}