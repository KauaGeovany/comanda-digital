package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<Usuario> findAllByPerfilNot(br.com.bomefit.comandadigital.model.enums.PerfilUsuario perfil, Pageable pageable);
}


