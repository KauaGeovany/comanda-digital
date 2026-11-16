package br.com.bomefit.comandadigital.dto.estoque;

import br.com.bomefit.comandadigital.model.enums.UnidadeEstoque;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record IngredienteRequest(@NotBlank @Size(max=140) String nome, @NotBlank @Size(max=80) String sku,
                                  @NotNull UnidadeEstoque unidadePadrao,
                                  @NotNull @PositiveOrZero BigDecimal estoqueMinimo,
                                  @NotNull @PositiveOrZero BigDecimal custoUnitario) {}
