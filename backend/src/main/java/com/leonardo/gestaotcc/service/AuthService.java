package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.auth.LoginRequestDto;
import com.leonardo.gestaotcc.dto.auth.LoginResponseDto;
import com.leonardo.gestaotcc.dto.auth.RegisterRequestDto;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.security.CustomUserDetails;
import com.leonardo.gestaotcc.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto register(RegisterRequestDto request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .papel(PapelUsuario.valueOf(request.getPapel()))
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);

        CustomUserDetails userDetails = new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPapel()
        );

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .usuario(usuario)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar senha manualmente
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new RuntimeException("Senha incorreta");
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPapel()
        );

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .usuario(usuario)
                .build();
    }
}