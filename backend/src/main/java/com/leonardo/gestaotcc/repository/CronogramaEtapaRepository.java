package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.CronogramaEtapa;
import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.enums.StatusCronogramaEtapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CronogramaEtapaRepository extends JpaRepository<CronogramaEtapa, UUID> {

    List<CronogramaEtapa> findByTccOrderByDataInicioAsc(Tcc tcc);

    interface CronogramaStatusCount {
        UUID getTccId();
        StatusCronogramaEtapa getStatus();
        long getQuantidade();
    }

    @Query("SELECT e.tcc.id AS tccId, e.status AS status, COUNT(e) AS quantidade " +
            "FROM CronogramaEtapa e WHERE e.tcc.id IN :tccIds GROUP BY e.tcc.id, e.status")
    List<CronogramaStatusCount> countByTccIds(@Param("tccIds") List<UUID> tccIds);
}

