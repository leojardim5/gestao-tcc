package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Comentario;
import com.leonardo.gestaotcc.entity.Submissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    List<Comentario> findBySubmissaoOrderByCriadoEmAsc(Submissao submissao);
}