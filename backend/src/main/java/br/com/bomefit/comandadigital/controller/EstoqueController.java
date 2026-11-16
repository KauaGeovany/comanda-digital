package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.estoque.*;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.service.EstoqueService;
import br.com.bomefit.comandadigital.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class EstoqueController {
    private final EstoqueService estoqueService;
    private final IngredienteService ingredienteService;
    public EstoqueController(EstoqueService estoqueService, IngredienteService ingredienteService) {
        this.estoqueService = estoqueService; this.ingredienteService = ingredienteService;
    }

    @GetMapping("/ingredientes")
    public Page<IngredienteResponse> ingredientes(@PageableDefault(size=20, sort="nome") Pageable pageable) {
        return ingredienteService.listar(pageable);
    }
    @PostMapping("/ingredientes")
    @ResponseStatus(HttpStatus.CREATED)
    public IngredienteResponse criarIngrediente(@Valid @RequestBody IngredienteRequest request) { return ingredienteService.criar(request); }
    @PutMapping("/ingredientes/{id}")
    public IngredienteResponse atualizarIngrediente(@PathVariable Long id, @Valid @RequestBody IngredienteRequest request) { return ingredienteService.atualizar(id, request); }
    @DeleteMapping("/ingredientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarIngrediente(@PathVariable Long id) { ingredienteService.desativar(id); }

    @GetMapping("/estoque/saldo")
    public Page<IngredienteResponse> saldos(@PageableDefault(size=50, sort="nome") Pageable pageable) { return estoqueService.listarSaldos(pageable, false); }
    @GetMapping("/estoque/alertas")
    public Page<IngredienteResponse> alertas(@PageableDefault(size=50, sort="nome") Pageable pageable) { return estoqueService.listarSaldos(pageable, true); }
    @GetMapping("/estoque/movimentacoes")
    public Page<MovimentacaoResponse> movimentacoes(@RequestParam(required=false) Long ingredienteId,
                                                    @PageableDefault(size=30, sort="createdAt", direction=org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return estoqueService.listarMovimentacoes(ingredienteId, pageable);
    }
    @PostMapping("/estoque/entrada-manual")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoResponse entrada(@Valid @RequestBody EstoqueEntradaRequest request, @AuthenticationPrincipal Usuario usuario) {
        return estoqueService.registrarEntrada(request, usuario);
    }
    @PostMapping("/estoque/movimentacao")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoResponse perda(@Valid @RequestBody EstoqueSaidaRequest request, @AuthenticationPrincipal Usuario usuario) {
        return estoqueService.registrarPerda(request, usuario);
    }
}
