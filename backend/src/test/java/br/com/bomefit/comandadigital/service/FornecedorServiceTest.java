package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.fornecedor.FornecedorRequest;
import br.com.bomefit.comandadigital.repository.FornecedorProdutoRepository;
import br.com.bomefit.comandadigital.repository.FornecedorRepository;
import br.com.bomefit.comandadigital.repository.HistoricoPrecoFornecedorRepository;
import br.com.bomefit.comandadigital.repository.IngredienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private FornecedorProdutoRepository fornecedorProdutoRepository;

    @Mock
    private HistoricoPrecoFornecedorRepository historicoPrecoRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    @Test
    @DisplayName("RN07: Deve validar CNPJ válido com formato e dígitos verificadores corretos")
    void testRN07_CnpjValido() {
        // CNPJ válido de teste (ex: 11.222.333/0001-81)
        assertTrue(FornecedorService.cnpjValido("11222333000181"));
        // CNPJ da Receita Federal (ex: 00.000.000/0001-91 - Banco do Brasil)
        assertTrue(FornecedorService.cnpjValido("00000000000191"));
    }

    @Test
    @DisplayName("RN07: Deve rejeitar CNPJ com dígitos repetidos")
    void testRN07_CnpjInvalido_DigitosRepetidos() {
        assertFalse(FornecedorService.cnpjValido("11111111111111"));
        assertFalse(FornecedorService.cnpjValido("00000000000000"));
        assertFalse(FornecedorService.cnpjValido("99999999999999"));
    }

    @Test
    @DisplayName("RN07: Deve rejeitar CNPJ com dígito verificador incorreto")
    void testRN07_CnpjInvalido_DigitoVerificadorErrado() {
        // 11222333000181 tem final 81; vamos mudar para 82
        assertFalse(FornecedorService.cnpjValido("11222333000182"));
    }

    @Test
    @DisplayName("RN07: Deve rejeitar CNPJ nulo ou com tamanho diferente de 14")
    void testRN07_CnpjInvalido_TamanhoOuNulo() {
        assertFalse(FornecedorService.cnpjValido(null));
        assertFalse(FornecedorService.cnpjValido("12345"));
        assertFalse(FornecedorService.cnpjValido("112223330001810"));
    }

    @Test
    @DisplayName("RN07: Ao cadastrar fornecedor com CNPJ inválido, deve lançar 400 Bad Request")
    void testRN07_CadastrarFornecedorComCnpjInvalido_Lanca400() {
        FornecedorRequest request = new FornecedorRequest(
            "Distribuidora Inválida", "11.111.111/1111-11", "11999999999", "contato@invalido.com"
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(request));
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("CNPJ"));
    }
}
