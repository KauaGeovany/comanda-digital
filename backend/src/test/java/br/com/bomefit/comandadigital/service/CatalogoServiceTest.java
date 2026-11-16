package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.catalogo.CustoResponse;
import br.com.bomefit.comandadigital.dto.catalogo.PratoRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private PratoRepository pratoRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private FichaTecnicaRepository fichaRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    private Categoria categoria;
    private Prato prato;
    private FichaTecnica ficha;
    private Ingrediente ingrediente;

    @BeforeEach
    void setup() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Saudáveis");
        categoria.setStatus(StatusRegistro.ATIVO);

        prato = new Prato();
        prato.setId(1L);
        prato.setNome("Prato Teste");
        prato.setCategoria(categoria);
        prato.setPrecoVenda(new BigDecimal("50.00"));
        prato.setStatus(StatusPrato.ATIVO);

        ingrediente = new Ingrediente();
        ingrediente.setId(10L);
        ingrediente.setNome("Frango");
        ingrediente.setUnidadePadrao(UnidadeEstoque.KG);
        ingrediente.setCustoUnitario(new BigDecimal("20.00")); // R$ 20 por KG

        ficha = new FichaTecnica();
        ficha.setId(100L);
        ficha.setPrato(prato);
        ficha.setRendimento(1);
        ficha.setItens(new ArrayList<>());
    }

    @Test
    @DisplayName("RN01: Não deve permitir criar prato já direto como ATIVO sem ficha cadastrada")
    void testRN01_CriarPratoDiretoAtivo_LancaExcecao() {
        PratoRequest request = new PratoRequest(
            1L, "Prato Sem Ficha", "Desc", "url", new BigDecimal("30.00"), 15, StatusPrato.ATIVO
        );
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> catalogoService.criarPrato(request));
        assertEquals(422, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("ficha técnica"));
    }

    @Test
    @DisplayName("RN01: Não deve permitir atualizar status para ATIVO se o prato não possuir ficha técnica")
    void testRN01_AtualizarParaAtivoSemFicha_LancaExcecao() {
        Prato pratoInativo = new Prato();
        pratoInativo.setId(5L);
        pratoInativo.setStatus(StatusPrato.INATIVO);
        pratoInativo.setCategoria(categoria);

        PratoRequest request = new PratoRequest(
            1L, "Prato Inativo", "Desc", "url", new BigDecimal("30.00"), 15, StatusPrato.ATIVO
        );

        when(pratoRepository.findById(5L)).thenReturn(Optional.of(pratoInativo));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(fichaRepository.findByPratoId(5L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> catalogoService.atualizarPrato(5L, request));
        assertEquals(422, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("ficha técnica"));
    }

    @Test
    @DisplayName("RN02: Food Cost <= 30% deve classificar como VERDE")
    void testRN02_FoodCostVerde() {
        // Custo do item: 500g de frango a R$20/KG = R$10,00. Preço venda: R$50,00 -> Food cost = 20% (VERDE)
        FichaTecnicaItem item = new FichaTecnicaItem();
        item.setIngrediente(ingrediente);
        item.setQuantidade(new BigDecimal("500"));
        item.setUnidade(UnidadeReceita.G);
        item.setFatorCorrecao(BigDecimal.ONE);
        ficha.getItens().add(item);

        when(pratoRepository.findById(1L)).thenReturn(Optional.of(prato));
        when(fichaRepository.findByPratoId(1L)).thenReturn(Optional.of(ficha));

        CustoResponse response = catalogoService.custo(1L);

        assertEquals("VERDE", response.faixa());
        assertFalse(response.alerta());
        assertEquals(new BigDecimal("10.00"), response.custoPorPorcao());
        assertEquals(new BigDecimal("20.00"), response.foodCostPercentual());
    }

    @Test
    @DisplayName("RN02: Food Cost entre 31% e 35% deve classificar como AMARELO")
    void testRN02_FoodCostAmarelo() {
        // Custo do item: 800g a R$20/KG = R$16,00. Preço venda: R$50,00 -> Food cost = 32% (AMARELO)
        FichaTecnicaItem item = new FichaTecnicaItem();
        item.setIngrediente(ingrediente);
        item.setQuantidade(new BigDecimal("800"));
        item.setUnidade(UnidadeReceita.G);
        item.setFatorCorrecao(BigDecimal.ONE);
        ficha.getItens().add(item);

        when(pratoRepository.findById(1L)).thenReturn(Optional.of(prato));
        when(fichaRepository.findByPratoId(1L)).thenReturn(Optional.of(ficha));

        CustoResponse response = catalogoService.custo(1L);

        assertEquals("AMARELO", response.faixa());
        assertFalse(response.alerta());
        assertEquals(new BigDecimal("16.00"), response.custoPorPorcao());
        assertEquals(new BigDecimal("32.00"), response.foodCostPercentual());
    }

    @Test
    @DisplayName("RN02: Food Cost > 35% deve classificar como VERMELHO e emitir alerta")
    void testRN02_FoodCostVermelho_ComAlerta() {
        // Custo do item: 1000g a R$20/KG = R$20,00. Preço venda: R$50,00 -> Food cost = 40% (VERMELHO)
        FichaTecnicaItem item = new FichaTecnicaItem();
        item.setIngrediente(ingrediente);
        item.setQuantidade(new BigDecimal("1000"));
        item.setUnidade(UnidadeReceita.G);
        item.setFatorCorrecao(BigDecimal.ONE);
        ficha.getItens().add(item);

        when(pratoRepository.findById(1L)).thenReturn(Optional.of(prato));
        when(fichaRepository.findByPratoId(1L)).thenReturn(Optional.of(ficha));

        CustoResponse response = catalogoService.custo(1L);

        assertEquals("VERMELHO", response.faixa());
        assertTrue(response.alerta());
        assertEquals(new BigDecimal("20.00"), response.custoPorPorcao());
        assertEquals(new BigDecimal("40.00"), response.foodCostPercentual());
    }
}
