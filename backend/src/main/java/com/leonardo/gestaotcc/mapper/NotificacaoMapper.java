package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.entity.Notificacao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    NotificacaoMapper INSTANCE = Mappers.getMapper(NotificacaoMapper.class);

    NotificacaoDto.NotificacaoResponse toResponse(Notificacao notificacao);
}
