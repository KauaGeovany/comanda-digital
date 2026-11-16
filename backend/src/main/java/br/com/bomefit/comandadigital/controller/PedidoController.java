package br.com.bomefit.comandadigital.controller;

import br.com.bomefit.comandadigital.dto.pedido.*;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.model.enums.StatusPedido;
import br.com.bomefit.comandadigital.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse criar(@Valid @RequestBody CriarPedidoRequest request, @AuthenticationPrincipal Usuario usuario) {
        return service.criar(request, usuario);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    public Page<PedidoResponse> meus(@AuthenticationPrincipal Usuario usuario, @PageableDefault(size=20) Pageable pageable) {
        return service.meus(usuario.getId(), pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) { return service.buscar(id, usuario); }

    @GetMapping("/{id}/status")
    public PedidoResponse status(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) { return service.buscar(id, usuario); }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','COZINHEIRO')")
    public Page<PedidoResponse> listar(@RequestParam(required=false) StatusPedido status,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required=false) String canal, @PageableDefault(size=20) Pageable pageable) {
        return service.listar(status, inicio == null ? null : inicio.atStartOfDay(), fim == null ? null : fim.plusDays(1).atStartOfDay().minusNanos(1), canal, pageable);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','COZINHEIRO')")
    public PedidoResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody AlterarStatusPedidoRequest request, @AuthenticationPrincipal Usuario usuario) {
        if (usuario.getPerfil().name().equals("COZINHEIRO") && request.status() != StatusPedido.EM_PREPARO && request.status() != StatusPedido.PRONTO)
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Cozinheiros podem atualizar somente as etapas de preparo.");
        return service.alterarStatus(id, request.status(), usuario);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarPedidoRequest request, @AuthenticationPrincipal Usuario usuario) {
        return service.cancelar(id, request.motivo(), usuario);
    }
}
