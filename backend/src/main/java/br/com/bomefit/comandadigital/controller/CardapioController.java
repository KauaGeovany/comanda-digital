package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.catalogo.CategoriaResponse;
import br.com.bomefit.comandadigital.dto.catalogo.PratoResumoResponse;
import br.com.bomefit.comandadigital.service.CatalogoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cardapio")
public class CardapioController {
    private final CatalogoService catalogoService;
    public CardapioController(CatalogoService catalogoService) { this.catalogoService = catalogoService; }

    @GetMapping("/categorias")
    public List<CategoriaResponse> categorias() { return catalogoService.categoriasPublicas(); }

    @GetMapping
    public Page<PratoResumoResponse> listar(@RequestParam(required = false) Long categoriaId,
                                            @PageableDefault(size = 12, sort = "nome") Pageable pageable) {
        return catalogoService.cardapio(categoriaId, pageable);
    }

    @GetMapping("/{id}")
    public PratoResumoResponse detalhe(@PathVariable Long id) { return catalogoService.detalhePublico(id); }
}
