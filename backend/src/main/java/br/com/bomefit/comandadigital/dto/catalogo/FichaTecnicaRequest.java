package br.com.bomefit.comandadigital.dto.catalogo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record FichaTecnicaRequest(@NotNull @Positive Integer rendimento,
                                  @Size(max=2000) String modoPreparo,
                                  @NotEmpty List<@Valid FichaTecnicaItemRequest> itens) {}
