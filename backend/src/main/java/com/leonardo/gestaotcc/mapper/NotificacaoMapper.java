package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.NotificacaoDto;
import com.leonardo.gestaotcc.entity.Notificacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    NotificacaoDto.NotificacaoResponse toResponse(Notificacao notificacao);
}
