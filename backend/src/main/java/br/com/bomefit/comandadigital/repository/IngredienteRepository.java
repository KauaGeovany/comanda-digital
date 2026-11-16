package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.Ingrediente;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCase(String sku);
    Page<Ingrediente> findAllByStatus(StatusRegistro status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Ingrediente i where i.id = :id")
    Optional<Ingrediente> buscarPorIdComBloqueio(@Param("id") Long id);
}
