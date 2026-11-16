package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.pedido.PedidoResponse;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoItemRepository itemRepository;

    @Mock
    private PedidoStatusHistoricoRepository historicoRepository;

    @Mock
    private PratoRepository pratoRepository;

    @Mock
    private FichaTecnicaRepository fichaRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario cliente;
    private Usuario cozinheiro;
    private Usuario gerente;
    private Usuario admin;
    private Pedido pedido;
    private Prato prato;
    private Ingrediente ingrediente;
    private FichaTecnica ficha;

    @BeforeEach
    void setup() {
        cliente = new Usuario();
        cliente.setId(10L);
        cliente.setNome("Cliente Teste");
        cliente.setPerfil(PerfilUsuario.CLIENTE);

        cozinheiro = new Usuario();
        cozinheiro.setId(20L);
        cozinheiro.setNome("Cozinheiro Chefe");
        cozinheiro.setPerfil(PerfilUsuario.COZINHEIRO);

        gerente = new Usuario();
        gerente.setId(30L);
        gerente.setNome("Gerente Operações");
        gerente.setPerfil(PerfilUsuario.GERENTE);

        admin = new Usuario();
        admin.setId(40L);
        admin.setNome("Admin Sistema");
        admin.setPerfil(PerfilUsuario.ADMIN);

        prato = new Prato();
        prato.setId(1L);
        prato.setNome("Prato Saudável");
        prato.setPrecoVenda(new BigDecimal("35.00"));

        ingrediente = new Ingrediente();
        ingrediente.setId(100L);
        ingrediente.setNome("Arroz Integral");
        ingrediente.setUnidadePadrao(UnidadeEstoque.G);
        ingrediente.setCustoUnitario(new BigDecimal("0.01"));

        ficha = new FichaTecnica();
        ficha.setId(50L);
        ficha.setPrato(prato);
        ficha.setRendimento(1);
        ficha.setItens(new ArrayList<>());

        FichaTecnicaItem fichaItem = new FichaTecnicaItem();
        fichaItem.setIngrediente(ingrediente);
        fichaItem.setQuantidade(new BigDecimal("150"));
        fichaItem.setUnidade(UnidadeReceita.G);
        fichaItem.setFatorCorrecao(BigDecimal.ONE);
        ficha.getItens().add(fichaItem);

        pedido = new Pedido();
        pedido.setId(500L);
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setStatusPagamento(StatusPagamento.PAGO_SIMULADO);
        pedido.setValorTotal(new BigDecimal("35.00"));
        pedido.setItens(new ArrayList<>());

        PedidoItem item = new PedidoItem();
        item.setPedido(pedido);
        item.setPrato(prato);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("35.00"));
        pedido.getItens().add(item);
    }

    @Test
    @DisplayName("RN03: Ao confirmar pedido sem estoque suficiente, deve lançar 422 Unprocessable Entity")
    void testRN03_SemEstoque_Lanca422() {
        when(pedidoRepository.buscarPorIdComBloqueio(500L)).thenReturn(Optional.of(pedido));
        when(fichaRepository.findByPratoId(1L)).thenReturn(Optional.of(ficha));
        when(ingredienteRepository.buscarPorIdComBloqueio(100L)).thenReturn(Optional.of(ingrediente));
        // Saldo disponível: 100g, mas necessário: 150g
        when(movimentacaoRepository.saldoAtual(100L)).thenReturn(new BigDecimal("100.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            pedidoService.alterarStatus(500L, StatusPedido.CONFIRMADO, gerente)
        );

        assertEquals(422, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Estoque insuficiente para Arroz Integral"));
    }

    @Test
    @DisplayName("RN04: Cozinheiro NÃO pode cancelar pedidos (lança 403 Forbidden)")
    void testRN04_CozinheiroNaoPodeCancelar() {
        when(pedidoRepository.buscarPorIdComBloqueio(500L)).thenReturn(Optional.of(pedido));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            pedidoService.cancelar(500L, "Desistência", cozinheiro)
        );

        assertEquals(403, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Cozinheiros não podem cancelar pedidos"));
    }

    @Test
    @DisplayName("RN04: Cliente não pode cancelar pedido após início do preparo (lança 403)")
    void testRN04_ClienteNaoPodeCancelarAposEmPreparo() {
        pedido.setStatus(StatusPedido.EM_PREPARO);
        when(pedidoRepository.buscarPorIdComBloqueio(500L)).thenReturn(Optional.of(pedido));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            pedidoService.cancelar(500L, "Demorou demais", cliente)
        );

        assertEquals(403, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("somente gerência ou administração podem cancelar"));
    }

    @Test
    @DisplayName("RN04: Gerente pode cancelar pedido após início do preparo")
    void testRN04_GerentePodeCancelarAposEmPreparo() {
        pedido.setStatus(StatusPedido.EM_PREPARO);
        when(pedidoRepository.buscarPorIdComBloqueio(500L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(movimentacaoRepository.findAllByPedido_Id(500L)).thenReturn(List.of());

        PedidoResponse response = pedidoService.cancelar(500L, "Problema operacional", gerente);

        assertEquals("CANCELADO", response.status());
        assertEquals("Problema operacional", pedido.getMotivoCancelamento());
    }

    @Test
    @DisplayName("RF-018: Cancelamento de pedido que já deu baixa no estoque deve gerar ESTORNO")
    void testRF018_CancelamentoDePedidoConfirmado_GeraEstorno() {
        pedido.setStatus(StatusPedido.CONFIRMADO);
        when(pedidoRepository.buscarPorIdComBloqueio(500L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        EstoqueMovimentacao saidaOriginal = new EstoqueMovimentacao();
        saidaOriginal.setIngrediente(ingrediente);
        saidaOriginal.setTipo(TipoMovimentacao.SAIDA);
        saidaOriginal.setMotivo(MotivoMovimentacao.VENDA);
        saidaOriginal.setQuantidade(new BigDecimal("150.000"));
        saidaOriginal.setCustoUnitario(new BigDecimal("0.01"));

        when(movimentacaoRepository.findAllByPedido_Id(500L)).thenReturn(List.of(saidaOriginal));

        pedidoService.cancelar(500L, "Cliente cancelou antes do preparo", cliente);

        // Deve registrar movimentação de ESTORNO
        verify(movimentacaoRepository, times(1)).save(argThat(m ->
            m.getTipo() == TipoMovimentacao.ESTORNO &&
            m.getMotivo() == MotivoMovimentacao.ESTORNO &&
            m.getIngrediente().equals(ingrediente)
        ));
    }
}
