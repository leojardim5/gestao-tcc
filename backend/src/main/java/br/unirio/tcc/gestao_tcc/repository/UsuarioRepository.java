package br.unirio.tcc.gestao_tcc.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.unirio.tcc.gestao_tcc.domain.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

    Optional<Usuario> findByEmail(String email);


}