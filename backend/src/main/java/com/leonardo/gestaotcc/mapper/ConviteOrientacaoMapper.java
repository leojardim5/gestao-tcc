package com.leonardo.gestaotcc.mapper;

import com.leonardo.gestaotcc.dto.convite.ConviteResponseDto;
import com.leonardo.gestaotcc.dto.convite.EnviarConviteDto;
import com.leonardo.gestaotcc.entity.ConviteOrientacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConviteOrientacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "orientador", ignore = true)
    @Mapping(target = "tcc", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataEnvio", ignore = true)
    @Mapping(target = "dataResposta", ignore = true)
    ConviteOrientacao toEntity(EnviarConviteDto dto);

    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "aluno.nome", target = "alunoNome")
    @Mapping(source = "aluno.email", target = "alunoEmail")
    @Mapping(source = "orientador.id", target = "orientadorId")
    @Mapping(source = "orientador.nome", target = "orientadorNome")
    @Mapping(source = "tcc.id", target = "tccId")
    @Mapping(source = "tcc.titulo", target = "tccTitulo")
    ConviteResponseDto toResponse(ConviteOrientacao entity);
}
