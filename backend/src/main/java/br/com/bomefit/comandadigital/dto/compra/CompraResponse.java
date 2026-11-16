package br.com.bomefit.comandadigital.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(Long id, Long fornecedorId, String fornecedor, String status,
                             BigDecimal valorTotal, LocalDateTime criadaEm, List<Item> itens) {
    public record Item(Long ingredienteId, String ingrediente, BigDecimal quantidade,
                       String unidadeVenda, BigDecimal precoUnitario, BigDecimal subtotal) {}
}
