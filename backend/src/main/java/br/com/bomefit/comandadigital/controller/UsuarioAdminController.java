package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.usuario.*;
import br.com.bomefit.comandadigital.service.UsuarioAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {
    private final UsuarioAdminService service;
    public UsuarioAdminController(UsuarioAdminService service) { this.service = service; }
    @GetMapping
    public Page<UsuarioInternoResponse> listar(@PageableDefault(size=20, sort="nome") Pageable pageable) { return service.listar(pageable); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioInternoResponse criar(@Valid @RequestBody UsuarioInternoRequest request) { return service.criar(request); }
    @PutMapping("/{id}")
    public UsuarioInternoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarUsuarioInternoRequest request) { return service.atualizar(id, request); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) { service.desativar(id); }
}
