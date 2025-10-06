package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.entity.Tcc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface TccMapper {

    TccMapper INSTANCE = Mappers.getMapper(TccMapper.class);

    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "orientador", ignore = true)
    @Mapping(target = "coorientador", ignore = true)
    Tcc toEntity(TccDto.TccCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "orientador", ignore = true)
    @Mapping(target = "coorientador", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    void updateEntityFromDto(TccDto.TccUpdateRequest dto, @MappingTarget Tcc entity);

    @Mapping(source = "aluno", target = "aluno")
    @Mapping(source = "orientador", target = "orientador")
    @Mapping(source = "coorientador", target = "coorientador")
    TccDto.TccResponse toResponseDto(Tcc tcc);

    List<TccDto.TccResponse> toResponseDtoList(List<Tcc> tccs);
}
