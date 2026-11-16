package br.com.bomefit.comandadigital.dto.catalogo;

import java.math.BigDecimal;

public record PratoResumoResponse(Long id, String nome, String descricao, String fotoUrl, BigDecimal precoVenda,
                                  Integer tempoPreparoMin, String categoria, String status) {}
