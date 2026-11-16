package br.com.bomefit.comandadigital.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import br.com.bomefit.comandadigital.model.enums.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "historico_preco_fornecedor")
public class HistoricoPrecoFornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;
    @Column(name = "preco", nullable = false, precision = 12, scale = 4)
    private BigDecimal preco;
    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_venda", nullable = false, length = 8)
    private UnidadeEstoque unidadeVenda;
    @Column(name = "registrado_em", nullable = false, insertable = false, updatable = false)
    private LocalDateTime registradoEm;
}

