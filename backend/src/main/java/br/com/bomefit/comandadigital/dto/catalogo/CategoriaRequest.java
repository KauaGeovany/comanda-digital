package br.com.bomefit.comandadigital.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(@NotBlank @Size(max = 80) String nome, @Size(max = 255) String descricao, Integer ordem) {}
