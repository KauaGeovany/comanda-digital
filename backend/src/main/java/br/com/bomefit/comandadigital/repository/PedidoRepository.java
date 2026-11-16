package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.Pedido;
import br.com.bomefit.comandadigital.model.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarPorIdComBloqueio(@Param("id") Long id);
    Page<Pedido> findAllByCliente_IdOrderByCreatedAtDesc(Long clienteId, Pageable pageable);
    @Query("select p from Pedido p where (:status is null or p.status = :status) and (:inicio is null or p.createdAt >= :inicio) and (:fim is null or p.createdAt <= :fim) and (:canal is null or p.canal = :canal) order by p.createdAt desc")
    Page<Pedido> filtrar(@Param("status") StatusPedido status, @Param("inicio") LocalDateTime inicio,
                         @Param("fim") LocalDateTime fim, @Param("canal") String canal, Pageable pageable);
    Optional<Pedido> findByIdAndCliente_Id(Long id, Long clienteId);

    @Query(value = "select count(*) from pedido p where p.created_at >= :inicio and p.created_at < :fim and p.status <> 'CANCELADO'", nativeQuery = true)
    long contarPedidosNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "select coalesce(sum(p.valor_total), 0) from pedido p where p.created_at >= :inicio and p.created_at < :fim and p.status <> 'CANCELADO'", nativeQuery = true)
    java.math.BigDecimal faturamentoNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    long countByStatus(StatusPedido status);

    @Query(value = "select date(p.created_at) as data, count(*) as pedidos, coalesce(sum(p.valor_total), 0) as faturamento from pedido p where p.created_at >= :inicio and p.created_at < :fim and p.status <> 'CANCELADO' group by date(p.created_at) order by date(p.created_at)", nativeQuery = true)
    List<Object[]> vendasDiarias(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "select pi.prato_id, pr.nome, sum(pi.quantidade) as unidades, sum(pi.quantidade * pi.preco_unitario) as faturamento from pedido_item pi join prato pr on pr.id = pi.prato_id join pedido p on p.id = pi.pedido_id where p.created_at >= :inicio and p.created_at < :fim and p.status <> 'CANCELADO' group by pi.prato_id, pr.nome order by unidades desc, faturamento desc limit 5", nativeQuery = true)
    List<Object[]> pratosMaisVendidos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
