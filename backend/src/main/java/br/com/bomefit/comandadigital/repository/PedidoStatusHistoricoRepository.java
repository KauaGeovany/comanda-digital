package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.PedidoStatusHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoStatusHistoricoRepository extends JpaRepository<PedidoStatusHistorico, Long> {
    List<PedidoStatusHistorico> findAllByPedido_IdOrderByCreatedAtAsc(Long pedidoId);
}
