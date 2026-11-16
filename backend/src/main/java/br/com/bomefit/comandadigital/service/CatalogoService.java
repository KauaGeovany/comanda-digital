package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.catalogo.*;
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
import java.util.List;

@Service
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final PratoRepository pratoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final FichaTecnicaRepository fichaRepository;

    public CatalogoService(CategoriaRepository categoriaRepository, PratoRepository pratoRepository,
                           IngredienteRepository ingredienteRepository, FichaTecnicaRepository fichaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.pratoRepository = pratoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.fichaRepository = fichaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> categoriasPublicas() {
        return categoriaRepository.findAllByStatusOrderByOrdemAscNomeAsc(StatusRegistro.ATIVO)
            .stream().map(this::categoriaResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PratoResumoResponse> cardapio(Long categoriaId, Pageable pageable) {
        Page<Prato> pratos = categoriaId == null
            ? pratoRepository.findAllByStatus(StatusPrato.ATIVO, pageable)
            : pratoRepository.findAllByCategoria_IdAndStatus(categoriaId, StatusPrato.ATIVO, pageable);
        return pratos.map(this::pratoResponse);
    }

    @Transactional(readOnly = true)
    public PratoResumoResponse detalhePublico(Long id) {
        return pratoRepository.findByIdAndStatus(id, StatusPrato.ATIVO)
            .map(this::pratoResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(this::categoriaResponse);
    }

    @Transactional
    public CategoriaResponse criarCategoria(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        aplicar(categoria, request);
        categoria.setStatus(StatusRegistro.ATIVO);
        return categoriaResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizarCategoria(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));
        aplicar(categoria, request);
        return categoriaResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void desativarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));
        categoria.setStatus(StatusRegistro.INATIVO);
        categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public Page<PratoResumoResponse> listarPratos(Pageable pageable) {
        return pratoRepository.findAll(pageable).map(this::pratoResponse);
    }

    @Transactional
    public PratoResumoResponse criarPrato(PratoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .filter(c -> c.getStatus() == StatusRegistro.ATIVO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria ativa não encontrada."));
        Prato prato = new Prato();
        prato.setCategoria(categoria);
        aplicar(prato, request);
        if (request.status() == StatusPrato.ATIVO) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cadastre a ficha técnica antes de ativar o prato.");
        }
        return pratoResponse(pratoRepository.save(prato));
    }

    @Transactional
    public PratoResumoResponse atualizarPrato(Long id, PratoRequest request) {
        Prato prato = pratoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .filter(c -> c.getStatus() == StatusRegistro.ATIVO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria ativa não encontrada."));
        prato.setCategoria(categoria);
        aplicar(prato, request);
        if (prato.getStatus() == StatusPrato.ATIVO) validarFichaParaAtivacao(prato.getId());
        return pratoResponse(pratoRepository.save(prato));
    }

    @Transactional
    public void desativarPrato(Long id) {
        Prato prato = pratoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        prato.setStatus(StatusPrato.INATIVO);
        pratoRepository.save(prato);
    }

    @Transactional(readOnly = true)
    public FichaTecnicaResponse obterFicha(Long pratoId) {
        Prato prato = pratoRepository.findById(pratoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        return fichaRepository.findByPratoId(pratoId)
            .map(ficha -> fichaResponse(prato, ficha))
            .orElse(new FichaTecnicaResponse(pratoId, 1, "", List.of()));
    }

    @Transactional
    public FichaTecnicaResponse salvarFicha(Long pratoId, FichaTecnicaRequest request) {
        Prato prato = pratoRepository.findById(pratoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        FichaTecnica ficha = fichaRepository.findByPratoId(pratoId).orElseGet(FichaTecnica::new);
        ficha.setPrato(prato);
        ficha.setRendimento(request.rendimento());
        ficha.setModoPreparo(request.modoPreparo());
        ficha.getItens().clear();
        ficha = fichaRepository.saveAndFlush(ficha);
        for (FichaTecnicaItemRequest itemRequest : request.itens()) {
            Ingrediente ingrediente = ingredienteRepository.findById(itemRequest.ingredienteId())
                .filter(i -> i.getStatus() == StatusRegistro.ATIVO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrediente ativo não encontrado: " + itemRequest.ingredienteId()));
            validarUnidade(ingrediente.getUnidadePadrao(), itemRequest.unidade());
            FichaTecnicaItem item = new FichaTecnicaItem();
            item.setFichaTecnica(ficha);
            item.setIngrediente(ingrediente);
            item.setQuantidade(itemRequest.quantidade());
            item.setUnidade(itemRequest.unidade());
            item.setFatorCorrecao(itemRequest.fatorCorrecao());
            ficha.getItens().add(item);
        }
        ficha = fichaRepository.save(ficha);
        if (prato.getStatus() == StatusPrato.ATIVO) validarFichaParaAtivacao(pratoId);
        return fichaResponse(prato, ficha);
    }

    @Transactional(readOnly = true)
    public CustoResponse custo(Long pratoId) {
        Prato prato = pratoRepository.findById(pratoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        FichaTecnica ficha = fichaRepository.findByPratoId(pratoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prato ainda não possui ficha técnica."));
        if (ficha.getItens().isEmpty()) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Ficha técnica sem ingredientes.");
        BigDecimal total = ficha.getItens().stream().map(this::custoItem).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal porcao = total.divide(BigDecimal.valueOf(ficha.getRendimento()), 4, RoundingMode.HALF_UP);
        BigDecimal foodCost = prato.getPrecoVenda().signum() == 0 ? BigDecimal.ZERO
            : porcao.divide(prato.getPrecoVenda(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        String faixa = foodCost.compareTo(BigDecimal.valueOf(30)) <= 0 ? "VERDE"
            : foodCost.compareTo(BigDecimal.valueOf(35)) <= 0 ? "AMARELO" : "VERMELHO";
        return new CustoResponse(pratoId, porcao.setScale(2, RoundingMode.HALF_UP), prato.getPrecoVenda(), foodCost, faixa, foodCost.compareTo(BigDecimal.valueOf(35)) > 0);
    }

    @Transactional(readOnly = true)
    public List<Ingrediente> ingredientesAtivos() {
        return ingredienteRepository.findAll().stream().filter(i -> i.getStatus() == StatusRegistro.ATIVO).toList();
    }

    private BigDecimal custoItem(FichaTecnicaItem item) {
        BigDecimal amount = quantidadeNaUnidadeDeEstoque(item.getQuantidade(), item.getUnidade(), item.getIngrediente().getUnidadePadrao());
        return amount.multiply(item.getFatorCorrecao()).multiply(item.getIngrediente().getCustoUnitario());
    }

    public static BigDecimal quantidadeNaUnidadeDeEstoque(BigDecimal quantidade, UnidadeReceita receita, UnidadeEstoque estoque) {
        if (receita.name().equals(estoque.name())) return quantidade;
        if (receita == UnidadeReceita.G && estoque == UnidadeEstoque.KG) return quantidade.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        if (receita == UnidadeReceita.ML && estoque == UnidadeEstoque.L) return quantidade.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unidade incompatível: receita em " + receita + " e ingrediente em " + estoque + ".");
    }

    private void validarUnidade(UnidadeEstoque estoque, UnidadeReceita receita) {
        quantidadeNaUnidadeDeEstoque(BigDecimal.ONE, receita, estoque);
    }

    private void validarFichaParaAtivacao(Long pratoId) {
        FichaTecnica ficha = fichaRepository.findByPratoId(pratoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prato ativo precisa de ficha técnica."));
        if (ficha.getItens().isEmpty()) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prato ativo precisa de pelo menos um ingrediente.");
    }

    private void aplicar(Categoria categoria, CategoriaRequest request) {
        categoria.setNome(request.nome().trim());
        categoria.setDescricao(request.descricao());
        categoria.setOrdem(request.ordem() == null ? 0 : request.ordem());
    }

    private void aplicar(Prato prato, PratoRequest request) {
        prato.setNome(request.nome().trim());
        prato.setDescricao(request.descricao().trim());
        prato.setFotoUrl(request.fotoUrl());
        prato.setPrecoVenda(request.precoVenda());
        prato.setTempoPreparoMin(request.tempoPreparoMin());
        prato.setStatus(request.status());
    }

    private CategoriaResponse categoriaResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao(), categoria.getOrdem(), categoria.getStatus().name());
    }

    private PratoResumoResponse pratoResponse(Prato prato) {
        return new PratoResumoResponse(prato.getId(), prato.getNome(), prato.getDescricao(), prato.getFotoUrl(), prato.getPrecoVenda(), prato.getTempoPreparoMin(), prato.getCategoria().getNome(), prato.getStatus().name());
    }

    private FichaTecnicaResponse fichaResponse(Prato prato, FichaTecnica ficha) {
        List<FichaTecnicaResponse.Item> itens = ficha.getItens().stream()
            .map(item -> new FichaTecnicaResponse.Item(item.getIngrediente().getId(), item.getIngrediente().getNome(), item.getQuantidade(), item.getUnidade().name(), item.getFatorCorrecao()))
            .toList();
        return new FichaTecnicaResponse(prato.getId(), ficha.getRendimento(), ficha.getModoPreparo(), itens);
    }
}
