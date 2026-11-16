package br.com.bomefit.comandadigital.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CriarCompraRequest(@NotNull Long fornecedorId, @NotEmpty List<@Valid ItemCompraRequest> itens) {}
