package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.ReuniaoDto;
import com.leonardo.gestaotcc.entity.Reuniao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReuniaoMapper {

    ReuniaoMapper INSTANCE = Mappers.getMapper(ReuniaoMapper.class);

    @Mapping(target = "tcc", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Reuniao toEntity(ReuniaoDto.ReuniaoCreateRequest request);

    @Mapping(source = "tcc.id", target = "tccId")
    ReuniaoDto.ReuniaoResponse toResponse(Reuniao reuniao);
}
