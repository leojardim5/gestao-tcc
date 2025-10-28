package com.leonardo.gestaotcc.repository;

import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Método para contagem de usuários ativos
    long countByAtivoTrue();
    
    // Método para buscar orientadores disponíveis
    Page<Usuario> findByPapelAndDisponivelParaOrientacaoTrue(PapelUsuario papel, Pageable pageable);
    
    // Método simples sem paginação
    List<Usuario> findByPapelAndDisponivelParaOrientacaoTrue(PapelUsuario papel);
}