package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.entity.Notificacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    NotificacaoMapper INSTANCE = Mappers.getMapper(NotificacaoMapper.class);

    @Mapping(source = "usuario.id", target = "usuarioId")
    NotificacaoDto.NotificacaoResponse toResponseDto(Notificacao notificacao);

    List<NotificacaoDto.NotificacaoResponse> toResponseDtoList(List<Notificacao> notificacoes);
}
