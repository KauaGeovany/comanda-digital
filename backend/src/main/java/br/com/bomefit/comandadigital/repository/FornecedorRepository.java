package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.Fornecedor;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    Page<Fornecedor> findAllByStatus(StatusRegistro status, Pageable pageable);
}
