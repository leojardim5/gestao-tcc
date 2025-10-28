package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.ReuniaoDto;
import com.leonardo.gestaotcc.entity.Reuniao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReuniaoMapper {

    @Mapping(target = "tcc", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Reuniao toEntity(ReuniaoDto.ReuniaoCreateRequest request);

    @Mapping(source = "tcc.id", target = "tccId")
    ReuniaoDto.ReuniaoResponse toResponse(Reuniao reuniao);
}
