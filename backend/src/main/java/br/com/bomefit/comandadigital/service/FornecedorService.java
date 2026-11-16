package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.fornecedor.*;
import br.com.bomefit.comandadigital.model.entity.*;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import br.com.bomefit.comandadigital.model.enums.UnidadeEstoque;
import br.com.bomefit.comandadigital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FornecedorService {
    private final FornecedorRepository fornecedorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final FornecedorProdutoRepository produtoRepository;
    private final HistoricoPrecoFornecedorRepository historicoRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository, IngredienteRepository ingredienteRepository,
                             FornecedorProdutoRepository produtoRepository, HistoricoPrecoFornecedorRepository historicoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.produtoRepository = produtoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional(readOnly = true)
    public Page<FornecedorResponse> listar(Pageable pageable) {
        return fornecedorRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        String cnpj = somenteDigitos(request.cnpj());
        if (!cnpjValido(cnpj)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        if (fornecedorRepository.existsByCnpj(cnpj)) throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado.");
        Fornecedor fornecedor = new Fornecedor();
        aplicar(fornecedor, request, cnpj);
        fornecedor.setStatus(StatusRegistro.ATIVO);
        return toResponse(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado."));
        String cnpj = somenteDigitos(request.cnpj());
        if (!cnpjValido(cnpj)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        fornecedorRepository.findByCnpj(cnpj).filter(f -> !f.getId().equals(id))
            .ifPresent(f -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado."); });
        aplicar(fornecedor, request, cnpj);
        return toResponse(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void desativar(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado."));
        fornecedor.setStatus(StatusRegistro.INATIVO);
        fornecedorRepository.save(fornecedor);
    }

    @Transactional
    public CotacaoResponse atualizarCatalogo(Long fornecedorId, CatalogoFornecedorRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
            .filter(f -> f.getStatus() == StatusRegistro.ATIVO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor ativo não encontrado."));
        Ingrediente ingrediente = ingredienteRepository.findById(request.ingredienteId())
            .filter(i -> i.getStatus() == StatusRegistro.ATIVO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente ativo não encontrado."));
        validarUnidades(request.unidadeVenda(), ingrediente.getUnidadePadrao());
        FornecedorProduto produto = produtoRepository.findByFornecedorIdAndIngredienteId(fornecedorId, ingrediente.getId())
            .orElseGet(FornecedorProduto::new);
        produto.setFornecedor(fornecedor);
        produto.setIngrediente(ingrediente);
        produto.setPreco(request.preco());
        produto.setUnidadeVenda(request.unidadeVenda());
        produto = produtoRepository.save(produto);
        salvarHistorico(fornecedor, ingrediente, request.preco(), request.unidadeVenda());
        return quoteResponse(produto);
    }

    @Transactional(readOnly = true)
    public List<CotacaoResponse> cotar(Long ingredienteId) {
        if (!ingredienteRepository.existsById(ingredienteId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado.");
        return produtoRepository.findAllByIngredienteIdOrderByPrecoAsc(ingredienteId).stream()
            .filter(p -> p.getFornecedor().getStatus() == StatusRegistro.ATIVO)
            .map(this::quoteResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<HistoricoPrecoResponse> historico(Long fornecedorId, Long ingredienteId, Pageable pageable) {
        if (!fornecedorRepository.existsById(fornecedorId) || !ingredienteRepository.existsById(ingredienteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor ou ingrediente não encontrado.");
        }
        return historicoRepository.findAllByFornecedor_IdAndIngrediente_IdOrderByRegistradoEmDesc(
            fornecedorId, ingredienteId, pageable).map(h -> new HistoricoPrecoResponse(
                h.getId(), h.getPreco(), h.getUnidadeVenda().name(), h.getRegistradoEm()));
    }

    private void salvarHistorico(Fornecedor f, Ingrediente i, BigDecimal preco, UnidadeEstoque unidade) {
        HistoricoPrecoFornecedor historico = new HistoricoPrecoFornecedor();
        historico.setFornecedor(f);
        historico.setIngrediente(i);
        historico.setPreco(preco);
        historico.setUnidadeVenda(unidade);
        historicoRepository.save(historico);
    }

    private void aplicar(Fornecedor f, FornecedorRequest request, String cnpj) {
        f.setRazaoSocial(request.razaoSocial().trim());
        f.setCnpj(cnpj);
        f.setTelefone(request.telefone());
        f.setEmail(request.email() == null ? null : request.email().trim().toLowerCase());
    }

    private FornecedorResponse toResponse(Fornecedor f) {
        return new FornecedorResponse(f.getId(), f.getRazaoSocial(), f.getCnpj(), f.getTelefone(), f.getEmail(), f.getStatus().name());
    }

    private CotacaoResponse quoteResponse(FornecedorProduto p) {
        return new CotacaoResponse(p.getFornecedor().getId(), p.getFornecedor().getRazaoSocial(), p.getFornecedor().getCnpj(),
            p.getIngrediente().getId(), p.getIngrediente().getNome(), p.getPreco(), p.getUnidadeVenda().name());
    }

    static String somenteDigitos(String value) { return value == null ? "" : value.replaceAll("\\D", ""); }

    static boolean cnpjValido(String cnpj) {
        if (cnpj == null || !cnpj.matches("\\d{14}") || cnpj.chars().distinct().count() == 1) return false;
        int[] weights1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] weights2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        int digit1 = checksum(cnpj, weights1, 12);
        int digit2 = checksum(cnpj, weights2, 13);
        return cnpj.charAt(12) - '0' == digit1 && cnpj.charAt(13) - '0' == digit2;
    }

    private static int checksum(String value, int[] weights, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) sum += (value.charAt(i) - '0') * weights[i];
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private void validarUnidades(UnidadeEstoque venda, UnidadeEstoque estoque) {
        boolean compatible = venda == estoque
            || (venda == UnidadeEstoque.KG && estoque == UnidadeEstoque.G)
            || (venda == UnidadeEstoque.G && estoque == UnidadeEstoque.KG)
            || (venda == UnidadeEstoque.L && estoque == UnidadeEstoque.ML)
            || (venda == UnidadeEstoque.ML && estoque == UnidadeEstoque.L);
        if (!compatible) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unidades incompatíveis entre fornecedor e ingrediente.");
    }
}
