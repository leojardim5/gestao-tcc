package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    Page<Notificacao> findByUsuarioIdAndLida(UUID usuarioId, boolean lida, Pageable pageable);

    Page<Notificacao> findByUsuarioId(UUID usuarioId, Pageable pageable);
}
