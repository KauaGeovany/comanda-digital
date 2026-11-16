package br.com.bomefit.comandadigital.dto.pedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemPedidoRequest(@NotNull Long pratoId, @NotNull @Positive Integer quantidade,
                                @Size(max=500) String observacoes) {}
