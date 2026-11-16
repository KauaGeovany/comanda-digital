package br.com.bomefit.comandadigital.repository;

import br.com.bomefit.comandadigital.model.entity.PedidoItem;
import br.com.bomefit.comandadigital.model.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {}
