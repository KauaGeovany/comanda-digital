INSERT INTO usuario (id, nome, email, senha_hash, perfil, telefone, endereco, status)
VALUES (1, 'Administrador', 'admin@email.com', '$2b$10$N3bjM/k/0vvWPM2LWo8qcuUWBpGbYeeY3o/mfxVJn1vnE/UmtE8x.', 'ADMIN', NULL, NULL, 'ATIVO');

INSERT INTO categoria (id, nome, descricao, ordem, status) VALUES
(1, 'Lanches', 'Hambúrgueres e opções práticas.', 1, 'ATIVO'),
(2, 'Bowls', 'Refeições completas em bowl.', 2, 'ATIVO'),
(3, 'Wraps', 'Opções leves enroladas.', 3, 'ATIVO'),
(4, 'Açaí', 'Açaí e acompanhamentos.', 4, 'ATIVO'),
(5, 'Bebidas', 'Sucos naturais e bebidas.', 5, 'ATIVO');

INSERT INTO ingrediente (id, nome, sku, unidade_padrao, estoque_minimo, custo_unitario, status) VALUES
(1, 'Blend bovino', 'BLEND-BOVINO', 'G', 1000, 0.0450, 'ATIVO'),
(2, 'Pão brioche', 'PAO-BRIOCHE', 'UN', 20, 2.8000, 'ATIVO'),
(3, 'Queijo cheddar', 'QUEIJO-CHEDDAR', 'G', 500, 0.0650, 'ATIVO'),
(4, 'Alface', 'ALFACE', 'G', 500, 0.0120, 'ATIVO'),
(5, 'Tomate', 'TOMATE', 'G', 500, 0.0080, 'ATIVO'),
(6, 'Molho especial', 'MOLHO-ESPECIAL', 'ML', 500, 0.0320, 'ATIVO'),
(7, 'Peito de frango', 'FRANGO', 'G', 1000, 0.0450, 'ATIVO'),
(8, 'Arroz integral', 'ARROZ-INTEGRAL', 'G', 1000, 0.0180, 'ATIVO'),
(9, 'Brócolis', 'BROCOLIS', 'G', 500, 0.0220, 'ATIVO'),
(10, 'Tortilha integral', 'TORTILHA-INTEGRAL', 'UN', 20, 3.2000, 'ATIVO'),
(11, 'Grão-de-bico', 'GRAO-DE-BICO', 'G', 500, 0.0180, 'ATIVO'),
(12, 'Cenoura', 'CENOURA', 'G', 500, 0.0080, 'ATIVO'),
(13, 'Polpa de açaí', 'POLPA-ACAI', 'G', 1000, 0.0350, 'ATIVO'),
(14, 'Granola', 'GRANOLA', 'G', 500, 0.0450, 'ATIVO'),
(15, 'Banana', 'BANANA', 'G', 500, 0.0060, 'ATIVO'),
(16, 'Mel', 'MEL', 'ML', 250, 0.0600, 'ATIVO'),
(17, 'Espinafre', 'ESPINAFRE', 'G', 250, 0.0200, 'ATIVO'),
(18, 'Maçã', 'MACA', 'G', 500, 0.0120, 'ATIVO'),
(19, 'Limão', 'LIMAO', 'ML', 250, 0.0200, 'ATIVO'),
(20, 'Abacate', 'ABACATE', 'G', 500, 0.0180, 'ATIVO');

INSERT INTO prato (id, categoria_id, nome, descricao, foto_url, preco_venda, tempo_preparo_min, status) VALUES
(1, 1, 'Hambúrguer Artesanal', 'Blend bovino, brioche, cheddar, salada fresca e molho especial.', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=1000&q=85', 39.90, 20, 'ATIVO'),
(2, 2, 'Bowl de Frango', 'Frango grelhado com arroz integral, brócolis e molho da casa.', 'https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1000&q=85', 32.90, 18, 'ATIVO'),
(3, 3, 'Wrap Veggie', 'Tortilha integral recheada com grão-de-bico, cenoura e folhas.', 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?auto=format&fit=crop&w=1000&q=85', 26.90, 15, 'ATIVO'),
(4, 4, 'Açaí Energia', 'Açaí batido com banana, granola crocante e um toque de mel.', 'https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=1000&q=85', 24.90, 10, 'ATIVO'),
(5, 5, 'Suco Verde', 'Suco natural de maçã, espinafre e limão, preparado na hora.', 'https://images.unsplash.com/photo-1610970881699-44a5587cabec?auto=format&fit=crop&w=1000&q=85', 14.90, 8, 'ATIVO'),
(6, 2, 'Bowl Tropical de Abacate', 'Bowl cremoso de abacate com folhas e grãos.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=1000&q=85', 29.90, 12, 'ATIVO');

INSERT INTO ficha_tecnica (id, prato_id, rendimento, modo_preparo) VALUES
(1, 1, 1, 'Grelhe o blend ao ponto desejado. Monte no brioche tostado com cheddar, folhas, tomate e molho.'),
(2, 2, 1, 'Grelhe o frango, cozinhe o arroz e o brócolis. Sirva no bowl com o molho.'),
(3, 3, 1, 'Aqueça a tortilha, distribua o recheio e enrole.'),
(4, 4, 1, 'Bata o açaí, sirva com banana e finalize com granola e mel.'),
(5, 5, 1, 'Higienize os ingredientes, processe e sirva imediatamente.'),
(6, 6, 1, 'Monte o bowl com abacate, arroz, folhas e molho.');

INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
(1, 1, 180, 'G', 1.000), (1, 2, 1, 'UN', 1.000), (1, 3, 40, 'G', 1.000), (1, 4, 30, 'G', 1.400), (1, 5, 50, 'G', 1.250), (1, 6, 25, 'ML', 1.000),
(2, 7, 150, 'G', 1.000), (2, 8, 120, 'G', 1.000), (2, 9, 80, 'G', 1.100), (2, 6, 20, 'ML', 1.000),
(3, 10, 1, 'UN', 1.000), (3, 11, 100, 'G', 1.000), (3, 12, 40, 'G', 1.000), (3, 4, 25, 'G', 1.250), (3, 6, 15, 'ML', 1.000),
(4, 13, 300, 'G', 1.000), (4, 14, 30, 'G', 1.000), (4, 15, 60, 'G', 1.000), (4, 16, 10, 'ML', 1.000),
(5, 17, 30, 'G', 1.000), (5, 18, 100, 'G', 1.000), (5, 19, 30, 'ML', 1.000),
(6, 20, 150, 'G', 1.000), (6, 8, 100, 'G', 1.000), (6, 4, 30, 'G', 1.200), (6, 6, 15, 'ML', 1.000);

INSERT INTO estoque_movimentacao (ingrediente_id, tipo, quantidade, motivo, lote, validade, custo_unitario, usuario_id) VALUES
(1, 'ENTRADA', 15000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0450, 1),
(2, 'ENTRADA', 300, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 2.8000, 1),
(3, 'ENTRADA', 6000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 120 DAY), 0.0650, 1),
(4, 'ENTRADA', 4000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 0.0120, 1),
(5, 'ENTRADA', 4000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 0.0080, 1),
(6, 'ENTRADA', 5000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 0.0320, 1),
(7, 'ENTRADA', 12000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 120 DAY), 0.0450, 1),
(8, 'ENTRADA', 10000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 240 DAY), 0.0180, 1),
(9, 'ENTRADA', 5000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0220, 1),
(10, 'ENTRADA', 300, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 60 DAY), 3.2000, 1),
(11, 'ENTRADA', 5000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0180, 1),
(12, 'ENTRADA', 4000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0080, 1),
(13, 'ENTRADA', 10000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 0.0350, 1),
(14, 'ENTRADA', 4000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0450, 1),
(15, 'ENTRADA', 5000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 0.0060, 1),
(16, 'ENTRADA', 2000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0600, 1),
(17, 'ENTRADA', 2000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 0.0200, 1),
(18, 'ENTRADA', 5000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0120, 1),
(19, 'ENTRADA', 2000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0200, 1),
(20, 'ENTRADA', 6000, 'AJUSTE', 'INICIAL-2026', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 0.0180, 1);
