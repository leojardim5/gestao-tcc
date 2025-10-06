package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Submissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissaoRepository extends JpaRepository<Submissao, UUID> {

    Optional<Submissao> findTopByTccIdOrderByVersaoDesc(UUID tccId);

    Page<Submissao> findByTccId(UUID tccId, Pageable pageable);
}
