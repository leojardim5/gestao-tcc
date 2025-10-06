package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.ComentarioDto;
import com.leonardo.gestaotcc.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    @Mapping(target = "submissao", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Comentario toEntity(ComentarioDto.ComentarioCreateRequest request);

    @Mapping(source = "submissao.id", target = "submissaoId")
    @Mapping(source = "autor", target = "autor")
    ComentarioDto.ComentarioResponse toResponseDto(Comentario comentario);

    List<ComentarioDto.ComentarioResponse> toResponseDtoList(List<Comentario> comentarios);
}
