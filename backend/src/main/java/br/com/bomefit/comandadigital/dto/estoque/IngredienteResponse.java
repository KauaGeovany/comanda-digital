package br.com.bomefit.comandadigital.dto.estoque;

import java.math.BigDecimal;

public record IngredienteResponse(Long id, String nome, String sku, String unidadePadrao,
                                  BigDecimal estoqueMinimo, BigDecimal custoUnitario, BigDecimal saldo, String status) {}
