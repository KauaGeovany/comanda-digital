package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.estoque.IngredienteRequest;
import br.com.bomefit.comandadigital.dto.estoque.IngredienteResponse;
import br.com.bomefit.comandadigital.model.entity.Ingrediente;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import br.com.bomefit.comandadigital.repository.EstoqueMovimentacaoRepository;
import br.com.bomefit.comandadigital.repository.IngredienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IngredienteService {
    private final IngredienteRepository ingredienteRepository;
    private final EstoqueMovimentacaoRepository movimentacaoRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository, EstoqueMovimentacaoRepository movimentacaoRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public Page<IngredienteResponse> listar(Pageable pageable) { return ingredienteRepository.findAll(pageable).map(this::response); }

    @Transactional
    public IngredienteResponse criar(IngredienteRequest request) {
        if (ingredienteRepository.existsBySkuIgnoreCase(request.sku().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado.");
        Ingrediente ingrediente = new Ingrediente();
        aplicar(ingrediente, request);
        ingrediente.setStatus(StatusRegistro.ATIVO);
        return response(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public IngredienteResponse atualizar(Long id, IngredienteRequest request) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        ingredienteRepository.findBySkuIgnoreCase(request.sku().trim())
            .filter(existente -> !existente.getId().equals(id))
            .ifPresent(existente -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado."); });
        if (ingrediente.getUnidadePadrao() != request.unidadePadrao() && movimentacaoRepository.existsByIngrediente_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível mudar a unidade de um ingrediente com movimentações registradas.");
        }
        aplicar(ingrediente, request);
        return response(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public void desativar(Long id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado."));
        ingrediente.setStatus(StatusRegistro.INATIVO);
        ingredienteRepository.save(ingrediente);
    }

    private void aplicar(Ingrediente i, IngredienteRequest r) {
        i.setNome(r.nome().trim());
        i.setSku(r.sku().trim().toUpperCase());
        i.setUnidadePadrao(r.unidadePadrao());
        i.setEstoqueMinimo(r.estoqueMinimo());
        i.setCustoUnitario(r.custoUnitario());
    }

    private IngredienteResponse response(Ingrediente i) {
        var saldo = movimentacaoRepository.saldoAtual(i.getId());
        if (saldo == null) saldo = java.math.BigDecimal.ZERO;
        return new IngredienteResponse(i.getId(), i.getNome(), i.getSku(), i.getUnidadePadrao().name(),
            i.getEstoqueMinimo(), i.getCustoUnitario(), saldo, i.getStatus().name());
    }
}
