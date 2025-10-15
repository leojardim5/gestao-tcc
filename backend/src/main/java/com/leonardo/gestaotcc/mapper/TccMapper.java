package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.TccDto;
import com.leonardo.gestaotcc.entity.Tcc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
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

    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "aluno.nome", target = "alunoNome")
    @Mapping(source = "orientador.id", target = "orientadorId")
    @Mapping(source = "orientador.nome", target = "orientadorNome")
    @Mapping(source = "coorientador.id", target = "coorientadorId")
    @Mapping(source = "coorientador.nome", target = "coorientadorNome")
    TccDto.TccResponse toResponse(Tcc tcc);
}
