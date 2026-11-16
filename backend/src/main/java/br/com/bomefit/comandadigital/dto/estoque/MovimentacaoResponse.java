package br.com.bomefit.comandadigital.dto.estoque;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentacaoResponse(Long id, Long ingredienteId, String ingrediente, String tipo,
                                   BigDecimal quantidade, String motivo, String lote, LocalDate validade,
                                   BigDecimal custoUnitario, Long pedidoId, Long pedidoCompraId,
                                   String usuario, LocalDateTime data) {}
