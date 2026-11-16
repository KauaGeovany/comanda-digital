package br.com.bomefit.comandadigital.dto.catalogo;

import java.math.BigDecimal;

public record CustoResponse(Long pratoId, BigDecimal custoPorPorcao, BigDecimal precoVenda,
                            BigDecimal foodCostPercentual, String faixa, boolean alerta) {}
