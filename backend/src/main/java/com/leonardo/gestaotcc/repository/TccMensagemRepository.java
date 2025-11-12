package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.TccMensagem;
import com.leonardo.gestaotcc.entity.Tcc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TccMensagemRepository extends JpaRepository<TccMensagem, UUID> {
    List<TccMensagem> findByTccOrderByCriadoEmAsc(Tcc tcc);
}

