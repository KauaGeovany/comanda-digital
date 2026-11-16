package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.compra.*;
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

@Service
public class CompraService {
    private final PedidoCompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final FornecedorProdutoRepository catalogoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EstoqueMovimentacaoRepository movimentacaoRepository;
    private final HistoricoPrecoFornecedorRepository historicoRepository;

    public CompraService(PedidoCompraRepository compraRepository, FornecedorRepository fornecedorRepository,
                         FornecedorProdutoRepository catalogoRepository, IngredienteRepository ingredienteRepository,
                         EstoqueMovimentacaoRepository movimentacaoRepository, HistoricoPrecoFornecedorRepository historicoRepository) {
        this.compraRepository = compraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.catalogoRepository = catalogoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional(readOnly = true)
    public Page<CompraResponse> listar(Pageable pageable) { return compraRepository.findAll(pageable).map(this::toResponse); }

    @Transactional(readOnly = true)
    public CompraResponse buscar(Long id) { return toResponse(obter(id)); }

    @Transactional
    public CompraResponse criar(CriarCompraRequest request, Usuario usuario) {
        Fornecedor fornecedor = fornecedorRepository.findById(request.fornecedorId())
            .filter(f -> f.getStatus() == StatusRegistro.ATIVO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor ativo não encontrado."));
        PedidoCompra compra = new PedidoCompra();
        compra.setFornecedor(fornecedor);
        compra.setUsuario(usuario);
        compra.setStatus(StatusCompra.RASCUNHO);
        compra.setValorTotal(BigDecimal.ZERO);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCompraRequest linha : request.itens()) {
            FornecedorProduto produto = catalogoRepository.findByFornecedorIdAndIngredienteId(fornecedor.getId(), linha.ingredienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "O fornecedor não possui este ingrediente no catálogo: " + linha.ingredienteId()));
            BigDecimal subtotal = produto.getPreco().multiply(linha.quantidade()).setScale(2, RoundingMode.HALF_UP);
            PedidoCompraItem item = new PedidoCompraItem();
            item.setPedidoCompra(compra);
            item.setIngrediente(produto.getIngrediente());
            item.setQuantidade(linha.quantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setUnidadeVenda(produto.getUnidadeVenda());
            item.setSubtotal(subtotal);
            compra.getItens().add(item);
            total = total.add(subtotal);
        }
        compra.setValorTotal(total.setScale(2, RoundingMode.HALF_UP));
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse enviar(Long id) {
        PedidoCompra compra = obter(id);
        if (compra.getStatus() != StatusCompra.RASCUNHO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente compras em rascunho podem ser enviadas.");
        compra.setStatus(StatusCompra.ENVIADO);
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse receber(Long id, Usuario usuario) {
        PedidoCompra compra = obter(id);
        if (compra.getStatus() != StatusCompra.ENVIADO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente compras enviadas podem ser recebidas.");
        for (PedidoCompraItem item : compra.getItens()) {
            Ingrediente ingrediente = ingredienteRepository.buscarPorIdComBloqueio(item.getIngrediente().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
            BigDecimal quantidadeBase = converterQuantidade(item.getQuantidade(), item.getUnidadeVenda(), ingrediente.getUnidadePadrao());
            BigDecimal custoBase = converterPreco(item.getPrecoUnitario(), item.getUnidadeVenda(), ingrediente.getUnidadePadrao());
            EstoqueMovimentacao movimento = new EstoqueMovimentacao();
            movimento.setIngrediente(ingrediente);
            movimento.setTipo(TipoMovimentacao.ENTRADA);
            movimento.setQuantidade(quantidadeBase);
            movimento.setMotivo(MotivoMovimentacao.COMPRA);
            movimento.setCustoUnitario(custoBase);
            movimento.setPedidoCompra(compra);
            movimento.setUsuario(usuario);
            movimentacaoRepository.save(movimento);
            ingrediente.setCustoUnitario(custoBase);
            ingredienteRepository.save(ingrediente);

            HistoricoPrecoFornecedor historico = new HistoricoPrecoFornecedor();
            historico.setFornecedor(compra.getFornecedor());
            historico.setIngrediente(ingrediente);
            historico.setPreco(item.getPrecoUnitario());
            historico.setUnidadeVenda(item.getUnidadeVenda());
            historicoRepository.save(historico);
        }
        compra.setStatus(StatusCompra.RECEBIDO);
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse cancelar(Long id) {
        PedidoCompra compra = obter(id);
        if (compra.getStatus() == StatusCompra.RECEBIDO || compra.getStatus() == StatusCompra.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta compra não pode ser cancelada.");
        }
        compra.setStatus(StatusCompra.CANCELADO);
        return toResponse(compraRepository.save(compra));
    }

    private PedidoCompra obter(Long id) {
        return compraRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada."));
    }

    private CompraResponse toResponse(PedidoCompra compra) {
        return new CompraResponse(compra.getId(), compra.getFornecedor().getId(), compra.getFornecedor().getRazaoSocial(),
              compra.getStatus().name(), compra.getValorTotal(), compra.getCreatedAt(), compra.getItens().stream().map(i ->
            new CompraResponse.Item(i.getIngrediente().getId(), i.getIngrediente().getNome(), i.getQuantidade(),
                i.getUnidadeVenda().name(), i.getPrecoUnitario(), i.getSubtotal())).toList());
    }

    private BigDecimal converterQuantidade(BigDecimal quantidade, UnidadeEstoque unidadeVenda, UnidadeEstoque unidadeEstoque) {
        if (unidadeVenda == unidadeEstoque) return quantidade;
        if ((unidadeVenda == UnidadeEstoque.KG && unidadeEstoque == UnidadeEstoque.G)
            || (unidadeVenda == UnidadeEstoque.L && unidadeEstoque == UnidadeEstoque.ML)) return quantidade.multiply(BigDecimal.valueOf(1000));
        if ((unidadeVenda == UnidadeEstoque.G && unidadeEstoque == UnidadeEstoque.KG)
            || (unidadeVenda == UnidadeEstoque.ML && unidadeEstoque == UnidadeEstoque.L)) return quantidade.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unidades de compra incompatíveis com o ingrediente.");
    }

    private BigDecimal converterPreco(BigDecimal preco, UnidadeEstoque unidadeVenda, UnidadeEstoque unidadeEstoque) {
        if (unidadeVenda == unidadeEstoque) return preco;
        if ((unidadeVenda == UnidadeEstoque.KG && unidadeEstoque == UnidadeEstoque.G)
            || (unidadeVenda == UnidadeEstoque.L && unidadeEstoque == UnidadeEstoque.ML)) return preco.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        if ((unidadeVenda == UnidadeEstoque.G && unidadeEstoque == UnidadeEstoque.KG)
            || (unidadeVenda == UnidadeEstoque.ML && unidadeEstoque == UnidadeEstoque.L)) return preco.multiply(BigDecimal.valueOf(1000));
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unidades de preço incompatíveis com o ingrediente.");
    }
}
