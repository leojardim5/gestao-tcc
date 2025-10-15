package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.ComentarioDto;
import com.leonardo.gestaotcc.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    @Mapping(target = "submissao", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Comentario toEntity(ComentarioDto.ComentarioCreateRequest request);

    @Mapping(source = "submissao.id", target = "submissaoId")
    @Mapping(source = "autor.id", target = "autorId")
    @Mapping(source = "autor.nome", target = "autorNome")
    ComentarioDto.ComentarioResponse toResponse(Comentario comentario);
}
