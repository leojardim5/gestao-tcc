package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.SubmissaoDto;
import com.leonardo.gestaotcc.entity.Submissao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SubmissaoMapper {

    SubmissaoMapper INSTANCE = Mappers.getMapper(SubmissaoMapper.class);

    @Mapping(target = "tcc", ignore = true)
    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "enviadoEm", ignore = true)
    Submissao toEntity(SubmissaoDto.SubmissaoCreateRequest request);

    @Mapping(source = "tcc.id", target = "tccId")
    SubmissaoDto.SubmissaoResponse toResponse(Submissao submissao);
}
