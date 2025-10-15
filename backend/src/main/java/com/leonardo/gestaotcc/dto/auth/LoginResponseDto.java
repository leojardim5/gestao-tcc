package com.leonardo.gestaotcc.dto.auth;

import com.leonardo.gestaotcc.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private Usuario usuario;
}
