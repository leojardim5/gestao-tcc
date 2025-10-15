package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPapel()
        );
    }
}
