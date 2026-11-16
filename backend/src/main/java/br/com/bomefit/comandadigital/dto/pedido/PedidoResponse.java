package br.com.bomefit.comandadigital.dto.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(Long id, String status, BigDecimal valorTotal, String enderecoEntrega,
                             String observacoes, String statusPagamento, String canal,
                             Long clienteId, String clienteNome, String clienteEmail,
                             String motivoCancelamento, LocalDateTime criadoEm,
                             List<Item> itens, List<StatusHistorico> timeline) {
    public record Item(Long pratoId, String prato, Integer quantidade, BigDecimal precoUnitario,
                       BigDecimal subtotal, String observacoes) {}
    public record StatusHistorico(String status, String observacao, String usuario, LocalDateTime data) {}
}
