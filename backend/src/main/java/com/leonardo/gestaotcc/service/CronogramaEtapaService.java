package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.cronograma.CronogramaEtapaDto;
import com.leonardo.gestaotcc.dto.cronograma.CronogramaResumoDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CronogramaEtapaService {

    List<CronogramaEtapaDto.EtapaResponse> listarPorTcc(UUID tccId);

    CronogramaEtapaDto.EtapaResponse criarOuAtualizarEtapa(UUID tccId, CronogramaEtapaDto.EtapaCreateRequest request);

    CronogramaEtapaDto.EtapaResponse atualizarStatus(UUID tccId, UUID etapaId, StatusCronogramaEtapa novoStatus, String observacao);

    CronogramaResumoDto obterResumo(UUID tccId);

    Map<UUID, CronogramaResumoDto> obterResumos(List<UUID> tccIds);
}

