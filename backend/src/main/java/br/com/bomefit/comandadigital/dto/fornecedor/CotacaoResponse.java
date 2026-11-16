package br.com.bomefit.comandadigital.dto.fornecedor;

import java.math.BigDecimal;

public record CotacaoResponse(Long fornecedorId, String fornecedor, String cnpj,
                              Long ingredienteId, String ingrediente, BigDecimal preco,
                              String unidadeVenda) {}
