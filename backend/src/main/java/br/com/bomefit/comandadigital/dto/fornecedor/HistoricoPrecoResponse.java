package br.com.bomefit.comandadigital.dto.fornecedor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoPrecoResponse(Long id, BigDecimal preco, String unidadeVenda, LocalDateTime registradoEm) {}
