package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {

    Page<Comentario> findBySubmissaoId(UUID submissaoId, Pageable pageable);
}
