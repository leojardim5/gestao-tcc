package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.cronograma.CronogramaEtapaDto;
import com.leonardo.gestaotcc.entity.CronogramaEtapa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CronogramaEtapaMapper {

    CronogramaEtapaDto.EtapaResponse toResponse(CronogramaEtapa entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tcc", ignore = true)
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : com.leonardo.gestaotcc.enums.StatusCronogramaEtapa.PENDENTE)")
    @Mapping(target = "concluidoEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    CronogramaEtapa toEntity(CronogramaEtapaDto.EtapaCreateRequest request);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "concluidoEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "tcc", ignore = true)
    CronogramaEtapa update(@MappingTarget CronogramaEtapa entity, CronogramaEtapaDto.EtapaUpdateRequest request);
}

