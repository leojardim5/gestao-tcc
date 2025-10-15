package com.leonardo.gestaotcc.dto.auth;

import com.leonardo.gestaotcc.enums.PapelUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UUID id;
    private String nome;
    private String email;
    private PapelUsuario papel;
}
