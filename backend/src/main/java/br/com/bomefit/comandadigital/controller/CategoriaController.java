package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.catalogo.CategoriaRequest;
import br.com.bomefit.comandadigital.dto.catalogo.CategoriaResponse;
import br.com.bomefit.comandadigital.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categorias")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class CategoriaController {
    private final CatalogoService catalogoService;
    public CategoriaController(CatalogoService catalogoService) { this.catalogoService = catalogoService; }

    @GetMapping
    public Page<CategoriaResponse> listar(@PageableDefault(size = 20, sort = "ordem") Pageable pageable) {
        return catalogoService.listarCategorias(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest request) { return catalogoService.criarCategoria(request); }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return catalogoService.atualizarCategoria(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) { catalogoService.desativarCategoria(id); }
}
