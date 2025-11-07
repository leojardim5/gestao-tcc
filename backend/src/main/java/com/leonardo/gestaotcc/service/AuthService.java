package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.auth.LoginRequestDto;
import com.leonardo.gestaotcc.dto.auth.LoginResponseDto;
import com.leonardo.gestaotcc.dto.auth.RegisterRequestDto;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.exception.ConflictException;
import com.leonardo.gestaotcc.exception.ResourceNotFoundException;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.security.JwtService;
import com.leonardo.gestaotcc.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto register(RegisterRequestDto request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .papel(request.getPapel())
                .ativo(true)
                .perfilOrientador(sanitizePerfilOrientador(request.getPapel(), request.getPerfilOrientador()))
                .build();

        usuario = usuarioRepository.save(usuario);
        CustomUserDetails userDetails = toUserDetails(usuario);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .usuario(usuario)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        CustomUserDetails userDetails = toUserDetails(usuario);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .usuario(usuario)
                .build();
    }

    private CustomUserDetails toUserDetails(Usuario usuario) {
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPapel()
        );
    }

    private String sanitizePerfilOrientador(PapelUsuario papel, String perfil) {
        if (papel != PapelUsuario.ORIENTADOR) {
            return null;
        }
        if (perfil == null) {
            return null;
        }
        String trimmed = perfil.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}