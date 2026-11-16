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
@Table(name = "estoque_movimentacao")
public class EstoqueMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 12)
    private TipoMovimentacao tipo;
    @Column(name = "quantidade", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;
    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 24)
    private MotivoMovimentacao motivo;
    @Column(name = "lote", length = 100)
    private String lote;
    @Column(name = "validade")
    private LocalDate validade;
    @Column(name = "custo_unitario", nullable = false, precision = 12, scale = 4)
    private BigDecimal custoUnitario;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "pedido_compra_id", nullable = true)
    private PedidoCompra pedidoCompra;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "pedido_id", nullable = true)
    private Pedido pedido;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

