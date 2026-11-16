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
@Table(name = "prato")
public class Prato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    @Column(name = "nome", nullable = false, length = 140)
    private String nome;
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Column(name = "foto_url", length = 1000)
    private String fotoUrl;
    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;
    @Column(name = "tempo_preparo_min", nullable = false)
    private Integer tempoPreparoMin;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPrato status;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}


