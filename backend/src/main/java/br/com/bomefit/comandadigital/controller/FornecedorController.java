package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.fornecedor.*;
import br.com.bomefit.comandadigital.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/fornecedores")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class FornecedorController {
    private final FornecedorService fornecedorService;
    public FornecedorController(FornecedorService fornecedorService) { this.fornecedorService = fornecedorService; }

    @GetMapping
    public Page<FornecedorResponse> listar(@PageableDefault(size=20, sort="razaoSocial") Pageable pageable) { return fornecedorService.listar(pageable); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorResponse criar(@Valid @RequestBody FornecedorRequest request) { return fornecedorService.criar(request); }
    @PutMapping("/{id}")
    public FornecedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorRequest request) { return fornecedorService.atualizar(id, request); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) { fornecedorService.desativar(id); }
    @PutMapping("/{id}/catalogo")
    public CotacaoResponse atualizarCatalogo(@PathVariable Long id, @Valid @RequestBody CatalogoFornecedorRequest request) { return fornecedorService.atualizarCatalogo(id, request); }
    @GetMapping("/cotacao/{ingredienteId}")
    public List<CotacaoResponse> cotar(@PathVariable Long ingredienteId) { return fornecedorService.cotar(ingredienteId); }

    @GetMapping("/{fornecedorId}/catalogo/{ingredienteId}/historico")
    public Page<HistoricoPrecoResponse> historico(@PathVariable Long fornecedorId, @PathVariable Long ingredienteId,
            @PageableDefault(size=50, sort="registradoEm", direction=org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return fornecedorService.historico(fornecedorId, ingredienteId, pageable);
    }
}
