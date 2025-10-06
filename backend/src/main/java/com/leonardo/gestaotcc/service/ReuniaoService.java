package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.ReuniaoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReuniaoService {

    ReuniaoDto.ReuniaoResponse schedule(ReuniaoDto.ReuniaoCreateRequest reuniaoCreateRequest);

    void cancel(UUID id);

    Page<ReuniaoDto.ReuniaoResponse> listByTcc(UUID tccId, Pageable pageable);
}
