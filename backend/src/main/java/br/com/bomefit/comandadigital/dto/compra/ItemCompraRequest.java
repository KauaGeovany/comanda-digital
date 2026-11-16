package br.com.bomefit.comandadigital.dto.compra;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemCompraRequest(@NotNull Long ingredienteId, @NotNull @Positive BigDecimal quantidade) {}
