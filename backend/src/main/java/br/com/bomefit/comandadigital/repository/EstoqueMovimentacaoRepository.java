package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.EstoqueMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacao, Long> {
    Page<EstoqueMovimentacao> findAllByIngrediente_IdOrderByCreatedAtDesc(Long ingredienteId, Pageable pageable);
    java.util.List<EstoqueMovimentacao> findAllByPedido_Id(Long pedidoId);
    boolean existsByIngrediente_Id(Long ingredienteId);

    @Query(value = "SELECT COALESCE(SUM(CASE WHEN tipo IN ('ENTRADA','ESTORNO') THEN quantidade ELSE -quantidade END), 0) FROM estoque_movimentacao WHERE ingrediente_id = :ingredienteId", nativeQuery = true)
    BigDecimal saldoAtual(@Param("ingredienteId") Long ingredienteId);
}
