package br.edu.unifor.missaomarte.servidor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifor.missaomarte.servidor.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNomeNormalizado(String nomeNormalizado);

    boolean existsByNomeNormalizado(String nomeNormalizado);
}
