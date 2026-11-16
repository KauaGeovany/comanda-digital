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
@Table(name = "ficha_tecnica")
public class FichaTecnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prato_id", nullable = false)
    private Prato prato;
    @Column(name = "rendimento", nullable = false)
    private Integer rendimento;
    @Column(name = "modo_preparo", columnDefinition = "TEXT")
    private String modoPreparo;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaTecnicaItem> itens = new ArrayList<>();
}

