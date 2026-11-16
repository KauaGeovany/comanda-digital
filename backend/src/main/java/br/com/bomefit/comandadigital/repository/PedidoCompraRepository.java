package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.PedidoCompra;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {
    Page<PedidoCompra> findAllByStatusOrderByCreatedAtDesc(StatusCompra status, Pageable pageable);
}
