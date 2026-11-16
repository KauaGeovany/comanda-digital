package br.com.bomefit.comandadigital.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarPedidoRequest(@NotBlank @Size(max=500) String motivo) {}
