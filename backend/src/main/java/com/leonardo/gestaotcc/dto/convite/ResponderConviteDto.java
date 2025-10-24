package com.leonardo.gestaotcc.dto.convite;

import com.leonardo.gestaotcc.enums.StatusConvite;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderConviteDto {
    @NotNull(message = "Status da resposta é obrigatório")
    private StatusConvite status;
}