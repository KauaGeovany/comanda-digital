CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL,
    senha_hash VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    telefone VARCHAR(30),
    endereco VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('ADMIN', 'GERENTE', 'COZINHEIRO', 'CLIENTE')),
    CONSTRAINT ck_usuario_status CHECK (status IN ('ATIVO', 'INATIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(80) NOT NULL,
    descricao VARCHAR(255),
    ordem INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_categoria_nome UNIQUE (nome),
    CONSTRAINT ck_categoria_status CHECK (status IN ('ATIVO', 'INATIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE prato (
    id BIGINT NOT NULL AUTO_INCREMENT,
    categoria_id BIGINT NOT NULL,
    nome VARCHAR(140) NOT NULL,
    descricao TEXT NOT NULL,
    foto_url VARCHAR(1000),
    preco_venda DECIMAL(10,2) NOT NULL,
    tempo_preparo_min INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INATIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_prato_categoria_status (categoria_id, status),
    CONSTRAINT fk_prato_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT ck_prato_preco CHECK (preco_venda >= 0),
    CONSTRAINT ck_prato_tempo CHECK (tempo_preparo_min > 0),
    CONSTRAINT ck_prato_status CHECK (status IN ('ATIVO', 'INATIVO', 'PAUSADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ingrediente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(140) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    unidade_padrao VARCHAR(8) NOT NULL,
    estoque_minimo DECIMAL(12,3) NOT NULL DEFAULT 0,
    custo_unitario DECIMAL(12,4) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_ingrediente_sku UNIQUE (sku),
    CONSTRAINT ck_ingrediente_unidade CHECK (unidade_padrao IN ('G', 'ML', 'UN', 'KG', 'L')),
    CONSTRAINT ck_ingrediente_estoque_minimo CHECK (estoque_minimo >= 0),
    CONSTRAINT ck_ingrediente_custo CHECK (custo_unitario >= 0),
    CONSTRAINT ck_ingrediente_status CHECK (status IN ('ATIVO', 'INATIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ficha_tecnica (
    id BIGINT NOT NULL AUTO_INCREMENT,
    prato_id BIGINT NOT NULL,
    rendimento INT NOT NULL,
    modo_preparo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_ficha_tecnica_prato UNIQUE (prato_id),
    CONSTRAINT fk_ficha_tecnica_prato FOREIGN KEY (prato_id) REFERENCES prato(id),
    CONSTRAINT ck_ficha_rendimento CHECK (rendimento > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ficha_tecnica_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ficha_tecnica_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    unidade VARCHAR(8) NOT NULL,
    fator_correcao DECIMAL(8,3) NOT NULL DEFAULT 1.000,
    PRIMARY KEY (id),
    KEY idx_ficha_item_ingrediente (ingrediente_id),
    CONSTRAINT fk_ficha_item_ficha FOREIGN KEY (ficha_tecnica_id) REFERENCES ficha_tecnica(id),
    CONSTRAINT fk_ficha_item_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT uk_ficha_item UNIQUE (ficha_tecnica_id, ingrediente_id),
    CONSTRAINT ck_ficha_item_quantidade CHECK (quantidade > 0),
    CONSTRAINT ck_ficha_item_unidade CHECK (unidade IN ('G', 'ML', 'UN')),
    CONSTRAINT ck_ficha_item_fator CHECK (fator_correcao >= 1.000)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE fornecedor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    razao_social VARCHAR(180) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    telefone VARCHAR(30),
    email VARCHAR(190),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_fornecedor_cnpj UNIQUE (cnpj),
    CONSTRAINT ck_fornecedor_status CHECK (status IN ('ATIVO', 'INATIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE fornecedor_produto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fornecedor_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    preco DECIMAL(12,4) NOT NULL,
    unidade_venda VARCHAR(8) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_fornecedor_produto_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id),
    CONSTRAINT fk_fornecedor_produto_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT uk_fornecedor_produto UNIQUE (fornecedor_id, ingrediente_id),
    CONSTRAINT ck_fornecedor_produto_preco CHECK (preco >= 0),
    CONSTRAINT ck_fornecedor_produto_unidade CHECK (unidade_venda IN ('G', 'ML', 'UN', 'KG', 'L'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE historico_preco_fornecedor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fornecedor_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    preco DECIMAL(12,4) NOT NULL,
    unidade_venda VARCHAR(8) NOT NULL,
    registrado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_historico_preco_item_data (ingrediente_id, fornecedor_id, registrado_em),
    CONSTRAINT fk_historico_preco_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id),
    CONSTRAINT fk_historico_preco_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT ck_historico_preco CHECK (preco >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pedido_compra (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fornecedor_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    valor_total DECIMAL(12,2) NOT NULL DEFAULT 0,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_pedido_compra_status_data (status, created_at),
    CONSTRAINT fk_pedido_compra_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id),
    CONSTRAINT fk_pedido_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT ck_pedido_compra_status CHECK (status IN ('RASCUNHO', 'ENVIADO', 'RECEBIDO', 'CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pedido_compra_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_compra_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    preco_unitario DECIMAL(12,4) NOT NULL,
    unidade_venda VARCHAR(8) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_compra_item_pedido FOREIGN KEY (pedido_compra_id) REFERENCES pedido_compra(id),
    CONSTRAINT fk_pedido_compra_item_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT ck_pedido_compra_item_quantidade CHECK (quantidade > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cliente_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'RECEBIDO',
    valor_total DECIMAL(12,2) NOT NULL,
    endereco_entrega VARCHAR(500) NOT NULL,
    observacoes VARCHAR(1000),
    status_pagamento VARCHAR(20) NOT NULL DEFAULT 'PAGO_SIMULADO',
    canal VARCHAR(20) NOT NULL DEFAULT 'WEB',
    motivo_cancelamento VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_pedido_status_data (status, created_at),
    KEY idx_pedido_cliente_data (cliente_id, created_at),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES usuario(id),
    CONSTRAINT ck_pedido_status CHECK (status IN ('RECEBIDO', 'CONFIRMADO', 'EM_PREPARO', 'PRONTO', 'SAIU_ENTREGA', 'FINALIZADO', 'CANCELADO')),
    CONSTRAINT ck_pedido_pagamento CHECK (status_pagamento IN ('PAGO_SIMULADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pedido_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    prato_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    observacoes VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_pedido_item_prato FOREIGN KEY (prato_id) REFERENCES prato(id),
    CONSTRAINT ck_pedido_item_quantidade CHECK (quantidade > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pedido_status_historico (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    observacao VARCHAR(500),
    usuario_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_pedido_historico_data (pedido_id, created_at),
    CONSTRAINT fk_pedido_historico_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_pedido_historico_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE estoque_movimentacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ingrediente_id BIGINT NOT NULL,
    tipo VARCHAR(12) NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    motivo VARCHAR(24) NOT NULL,
    lote VARCHAR(100),
    validade DATE,
    custo_unitario DECIMAL(12,4) NOT NULL DEFAULT 0,
    pedido_compra_id BIGINT,
    pedido_id BIGINT,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_movimentacao_ingrediente_data (ingrediente_id, created_at),
    KEY idx_movimentacao_referencia_pedido (pedido_id),
    CONSTRAINT fk_movimentacao_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT fk_movimentacao_compra FOREIGN KEY (pedido_compra_id) REFERENCES pedido_compra(id),
    CONSTRAINT fk_movimentacao_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_movimentacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT ck_movimentacao_tipo CHECK (tipo IN ('ENTRADA', 'SAIDA', 'ESTORNO')),
    CONSTRAINT ck_movimentacao_motivo CHECK (motivo IN ('COMPRA', 'VENDA', 'DESPERDICIO', 'VENCIMENTO', 'QUEBRA', 'USO_INTERNO', 'AJUSTE', 'ESTORNO')),
    CONSTRAINT ck_movimentacao_quantidade CHECK (quantidade > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

