package com.leonardo.gestaotcc.dto.convite;

import com.leonardo.gestaotcc.enums.StatusConvite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConviteResponseDto {
    private UUID id;
    private UUID alunoId;
    private String alunoNome;
    private String alunoEmail;
    private UUID orientadorId;
    private String orientadorNome;
    private UUID tccId;
    private String tccTitulo;
    private String mensagem;
    private StatusConvite status;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataResposta;
}