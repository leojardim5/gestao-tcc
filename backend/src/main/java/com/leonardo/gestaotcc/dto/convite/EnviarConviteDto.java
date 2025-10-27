package com.leonardo.gestaotcc.dto.convite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnviarConviteDto {
    @NotNull(message = "ID do orientador é obrigatório")
    private UUID orientadorId;

    @NotNull(message = "ID do TCC é obrigatório")
    private UUID tccId;

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;
}