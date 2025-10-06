package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Submissao;
import com.leonardo.gestaotcc.entity.Tcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissaoRepository extends JpaRepository<Submissao, UUID> {
    Optional<Submissao> findTopByTccOrderByVersaoDesc();
    List<Submissao> findByTccOrderByVersaoAsc(Tcc tcc);
}