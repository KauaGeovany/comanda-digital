package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.compra.*;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/compras")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class CompraController {
    private final CompraService compraService;
    public CompraController(CompraService compraService) { this.compraService = compraService; }

    @GetMapping
    public Page<CompraResponse> listar(@PageableDefault(size=20, sort="createdAt", direction=org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) { return compraService.listar(pageable); }
    @GetMapping("/{id}")
    public CompraResponse buscar(@PathVariable Long id) { return compraService.buscar(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse criar(@Valid @RequestBody CriarCompraRequest request, @AuthenticationPrincipal Usuario usuario) { return compraService.criar(request, usuario); }
    @PatchMapping("/{id}/enviar")
    public CompraResponse enviar(@PathVariable Long id) { return compraService.enviar(id); }
    @PostMapping("/{id}/receber")
    public CompraResponse receber(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) { return compraService.receber(id, usuario); }
    @PatchMapping("/{id}/cancelar")
    public CompraResponse cancelar(@PathVariable Long id) { return compraService.cancelar(id); }
}
