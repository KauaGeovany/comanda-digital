package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.catalogo.*;
import br.com.bomefit.comandadigital.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pratos")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class PratoController {
    private final CatalogoService catalogoService;
    public PratoController(CatalogoService catalogoService) { this.catalogoService = catalogoService; }

    @GetMapping
    public Page<PratoResumoResponse> listar(@PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return catalogoService.listarPratos(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PratoResumoResponse criar(@Valid @RequestBody PratoRequest request) { return catalogoService.criarPrato(request); }

    @PutMapping("/{id}")
    public PratoResumoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PratoRequest request) {
        return catalogoService.atualizarPrato(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) { catalogoService.desativarPrato(id); }

    @GetMapping("/{id}/ficha")
    public FichaTecnicaResponse obterFicha(@PathVariable Long id) { return catalogoService.obterFicha(id); }

    @PutMapping("/{id}/ficha")
    public FichaTecnicaResponse salvarFicha(@PathVariable Long id, @Valid @RequestBody FichaTecnicaRequest request) {
        return catalogoService.salvarFicha(id, request);
    }

    @GetMapping("/{id}/custo")
    public CustoResponse custo(@PathVariable Long id) { return catalogoService.custo(id); }
}
