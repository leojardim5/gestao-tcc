package com.leonardo.gestaotcc.controller;

import com.leonardo.gestaotcc.dto.auth.LoginRequestDto;
import com.leonardo.gestaotcc.dto.auth.LoginResponseDto;
import com.leonardo.gestaotcc.dto.auth.RegisterRequestDto;
import com.leonardo.gestaotcc.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica um usuário e retorna o token JWT")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registro de usuário", description = "Registra um novo usuário no sistema")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        LoginResponseDto response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}