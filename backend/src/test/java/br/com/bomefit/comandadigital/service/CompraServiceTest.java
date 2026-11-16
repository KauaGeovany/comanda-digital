package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.compra.CompraResponse;
import br.com.bomefit.comandadigital.model.entity.*;
import br.com.bomefit.comandadigital.model.enums.*;
import br.com.bomefit.comandadigital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private PedidoCompraRepository compraRepository;

    @Mock
    private PedidoCompraItemRepository itemRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private FornecedorProdutoRepository fornecedorProdutoRepository;

    @Mock
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    @Mock
    private HistoricoPrecoFornecedorRepository historicoRepository;

    @InjectMocks
    private CompraService compraService;

    private Usuario admin;
    private Fornecedor fornecedor;
    private Ingrediente ingrediente;
    private PedidoCompra compra;

    @BeforeEach
    void setup() {
        admin = new Usuario();
        admin.setId(1L);
        admin.setNome("Admin");
        admin.setPerfil(PerfilUsuario.ADMIN);

        fornecedor = new Fornecedor();
        fornecedor.setId(10L);
        fornecedor.setRazaoSocial("Hortifruti Bom Preço");

        ingrediente = new Ingrediente();
        ingrediente.setId(100L);
        ingrediente.setNome("Tomate");
        ingrediente.setUnidadePadrao(UnidadeEstoque.KG);
        ingrediente.setCustoUnitario(new BigDecimal("5.00")); // Custo antigo: R$ 5,00/KG

        compra = new PedidoCompra();
        compra.setId(1000L);
        compra.setFornecedor(fornecedor);
        compra.setStatus(StatusCompra.ENVIADO);
        compra.setValorTotal(new BigDecimal("120.00"));
        compra.setItens(new ArrayList<>());

        PedidoCompraItem item = new PedidoCompraItem();
        item.setPedidoCompra(compra);
        item.setIngrediente(ingrediente);
        item.setQuantidade(new BigDecimal("20")); // 20 KG
        item.setUnidadeVenda(UnidadeEstoque.KG);
        item.setPrecoUnitario(new BigDecimal("6.00")); // Preço novo pago: R$ 6,00/KG
        item.setSubtotal(new BigDecimal("120.00"));
        compra.getItens().add(item);
    }

    @Test
    @DisplayName("RN05: Ao receber mercadoria de compra, deve atualizar o custo unitário do ingrediente e dar entrada no estoque")
    void testRN05_ReceberCompra_AtualizaCustoUnitarioEEstoque() {
        when(compraRepository.findById(1000L)).thenReturn(Optional.of(compra));
        when(ingredienteRepository.buscarPorIdComBloqueio(100L)).thenReturn(Optional.of(ingrediente));
        when(compraRepository.save(any(PedidoCompra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompraResponse response = compraService.receber(1000L, admin);

        assertEquals("RECEBIDO", response.status());

        // Custo do ingrediente deve ter sido atualizado para R$ 6.00
        assertEquals(new BigDecimal("6.00"), ingrediente.getCustoUnitario());
        verify(ingredienteRepository, times(1)).save(ingrediente);

        // Deve registrar movimentação de entrada no estoque
        verify(movimentacaoRepository, times(1)).save(argThat(m ->
            m.getTipo() == TipoMovimentacao.ENTRADA &&
            m.getMotivo() == MotivoMovimentacao.COMPRA &&
            m.getQuantidade().compareTo(new BigDecimal("20")) == 0 &&
            m.getCustoUnitario().compareTo(new BigDecimal("6.00")) == 0
        ));

        // Deve registrar histórico de preço
        verify(historicoRepository, times(1)).save(argThat(h ->
            h.getPreco().compareTo(new BigDecimal("6.00")) == 0 &&
            h.getFornecedor().equals(fornecedor) &&
            h.getIngrediente().equals(ingrediente)
        ));
    }
}
