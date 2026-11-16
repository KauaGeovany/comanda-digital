package br.com.bomefit.comandadigital.dto.pedido;

import br.com.bomefit.comandadigital.model.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusPedidoRequest(@NotNull StatusPedido status) {}
