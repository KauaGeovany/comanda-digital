package br.com.bomefit.comandadigital.dto.catalogo;

import java.math.BigDecimal;
import java.util.List;

public record FichaTecnicaResponse(Long pratoId, Integer rendimento, String modoPreparo, List<Item> itens) {
    public record Item(Long ingredienteId, String ingrediente, BigDecimal quantidade,
                       String unidade, BigDecimal fatorCorrecao) {}
}
