package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.HistoricoPrecoFornecedor;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface HistoricoPrecoFornecedorRepository extends JpaRepository<HistoricoPrecoFornecedor, Long> {
    Page<HistoricoPrecoFornecedor> findAllByFornecedor_IdAndIngrediente_IdOrderByRegistradoEmDesc(
        Long fornecedorId, Long ingredienteId, Pageable pageable);
}
