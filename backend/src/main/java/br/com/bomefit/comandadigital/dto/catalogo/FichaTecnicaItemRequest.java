package br.com.bomefit.comandadigital.dto.catalogo;

import br.com.bomefit.comandadigital.model.enums.UnidadeReceita;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record FichaTecnicaItemRequest(@NotNull Long ingredienteId, @NotNull @Positive BigDecimal quantidade,
                                      @NotNull UnidadeReceita unidade,
                                      @NotNull @DecimalMin("1.0") BigDecimal fatorCorrecao) {}
