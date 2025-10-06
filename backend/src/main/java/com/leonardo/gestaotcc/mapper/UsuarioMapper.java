package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.UsuarioDto;
import com.leonardo.gestaotcc.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    Usuario toEntity(UsuarioDto.UsuarioCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "senhaHash", ignore = true)
    void updateEntityFromDto(UsuarioDto.UsuarioUpdateRequest dto, @MappingTarget Usuario entity);

    UsuarioDto.UsuarioResponse toResponseDto(Usuario usuario);

    List<UsuarioDto.UsuarioResponse> toResponseDtoList(List<Usuario> usuarios);
}
