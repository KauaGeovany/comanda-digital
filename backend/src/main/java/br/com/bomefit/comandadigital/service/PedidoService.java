package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.pedido.*;
import br.com.bomefit.comandadigital.model.entity.*;
import br.com.bomefit.comandadigital.model.enums.*;
import br.com.bomefit.comandadigital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PedidoService {
    private final PedidoRepository pedidos;
    private final PedidoItemRepository itens;
    private final PedidoStatusHistoricoRepository historico;
    private final PratoRepository pratos;
    private final FichaTecnicaRepository fichas;
    private final IngredienteRepository ingredientes;
    private final EstoqueMovimentacaoRepository movimentacoes;

    public PedidoService(PedidoRepository pedidos, PedidoItemRepository itens,
            PedidoStatusHistoricoRepository historico, PratoRepository pratos,
            FichaTecnicaRepository fichas, IngredienteRepository ingredientes,
            EstoqueMovimentacaoRepository movimentacoes) {
        this.pedidos = pedidos; this.itens = itens; this.historico = historico;
        this.pratos = pratos; this.fichas = fichas; this.ingredientes = ingredientes;
        this.movimentacoes = movimentacoes;
    }

    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request, Usuario cliente) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setStatusPagamento(StatusPagamento.PAGO_SIMULADO);
        pedido.setCanal("WEB");
        pedido.setEnderecoEntrega(request.enderecoEntrega().trim());
        pedido.setObservacoes(request.observacoes());
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoRequest linha : request.itens()) {
            Prato prato = pratos.findByIdAndStatus(linha.pratoId(), StatusPrato.ATIVO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prato indisponível: " + linha.pratoId()));
            FichaTecnica ficha = fichas.findByPratoId(prato.getId())
                .filter(f -> !f.getItens().isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prato sem ficha técnica válida: " + prato.getNome()));
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido); item.setPrato(prato); item.setQuantidade(linha.quantidade());
            item.setPrecoUnitario(prato.getPrecoVenda()); item.setObservacoes(linha.observacoes());
            pedido.getItens().add(item);
            total = total.add(prato.getPrecoVenda().multiply(BigDecimal.valueOf(linha.quantidade())));
        }
        pedido.setValorTotal(total.setScale(2, RoundingMode.HALF_UP));
        pedido = pedidos.save(pedido);
        registrarStatus(pedido, null, "Pedido recebido pelo canal WEB.");
        return resposta(pedido);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> meus(Long clienteId, Pageable pageable) {
        return pedidos.findAllByCliente_IdOrderByCreatedAtDesc(clienteId, pageable).map(this::resposta);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(StatusPedido status, LocalDateTime inicio, LocalDateTime fim, String canal, Pageable pageable) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O fim do período deve ser posterior ao início.");
        return pedidos.filtrar(status, inicio, fim, canal, pageable).map(this::resposta);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, Usuario solicitante) {
        Pedido p = pedidos.findById(id).orElseThrow(() -> naoEncontrado());
        if (solicitante.getPerfil() == PerfilUsuario.CLIENTE && !p.getCliente().getId().equals(solicitante.getId())) throw naoEncontrado();
        return resposta(p);
    }

    @Transactional
    public PedidoResponse alterarStatus(Long id, StatusPedido novoStatus, Usuario usuario) {
        Pedido pedido = obter(id);
        if (!transicaoValida(pedido.getStatus(), novoStatus)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Transição de status inválida.");
        if (novoStatus == StatusPedido.CONFIRMADO) consumirIngredientes(pedido, usuario);
        pedido.setStatus(novoStatus);
        pedidos.save(pedido);
        registrarStatus(pedido, usuario, "Status atualizado para " + novoStatus.name() + ".");
        return resposta(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, String motivo, Usuario usuario) {
        Pedido pedido = obter(id);
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE && !pedido.getCliente().getId().equals(usuario.getId()))
            throw naoEncontrado();
        if (usuario.getPerfil() == PerfilUsuario.COZINHEIRO)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cozinheiros não podem cancelar pedidos.");
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.FINALIZADO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido não pode ser cancelado.");
        if (pedido.getStatus().ordinal() >= StatusPedido.EM_PREPARO.ordinal()
            && usuario.getPerfil() != PerfilUsuario.ADMIN && usuario.getPerfil() != PerfilUsuario.GERENTE)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Após o início do preparo, somente gerência ou administração podem cancelar.");
        if (pedido.getStatus().ordinal() >= StatusPedido.CONFIRMADO.ordinal()) estornarIngredientes(pedido, usuario);
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setMotivoCancelamento(motivo.trim());
        pedidos.save(pedido);
        registrarStatus(pedido, usuario, "Cancelado: " + motivo.trim());
        return resposta(pedido);
    }

    private void consumirIngredientes(Pedido pedido, Usuario usuario) {
        Map<Long, BigDecimal> necessarios = new TreeMap<>();
        Map<Long, Ingrediente> referencias = new HashMap<>();
        for (PedidoItem linha : pedido.getItens()) {
            FichaTecnica ficha = fichas.findByPratoId(linha.getPrato().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Ficha técnica não encontrada."));
            for (FichaTecnicaItem receita : ficha.getItens()) {
                Ingrediente ingredienteRef = receita.getIngrediente();
                BigDecimal quantidade = CatalogoService.quantidadeNaUnidadeDeEstoque(receita.getQuantidade(), receita.getUnidade(), ingredienteRef.getUnidadePadrao())
                    .multiply(receita.getFatorCorrecao())
                    .multiply(BigDecimal.valueOf(linha.getQuantidade()))
                    .divide(BigDecimal.valueOf(ficha.getRendimento()), 6, RoundingMode.HALF_UP);
                necessarios.merge(ingredienteRef.getId(), quantidade, BigDecimal::add);
                referencias.put(ingredienteRef.getId(), ingredienteRef);
            }
        }
        Map<Long, Ingrediente> bloqueados = new HashMap<>();
        for (Long idIngrediente : necessarios.keySet()) {
            Ingrediente ingrediente = ingredientes.buscarPorIdComBloqueio(idIngrediente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente da ficha técnica não encontrado."));
            BigDecimal saldo = saldo(idIngrediente);
            if (saldo.compareTo(necessarios.get(idIngrediente)) < 0)
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estoque insuficiente para " + ingrediente.getNome() + ". Disponível: " + saldo + ", necessário: " + necessarios.get(idIngrediente));
            bloqueados.put(idIngrediente, ingrediente);
        }
        necessarios.forEach((id, quantidade) -> {
            EstoqueMovimentacao m = new EstoqueMovimentacao();
            m.setIngrediente(bloqueados.get(id)); m.setTipo(TipoMovimentacao.SAIDA);
            m.setQuantidade(quantidade.setScale(3, RoundingMode.HALF_UP)); m.setMotivo(MotivoMovimentacao.VENDA);
            m.setCustoUnitario(bloqueados.get(id).getCustoUnitario()); m.setPedido(pedido); m.setUsuario(usuario);
            movimentacoes.save(m);
        });
    }

    private void estornarIngredientes(Pedido pedido, Usuario usuario) {
        for (EstoqueMovimentacao original : movimentacoes.findAllByPedido_Id(pedido.getId())) {
            if (original.getTipo() != TipoMovimentacao.SAIDA || original.getMotivo() != MotivoMovimentacao.VENDA) continue;
            boolean jaEstornado = movimentacoes.findAllByPedido_Id(pedido.getId()).stream()
                .anyMatch(m -> m.getTipo() == TipoMovimentacao.ESTORNO && m.getIngrediente().getId().equals(original.getIngrediente().getId()));
            if (jaEstornado) continue;
            EstoqueMovimentacao estorno = new EstoqueMovimentacao();
            estorno.setIngrediente(original.getIngrediente()); estorno.setTipo(TipoMovimentacao.ESTORNO);
            estorno.setQuantidade(original.getQuantidade()); estorno.setMotivo(MotivoMovimentacao.ESTORNO);
            estorno.setCustoUnitario(original.getCustoUnitario()); estorno.setPedido(pedido); estorno.setUsuario(usuario);
            movimentacoes.save(estorno);
        }
    }

    private BigDecimal saldo(Long id) {
        BigDecimal saldo = movimentacoes.saldoAtual(id);
        return saldo == null ? BigDecimal.ZERO : saldo;
    }

    private boolean transicaoValida(StatusPedido atual, StatusPedido proximo) {
        return switch (atual) {
            case RECEBIDO -> proximo == StatusPedido.CONFIRMADO;
            case CONFIRMADO -> proximo == StatusPedido.EM_PREPARO;
            case EM_PREPARO -> proximo == StatusPedido.PRONTO;
            case PRONTO -> proximo == StatusPedido.SAIU_ENTREGA;
            case SAIU_ENTREGA -> proximo == StatusPedido.FINALIZADO;
            default -> false;
        };
    }

    private void registrarStatus(Pedido pedido, Usuario usuario, String observacao) {
        PedidoStatusHistorico h = new PedidoStatusHistorico();
        h.setPedido(pedido); h.setStatus(pedido.getStatus()); h.setUsuario(usuario); h.setObservacao(observacao);
        historico.save(h);
    }

    private Pedido obter(Long id) { return pedidos.buscarPorIdComBloqueio(id).orElseThrow(() -> naoEncontrado()); }
    private ResponseStatusException naoEncontrado() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."); }

    private PedidoResponse resposta(Pedido p) {
        List<PedidoResponse.Item> linhas = p.getItens().stream().map(i -> new PedidoResponse.Item(
            i.getPrato().getId(), i.getPrato().getNome(), i.getQuantidade(), i.getPrecoUnitario(),
            i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())).setScale(2, RoundingMode.HALF_UP), i.getObservacoes())).toList();
        List<PedidoResponse.StatusHistorico> timeline = historico.findAllByPedido_IdOrderByCreatedAtAsc(p.getId()).stream()
            .map(h -> new PedidoResponse.StatusHistorico(h.getStatus().name(), h.getObservacao(), h.getUsuario() == null ? null : h.getUsuario().getNome(), h.getCreatedAt())).toList();
        return new PedidoResponse(p.getId(), p.getStatus().name(), p.getValorTotal(), p.getEnderecoEntrega(), p.getObservacoes(),
            p.getStatusPagamento().name(), p.getCanal(), p.getCliente().getId(), p.getCliente().getNome(), p.getCliente().getEmail(),
            p.getMotivoCancelamento(), p.getCreatedAt(), linhas, timeline);
    }
}
