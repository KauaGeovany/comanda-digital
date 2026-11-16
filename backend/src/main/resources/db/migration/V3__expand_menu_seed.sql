-- V3__expand_menu_seed.sql: Adiciona mais opções para garantir 5 pratos em cada categoria

-- 1. Novos Pratos (IDs 7 a 25)
INSERT INTO prato (id, categoria_id, nome, descricao, foto_url, preco_venda, tempo_preparo_min, status) VALUES
-- Categoria 1: Lanches (já tinha #1)
(7, 1, 'Burger de Frango Crispy', 'Peito de frango grelhado, alface americana, tomate fresco e molho da casa no pão brioche.', 'https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1000&q=85', 34.90, 15, 'ATIVO'),
(8, 1, 'Smash Burger Duplo Cheddar', 'Dois smash burgers bovinos prensados, dobro de queijo cheddar derretido e pão brioche tostado.', 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=1000&q=85', 36.90, 15, 'ATIVO'),
(9, 1, 'Burger Vegetariano de Grão-de-Bico', 'Hambúrguer artesanal de grão-de-bico com especiarias, alface, cenoura ralada e molho verde.', 'https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=1000&q=85', 31.90, 18, 'ATIVO'),
(10, 1, 'Sanduíche Leve de Frango Desfiado', 'Peito de frango desfiado com cenoura, folhas frescas e creme suave em wrap leve.', 'https://images.unsplash.com/photo-1509722747041-616f39b57569?auto=format&fit=crop&w=1000&q=85', 24.90, 10, 'ATIVO'),

-- Categoria 2: Bowls (já tinha #2 e #6)
(11, 2, 'Bowl Proteico de Carne', 'Tiras de carne grelhadas com arroz integral, brócolis ao vapor e cenoura crocante.', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=1000&q=85', 38.90, 20, 'ATIVO'),
(12, 2, 'Bowl Vegano Primavera', 'Mix de grão-de-bico temperado, arroz integral, brócolis, cenoura laminada e abacate.', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=1000&q=85', 28.90, 15, 'ATIVO'),
(13, 2, 'Bowl Fit Frango & Brócolis', 'Clássico dos treinos: peito de frango grelhado com brócolis frescos e molho suave da casa.', 'https://images.unsplash.com/photo-1543339308-43e59d6b73a6?auto=format&fit=crop&w=1000&q=85', 31.90, 16, 'ATIVO'),

-- Categoria 3: Wraps (já tinha #3)
(14, 3, 'Wrap de Frango Caesar', 'Tortilha recheada com peito de frango, alface crocante, cheddar suave e molho especial.', 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=1000&q=85', 28.90, 12, 'ATIVO'),
(15, 3, 'Wrap de Carne & Cheddar', 'Carne bovina macia em tiras com cheddar cremoso e tomate na tortilha tostada.', 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=1000&q=85', 32.90, 15, 'ATIVO'),
(16, 3, 'Wrap Verde Detox', 'Tortilha integral recheada com espinafre fresco, cenoura ralada, abacate e grão-de-bico.', 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?auto=format&fit=crop&w=1000&q=85', 25.90, 10, 'ATIVO'),
(17, 3, 'Wrap Mediterrâneo', 'Tiras de frango grelhado, tomate picadinho, espinafre baby e toque suave de limão.', 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=1000&q=85', 27.90, 12, 'ATIVO'),

-- Categoria 4: Açaí (já tinha #4)
(18, 4, 'Açaí Turbinado Proteico', 'Açaí cremoso batido, coberto com banana fatiada, mel silvestre e dose extra de granola.', 'https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=1000&q=85', 29.90, 8, 'ATIVO'),
(19, 4, 'Açaí Tropical com Maçã e Mel', 'Tigela de açaí com rodelas de banana, cubos de maçã fresca e fios de mel.', 'https://images.unsplash.com/photo-1582293041079-7814c2f12063?auto=format&fit=crop&w=1000&q=85', 26.90, 8, 'ATIVO'),
(20, 4, 'Açaí Puro da Amazônia', 'O autêntico açaí sem xaropes artificiais, acompanhado de granola crocante tradicional.', 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?auto=format&fit=crop&w=1000&q=85', 25.90, 6, 'ATIVO'),
(21, 4, 'Açaí Bowl Clássico com Mel', 'Açaí na tigela com banana geladinha, mel de abelha e granola leve.', 'https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=1000&q=85', 23.90, 6, 'ATIVO'),

-- Categoria 5: Bebidas (já tinha #5)
(22, 5, 'Suco de Maçã & Limão', 'Combinação leve e revigorante de maçã fresca com suco de limão e toque de mel.', 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=1000&q=85', 13.90, 5, 'ATIVO'),
(23, 5, 'Limonada Suíça com Mel', 'Limões frescos batidos com água gelada e adoçados com mel puro silvestre.', 'https://images.unsplash.com/photo-1523371067109-8a39c941dfd1?auto=format&fit=crop&w=1000&q=85', 12.90, 5, 'ATIVO'),
(24, 5, 'Suco Detox de Espinafre e Maçã', 'Poderoso antioxidante com folhas frescas de espinafre, maçã e limão espremido na hora.', 'https://images.unsplash.com/photo-1610970881699-44a5587cabec?auto=format&fit=crop&w=1000&q=85', 15.90, 6, 'ATIVO'),
(25, 5, 'Shake de Banana e Mel', 'Bebida cremosa e nutritiva de banana fresca batida com mel silvestre puro.', 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=1000&q=85', 16.90, 6, 'ATIVO')
ON DUPLICATE KEY UPDATE nome = VALUES(nome), preco_venda = VALUES(preco_venda);

-- 2. Fichas Técnicas (IDs 7 a 25)
INSERT INTO ficha_tecnica (id, prato_id, rendimento, modo_preparo) VALUES
(7, 7, 1, 'Grelhe o frango. Toste o pão brioche e monte com alface, tomate e molho.'),
(8, 8, 1, 'Pressione as carnes na chapa quente (smash). Derreta o cheddar e monte no pão tostado.'),
(9, 9, 1, 'Grelhe o hambúrguer de grão-de-bico, adicione cenoura, alface e molho verde no brioche.'),
(10, 10, 1, 'Desfie o frango cozido, misture com cenoura e espinafre, recheie a tortilha e dobre.'),
(11, 11, 1, 'Grelhe a carne fatiada, cozinhe o arroz, o brócolis e a cenoura ao vapor. Monte no bowl.'),
(12, 12, 1, 'No bowl, monte o arroz integral com grão-de-bico, brócolis, cenoura e fatias de abacate.'),
(13, 13, 1, 'Grelhe cubos de frango com brócolis ao vapor, arroz e finalize com o molho da casa.'),
(14, 14, 1, 'Aqueça a tortilha, adicione frango em tiras, alface fresca, cheddar e molho especial.'),
(15, 15, 1, 'Grelhe a carne fatiada com cheddar derretido, tomate picado e enrole na tortilha.'),
(16, 16, 1, 'Recheie a tortilha com espinafre, abacate em fatias, grão-de-bico e cenoura ralada.'),
(17, 17, 1, 'Toste o wrap com frango grelhado, espinafre, tomate fresco e algumas gotas de limão.'),
(18, 18, 1, 'Bata o açaí gelado, adicione banana fatiada, dose farta de granola e finalize com mel.'),
(19, 19, 1, 'Sirva o açaí batido na tigela com rodelas de banana, cubos de maçã e fios de mel.'),
(20, 20, 1, 'Sirva o açaí puro da Amazônia tigela acompanhado de granola crocante.'),
(21, 21, 1, 'Bata o açaí, monte com banana fatiada e finalize com mel silvestre e granola.'),
(22, 22, 1, 'Processe a maçã com suco de limão, adicione mel e sirva com gelo.'),
(23, 23, 1, 'Bata o limão com água mineral e mel silvestre; coe e sirva imediatamente.'),
(24, 24, 1, 'Processe o espinafre higienizado com maçã, suco de limão e gelo.'),
(25, 25, 1, 'Bata as bananas maduras com água e mel puro até atingir textura cremosa.')
ON DUPLICATE KEY UPDATE rendimento = VALUES(rendimento), modo_preparo = VALUES(modo_preparo);

-- 3. Itens das Fichas Técnicas
INSERT INTO ficha_tecnica_item (ficha_tecnica_id, ingrediente_id, quantidade, unidade, fator_correcao) VALUES
-- Prato 7 (Burger Frango Crispy)
(7, 2, 1, 'UN', 1.000), (7, 7, 150, 'G', 1.000), (7, 4, 20, 'G', 1.200), (7, 5, 30, 'G', 1.100), (7, 6, 20, 'ML', 1.000),
-- Prato 8 (Smash Burger Duplo)
(8, 2, 1, 'UN', 1.000), (8, 1, 160, 'G', 1.000), (8, 3, 60, 'G', 1.000), (8, 6, 15, 'ML', 1.000),
-- Prato 9 (Burger Grão-de-Bico)
(9, 2, 1, 'UN', 1.000), (9, 11, 150, 'G', 1.000), (9, 12, 30, 'G', 1.200), (9, 4, 25, 'G', 1.300), (9, 6, 15, 'ML', 1.000),
-- Prato 10 (Sanduíche Leve Frango)
(10, 10, 1, 'UN', 1.000), (10, 7, 100, 'G', 1.000), (10, 12, 30, 'G', 1.100), (10, 17, 20, 'G', 1.200),
-- Prato 11 (Bowl Proteico Carne)
(11, 1, 150, 'G', 1.000), (11, 8, 120, 'G', 1.000), (11, 9, 80, 'G', 1.150), (11, 12, 40, 'G', 1.200),
-- Prato 12 (Bowl Vegano Primavera)
(12, 11, 120, 'G', 1.000), (12, 8, 100, 'G', 1.000), (12, 20, 60, 'G', 1.100), (12, 9, 50, 'G', 1.200), (12, 12, 30, 'G', 1.100),
-- Prato 13 (Bowl Fit Frango & Brócolis)
(13, 7, 160, 'G', 1.000), (13, 9, 120, 'G', 1.150), (13, 8, 80, 'G', 1.000), (13, 6, 20, 'ML', 1.000),
-- Prato 14 (Wrap Frango Caesar)
(14, 10, 1, 'UN', 1.000), (14, 7, 120, 'G', 1.000), (14, 4, 30, 'G', 1.300), (14, 3, 20, 'G', 1.000), (14, 6, 20, 'ML', 1.000),
-- Prato 15 (Wrap Carne & Cheddar)
(15, 10, 1, 'UN', 1.000), (15, 1, 120, 'G', 1.000), (15, 3, 30, 'G', 1.000), (15, 5, 40, 'G', 1.200), (15, 6, 15, 'ML', 1.000),
-- Prato 16 (Wrap Verde Detox)
(16, 10, 1, 'UN', 1.000), (16, 17, 30, 'G', 1.200), (16, 20, 50, 'G', 1.100), (16, 11, 60, 'G', 1.000), (16, 12, 30, 'G', 1.100),
-- Prato 17 (Wrap Mediterrâneo)
(17, 10, 1, 'UN', 1.000), (17, 7, 110, 'G', 1.000), (17, 5, 40, 'G', 1.200), (17, 17, 20, 'G', 1.200), (17, 19, 10, 'ML', 1.000),
-- Prato 18 (Açaí Turbinado Proteico)
(18, 13, 350, 'G', 1.000), (18, 14, 50, 'G', 1.000), (18, 15, 80, 'G', 1.000), (18, 16, 20, 'ML', 1.000),
-- Prato 19 (Açaí Tropical Maçã e Mel)
(19, 13, 300, 'G', 1.000), (19, 15, 60, 'G', 1.000), (19, 18, 50, 'G', 1.200), (19, 16, 15, 'ML', 1.000),
-- Prato 20 (Açaí Puro da Amazônia)
(20, 13, 300, 'G', 1.000), (20, 14, 40, 'G', 1.000),
-- Prato 21 (Açaí Bowl Clássico)
(21, 13, 250, 'G', 1.000), (21, 15, 50, 'G', 1.000), (21, 14, 30, 'G', 1.000), (21, 16, 10, 'ML', 1.000),
-- Prato 22 (Suco Maçã & Limão)
(22, 18, 150, 'G', 1.250), (22, 19, 30, 'ML', 1.000), (22, 16, 10, 'ML', 1.000),
-- Prato 23 (Limonada Suíça)
(23, 19, 50, 'ML', 1.000), (23, 16, 20, 'ML', 1.000),
-- Prato 24 (Suco Detox Espinafre)
(24, 17, 50, 'G', 1.200), (24, 18, 120, 'G', 1.200), (24, 19, 20, 'ML', 1.000),
-- Prato 25 (Shake Banana & Mel)
(25, 15, 120, 'G', 1.000), (25, 16, 25, 'ML', 1.000)
ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade);

-- 4. Reforço de Estoque Inicial para os novos pratos
INSERT INTO estoque_movimentacao (ingrediente_id, tipo, quantidade, motivo, lote, validade, custo_unitario, usuario_id) VALUES
(1, 'ENTRADA', 10000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0450, 1),
(2, 'ENTRADA', 200, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 2.8000, 1),
(7, 'ENTRADA', 10000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 120 DAY), 0.0450, 1),
(8, 'ENTRADA', 10000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 240 DAY), 0.0180, 1),
(9, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0220, 1),
(10, 'ENTRADA', 300, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 60 DAY), 3.2000, 1),
(11, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0180, 1),
(13, 'ENTRADA', 15000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 0.0350, 1),
(14, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0450, 1),
(15, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 0.0060, 1),
(16, 'ENTRADA', 2000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0.0600, 1),
(17, 'ENTRADA', 2000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 0.0200, 1),
(18, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0120, 1),
(19, 'ENTRADA', 2000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0.0200, 1),
(20, 'ENTRADA', 5000, 'AJUSTE', 'EXPANSAO-2026', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 0.0180, 1);
