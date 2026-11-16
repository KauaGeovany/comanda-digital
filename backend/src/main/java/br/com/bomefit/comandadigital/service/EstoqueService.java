package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.estoque.*;
import br.com.bomefit.comandadigital.model.entity.*;
import br.com.bomefit.comandadigital.model.enums.*;
import br.com.bomefit.comandadigital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EstoqueService {
    private final IngredienteRepository ingredienteRepository;
    private final EstoqueMovimentacaoRepository movimentacaoRepository;

    public EstoqueService(IngredienteRepository ingredienteRepository, EstoqueMovimentacaoRepository movimentacaoRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public Page<IngredienteResponse> listarSaldos(Pageable pageable, boolean somenteAlertas) {
        if (!somenteAlertas) {
            return ingredienteRepository.findAllByStatus(StatusRegistro.ATIVO, pageable).map(this::saldoResponse);
        }
        List<IngredienteResponse> todos = ingredienteRepository.findAllByStatus(StatusRegistro.ATIVO, Pageable.unpaged())
            .stream().map(this::saldoResponse)
            .filter(item -> item.saldo().compareTo(item.estoqueMinimo()) < 0)
            .toList();
        int start = (int) Math.min(pageable.getOffset(), todos.size());
        int end = (int) Math.min(start + pageable.getPageSize(), todos.size());
        return new PageImpl<>(todos.subList(start, end), pageable, todos.size());
    }

    @Transactional(readOnly = true)
    public Page<IngredienteResponse> listarIngredientes(Pageable pageable) {
        return ingredienteRepository.findAll(pageable).map(this::saldoResponse);
    }

    @Transactional
    public IngredienteResponse criarIngrediente(IngredienteRequest request) {
        if (ingredienteRepository.existsBySkuIgnoreCase(request.sku().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado.");
        }
        Ingrediente ingrediente = new Ingrediente();
        aplicar(ingrediente, request);
        ingrediente.setStatus(StatusRegistro.ATIVO);
        return saldoResponse(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public IngredienteResponse atualizarIngrediente(Long id, IngredienteRequest request) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        ingredienteRepository.findBySkuIgnoreCase(request.sku().trim())
            .filter(existente -> !existente.getId().equals(id))
            .ifPresent(existente -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado."); });
        aplicar(ingrediente, request);
        return saldoResponse(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public void desativarIngrediente(Long id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        ingrediente.setStatus(StatusRegistro.INATIVO);
        ingredienteRepository.save(ingrediente);
    }

    @Transactional
    public MovimentacaoResponse registrarEntrada(EstoqueEntradaRequest request, Usuario usuario) {
        Ingrediente ingrediente = ingredienteRepository.buscarPorIdComBloqueio(request.ingredienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        EstoqueMovimentacao movimento = new EstoqueMovimentacao();
        movimento.setIngrediente(ingrediente);
        movimento.setTipo(TipoMovimentacao.ENTRADA);
        movimento.setMotivo(MotivoMovimentacao.AJUSTE);
        movimento.setQuantidade(request.quantidade());
        movimento.setLote(request.lote());
        movimento.setValidade(request.validade());
        movimento.setCustoUnitario(request.custoUnitario());
        movimento.setUsuario(usuario);
        ingrediente.setCustoUnitario(request.custoUnitario());
        ingredienteRepository.save(ingrediente);
        return movimentoResponse(movimentacaoRepository.save(movimento));
    }

    @Transactional
    public MovimentacaoResponse registrarPerda(EstoqueSaidaRequest request, Usuario usuario) {
        if (!List.of(MotivoMovimentacao.DESPERDICIO, MotivoMovimentacao.VENCIMENTO,
            MotivoMovimentacao.QUEBRA, MotivoMovimentacao.USO_INTERNO).contains(request.motivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Motivo de saída manual inválido.");
        }
        Ingrediente ingrediente = ingredienteRepository.buscarPorIdComBloqueio(request.ingredienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        BigDecimal saldo = saldo(ingrediente.getId());
        if (saldo.compareTo(request.quantidade()) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estoque insuficiente para registrar a saída.");
        }
        EstoqueMovimentacao movimento = new EstoqueMovimentacao();
        movimento.setIngrediente(ingrediente);
        movimento.setTipo(TipoMovimentacao.SAIDA);
        movimento.setMotivo(request.motivo());
        movimento.setQuantidade(request.quantidade());
        movimento.setLote(request.lote());
        movimento.setValidade(request.validade());
        movimento.setCustoUnitario(ingrediente.getCustoUnitario());
        movimento.setUsuario(usuario);
        return movimentoResponse(movimentacaoRepository.save(movimento));
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoResponse> listarMovimentacoes(Long ingredienteId, Pageable pageable) {
        Page<EstoqueMovimentacao> page = ingredienteId == null
            ? movimentacaoRepository.findAll(pageable)
            : movimentacaoRepository.findAllByIngrediente_IdOrderByCreatedAtDesc(ingredienteId, pageable);
        return page.map(this::movimentoResponse);
    }

    public BigDecimal saldo(Long ingredienteId) {
        BigDecimal saldo = movimentacaoRepository.saldoAtual(ingredienteId);
        return saldo == null ? BigDecimal.ZERO : saldo;
    }

    public BigDecimal saldo(Ingrediente ingrediente) { return saldo(ingrediente.getId()); }

    private IngredienteResponse saldoResponse(Ingrediente i) {
        return new IngredienteResponse(i.getId(), i.getNome(), i.getSku(), i.getUnidadePadrao().name(),
            i.getEstoqueMinimo(), i.getCustoUnitario(), saldo(i.getId()), i.getStatus().name());
    }

    private MovimentacaoResponse movimentoResponse(EstoqueMovimentacao m) {
        return new MovimentacaoResponse(m.getId(), m.getIngrediente().getId(), m.getIngrediente().getNome(),
            m.getTipo().name(), m.getQuantidade(), m.getMotivo().name(), m.getLote(), m.getValidade(),
            m.getCustoUnitario(), m.getPedido() == null ? null : m.getPedido().getId(),
            m.getPedidoCompra() == null ? null : m.getPedidoCompra().getId(),
            m.getUsuario().getNome(), m.getCreatedAt());
    }

    private void aplicar(Ingrediente ingrediente, IngredienteRequest request) {
        ingrediente.setNome(request.nome().trim());
        ingrediente.setSku(request.sku().trim().toUpperCase());
        ingrediente.setUnidadePadrao(request.unidadePadrao());
        ingrediente.setEstoqueMinimo(request.estoqueMinimo());
        ingrediente.setCustoUnitario(request.custoUnitario());
    }
}
