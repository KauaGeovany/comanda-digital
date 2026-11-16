package br.com.bomefit.comandadigital.dto.estoque;

import br.com.bomefit.comandadigital.model.enums.MotivoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EstoqueSaidaRequest(@NotNull Long ingredienteId, @NotNull @Positive BigDecimal quantidade,
                                  @NotNull MotivoMovimentacao motivo, @Size(max=100) String lote,
                                  LocalDate validade) {}
