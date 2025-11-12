package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.mensagem.TccMensagemDto;
import com.leonardo.gestaotcc.entity.TccMensagem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TccMensagemMapper {

    @Mapping(target = "autorId", source = "autor.id")
    @Mapping(target = "autorNome", source = "autor.nome")
    @Mapping(target = "autorEmail", source = "autor.email")
    TccMensagemDto.MensagemResponse toResponse(TccMensagem entity);
}

