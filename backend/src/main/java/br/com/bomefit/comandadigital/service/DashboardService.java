package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.catalogo.CustoResponse;
import br.com.bomefit.comandadigital.dto.dashboard.DashboardResponse;
import br.com.bomefit.comandadigital.dto.estoque.IngredienteResponse;
import br.com.bomefit.comandadigital.model.enums.StatusPrato;
import br.com.bomefit.comandadigital.repository.PedidoRepository;
import br.com.bomefit.comandadigital.repository.PratoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class DashboardService {
    private static final ZoneId FUSO_NEGOCIO = ZoneId.of("America/Sao_Paulo");

    private final PedidoRepository pedidoRepository;
    private final PratoRepository pratoRepository;
    private final CatalogoService catalogoService;
    private final EstoqueService estoqueService;

    public DashboardService(PedidoRepository pedidoRepository, PratoRepository pratoRepository,
                            CatalogoService catalogoService, EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.pratoRepository = pratoRepository;
        this.catalogoService = catalogoService;
        this.estoqueService = estoqueService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse consultar(LocalDate inicio, LocalDate fim) {
        LocalDate hoje = LocalDate.now(FUSO_NEGOCIO);
        LocalDate dataFim = fim == null ? hoje : fim;
        LocalDate dataInicio = inicio == null ? dataFim.minusDays(29) : inicio;
        if (dataInicio.isAfter(dataFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data inicial deve ser anterior ou igual à data final.");
        }
        if (dataInicio.plusDays(366).isBefore(dataFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período máximo do painel é de 367 dias.");
        }
        var inicioTs = dataInicio.atStartOfDay();
        var fimExclusivo = dataFim.plusDays(1).atStartOfDay();

        Map<LocalDate, DashboardResponse.VendaDiaria> vendasPorData = new HashMap<>();
        for (Object[] linha : pedidoRepository.vendasDiarias(inicioTs, fimExclusivo)) {
            LocalDate data = data(linha[0]);
            vendasPorData.put(data, new DashboardResponse.VendaDiaria(data, numero(linha[1]).longValue(), decimal(linha[2])));
        }
        List<DashboardResponse.VendaDiaria> serie = new ArrayList<>();
        for (LocalDate data = dataInicio; !data.isAfter(dataFim); data = data.plusDays(1)) {
            serie.add(vendasPorData.getOrDefault(data, new DashboardResponse.VendaDiaria(data, 0, BigDecimal.ZERO)));
        }

        List<DashboardResponse.PratoMaisVendido> maisVendidos = pedidoRepository.pratosMaisVendidos(inicioTs, fimExclusivo).stream()
            .map(linha -> new DashboardResponse.PratoMaisVendido(
                numero(linha[0]).longValue(), String.valueOf(linha[1]), numero(linha[2]).longValue(), decimal(linha[3])))
            .toList();

        List<CustoResponse> custos = pratoRepository.findAllByStatus(StatusPrato.ATIVO, Pageable.unpaged()).stream()
            .map(prato -> catalogoService.custo(prato.getId()))
            .toList();
        BigDecimal foodCostMedio = custos.isEmpty() ? BigDecimal.ZERO : custos.stream()
            .map(CustoResponse::foodCostPercentual).reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(custos.size()), 2, RoundingMode.HALF_UP);

        List<IngredienteResponse> alertas = estoqueService.listarSaldos(PageRequest.of(0, 1000), true).getContent();
        var inicioHoje = hoje.atStartOfDay();
        var inicioAmanha = hoje.plusDays(1).atStartOfDay();
        long pedidosHoje = pedidoRepository.contarPedidosNoPeriodo(inicioHoje, inicioAmanha);
        BigDecimal faturamentoHoje = Optional.ofNullable(
            pedidoRepository.faturamentoNoPeriodo(inicioHoje, inicioAmanha)).orElse(BigDecimal.ZERO);
        BigDecimal ticketMedioHoje = pedidosHoje == 0 ? BigDecimal.ZERO : faturamentoHoje
            .divide(BigDecimal.valueOf(pedidosHoje), 2, RoundingMode.HALF_UP);
        return new DashboardResponse(pedidosHoje, faturamentoHoje, ticketMedioHoje,
            dataInicio, dataFim,
            pedidoRepository.countByStatus(br.com.bomefit.comandadigital.model.enums.StatusPedido.EM_PREPARO), foodCostMedio,
            serie, maisVendidos, alertas);
    }

    private static LocalDate data(Object value) {
        if (value instanceof Date sqlDate) return sqlDate.toLocalDate();
        if (value instanceof LocalDate localDate) return localDate;
        return LocalDate.parse(value.toString());
    }

    private static Number numero(Object value) {
        return value instanceof Number number ? number : Long.parseLong(value.toString());
    }

    private static BigDecimal decimal(Object value) {
        if (value instanceof BigDecimal amount) return amount;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(value.toString());
    }
}
