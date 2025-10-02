
package br.unirio.tcc.gestao_tcc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.unirio.tcc.gestao_tcc.domain.Usuario;
import br.unirio.tcc.gestao_tcc.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario salvar(Usuario usuario) {
        return repo.save(usuario);
    }

    public Usuario buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}