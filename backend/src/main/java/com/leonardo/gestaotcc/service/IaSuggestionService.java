package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.ia.AiSuggestionDto;

public interface IaSuggestionService {

    AiSuggestionDto.SuggestionResponse sugerirOrientadores(AiSuggestionDto.SuggestionRequest request);
}

