package br.com.bomefit.comandadigital.dto.estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EstoqueEntradaRequest(@NotNull Long ingredienteId, @NotNull @Positive BigDecimal quantidade,
                                    @Size(max=100) String lote, LocalDate validade,
                                    @NotNull @PositiveOrZero BigDecimal custoUnitario) {}
