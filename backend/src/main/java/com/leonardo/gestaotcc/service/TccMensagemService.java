package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.mensagem.TccMensagemDto;

import java.util.List;
import java.util.UUID;

public interface TccMensagemService {
    List<TccMensagemDto.MensagemResponse> listarMensagens(UUID tccId);

    TccMensagemDto.MensagemResponse criarMensagem(UUID tccId, TccMensagemDto.CriarMensagemRequest request);
}

