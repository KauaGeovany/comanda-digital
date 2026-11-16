package br.com.bomefit.comandadigital.dto.fornecedor;

import br.com.bomefit.comandadigital.model.enums.UnidadeEstoque;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CatalogoFornecedorRequest(@NotNull Long ingredienteId,
                                        @NotNull @PositiveOrZero BigDecimal preco,
                                        @NotNull UnidadeEstoque unidadeVenda) {}
