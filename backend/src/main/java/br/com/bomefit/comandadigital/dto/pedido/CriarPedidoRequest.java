package br.com.bomefit.comandadigital.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CriarPedidoRequest(@NotBlank @Size(max=500) String enderecoEntrega,
                                 @Size(max=1000) String observacoes,
                                 @NotEmpty List<@Valid ItemPedidoRequest> itens) {}
