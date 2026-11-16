package br.com.bomefit.comandadigital.dto.dashboard;

import br.com.bomefit.comandadigital.dto.estoque.IngredienteResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
    long pedidosHoje,
    BigDecimal faturamentoHoje,
    BigDecimal ticketMedioHoje,
    LocalDate inicio,
    LocalDate fim,
    long pedidosEmPreparo,
    BigDecimal foodCostMedio,
    List<VendaDiaria> vendasDiarias,
    List<PratoMaisVendido> pratosMaisVendidos,
    List<IngredienteResponse> alertasEstoque
) {
    public record VendaDiaria(LocalDate data, long pedidos, BigDecimal faturamento) {}
    public record PratoMaisVendido(Long pratoId, String nome, long unidadesVendidas, BigDecimal faturamento) {}
}
