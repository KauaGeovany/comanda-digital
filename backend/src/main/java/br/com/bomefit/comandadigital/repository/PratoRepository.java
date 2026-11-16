package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.Prato;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PratoRepository extends JpaRepository<Prato, Long> {
    Optional<Prato> findByIdAndStatus(Long id, StatusPrato status);
    Page<Prato> findAllByStatus(StatusPrato status, Pageable pageable);
    Page<Prato> findAllByCategoria_IdAndStatus(Long categoriaId, StatusPrato status, Pageable pageable);
}

