# Roteiro Oficial do Vídeo Pitch (5 Minutos) — Comanda Digital

> **Baseado na Seção 9.3 do SRS v3.2 (Critérios de Avaliação do Prof. Thiago Silva)**  
> **Duração Total Obrigatória:** Exatamente 5 minutos (vídeos fora da duração ou sem uma das duas partes perdem pontos na avaliação).  
> **Entrega:** Link do YouTube (pode ser Não Listado). Cada integrante da dupla deve enviar individualmente.

---

## Estrutura do Vídeo

```
[00:00 - 03:30] PARTE 1: Pitch de Vendas com o Sistema Funcionando (3 min e 30 seg)
[03:30 - 05:00] PARTE 2: Código e Deploy em Produção (1 min e 30 seg)
```

---

## PARTE 1: Pitch de Vendas e Demonstração ao Vivo (00:00 a 03:30)
> *Dica do SRS: "Apresentem como se estivessem vendendo pra um dono de dark kitchen: o problema, a solução e o sistema rodando ao vivo. Vendam o produto, não leiam requisitos."*

### 1.1 Introdução e Proposta de Valor (00:00 – 00:30)
* **O que mostrar:** Tela inicial do Bom e Fit (`http://localhost:4200` ou URL pública).
* **Fala sugerida:**
  > "Olá! Se você gerencia uma Dark Kitchen, sabe que operar sem salão exige velocidade máxima no delivery e controle rigoroso de custos para não quebrar. Conheça a **Comanda Digital Bom e Fit**: uma plataforma full-stack que conecta a experiência de compra do cliente diretamente à linha de produção da cozinha, integrando ficha técnica, food cost e baixa automática de estoque em tempo real."

### 1.2 Jornada do Cliente: Cardápio, Carrinho e Pedido (00:30 – 01:30)
* **O que mostrar:**
  1. Navegação no cardápio público sem login, filtrando por categorias (Lanches, Bows, Açaí).
  2. Abrir o detalhe do **Hambúrguer Artesanal**, mostrar descrição, tempo de preparo (20 min) e adicionar à sacola com observação *(ex: "sem cebola")*.
  3. Ir para o carrinho, conferir subtotal e total.
  4. Realizar cadastro ou login rápido (`cliente@email.com`).
  5. Confirmar endereço de entrega e finalizar pedido com pagamento simulado.
  6. Mostrar a tela **Meus Pedidos** com a timeline de status (`RECEBIDO`).
* **Fala sugerida:**
  > "Para o cliente, a experiência é fluida: ele navega pelo cardápio, personaliza itens com observações e faz o checkout em segundos. Assim que o pedido é confirmado, ele já entra direto na esteira de produção da cozinha."

### 1.3 Painel Administrativo, Ficha Técnica e Custos (01:30 – 02:30)
* **O que mostrar:**
  1. Login no painel de gestão com `admin@email.com` / `senha123`.
  2. Acessar **Cardápio**: abrir a **Ficha Técnica** do Hambúrguer Artesanal com rendimento e ingredientes (blend bovino, brioche, cheddar, etc.).
  3. Mostrar o cálculo em tempo real do **Custo por Porção** e a faixa de **Food Cost com cores** (Verde $\le 30\%$, Amarelo $\le 35\%$, Vermelho $> 35\%$).
* **Fala sugerida:**
  > "No coração da gestão está a nossa Ficha Técnica: o sistema calcula o custo exato de cada ingrediente considerando fatores de correção e rendimento. Nosso indicador visual de Food Cost avisa instantaneamente se a margem está no verde ou se passou do limite crítico de 35%."

### 1.4 Cozinha, Pedidos e Baixa Automática no Estoque (02:30 – 03:30)
* **O que mostrar:**
  1. Ir para a aba **Pedidos**: o pedido recém-criado está lá como `RECEBIDO`.
  2. Gerente clica para avançar para `CONFIRMADO`: explicar que houve **baixa automática no estoque via transação atômica**.
  3. Cozinheiro avança para `EM_PREPARO` e depois `PRONTO`.
  4. Ir para a aba **Estoque**: mostrar os saldos dos ingredientes atualizados e as movimentações auditadas.
  5. Voltar ao **Dashboard**: mostrar os KPIs do dia (faturamento, pedidos, ticket médio, food cost) e o gráfico diário com Chart.js.
* **Fala sugerida:**
  > "Na cozinha, ao confirmar o pedido, o sistema executa a baixa transacional de cada grama de ingrediente no estoque. Tudo fica registrado no razão de movimentações. E no Dashboard, o gestor acompanha faturamento, ticket médio e alertas de estoque crítico em tempo real."

---

## PARTE 2: Código e Deploy (03:30 a 05:00)
> *Dica do SRS: "Mostrem o repositório no GitHub: estrutura do front (Angular) e do back (Spring Boot), e expliquem como subiram pra produção (plataformas usadas, variáveis de ambiente, banco gerenciado)."*

### 2.1 Arquitetura e Estrutura do Repositório (03:30 – 04:15)
* **O que mostrar:**
  1. O repositório no GitHub/GitLab ou VS Code.
  2. **Frontend Angular 22:** componentes standalone, guards de rota (`authGuard`, `staffGuard`, `roleGuard`), interceptor JWT (`authInterceptor`), formulários reativos e separação em `environments`.
  3. **Backend Spring Boot 3.5 / Java 17:** arquitetura limpa em camadas:
     * `controller/` com `@PreAuthorize` para os 4 perfis (`CLIENTE`, `ADMIN`, `GERENTE`, `COZINHEIRO`) e documentação Swagger OpenAPI.
     * `service/` com todas as regras de negócio isoladas (RN01 a RN10).
     * `repository/` Spring Data JPA com queries de locking e agregações.
     * `dto/` para isolamento total das entidades.
     * `exception/` com `@RestControllerAdvice` para tratamento global de erros.
  4. **Persistência Flyway:** migrations `V1__create_schema.sql` (15 tabelas em snake_case) e `V2__seed_data.sql`.
* **Fala sugerida:**
  > "Na arquitetura, seguimos rigorosamente a stack do SRS: Frontend em Angular moderno standalone com TypeScript, guards de autenticação RBAC e interceptor JWT. O backend em Spring Boot segue o padrão Controller-Service-Repository com DTOs estritos, segurança com BCrypt e tokens JWT de 8 horas, tratamento global de exceções e schema versionado com Flyway no MySQL."

### 2.2 Deploy em Nuvem e Variáveis de Ambiente (04:15 – 05:00)
* **O que mostrar:**
  1. Explicação dos arquivos `compose.yaml`, `Dockerfile` multi-stage e `deploy/nginx.conf`.
  2. Como as credenciais são isoladas via variáveis de ambiente (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `FRONTEND_URL`) sem segredos no Git.
  3. Plataformas de nuvem usadas para deploy gratuito (ex: Render/Railway para backend Java + MySQL gerenciado, e Vercel/Netlify para frontend Angular).
  4. Encerramento: destacar o link do Swagger (`/swagger-ui.html`) e o README completo.
* **Fala sugerida:**
  > "Para produção, o sistema está publicado e ativo em nuvem com alta disponibilidade: frontend Angular no Cloudflare Pages (comanda-digital-b0u.pages.dev), API REST no Render (comanda-digital-plko.onrender.com) e banco de dados MySQL gerenciado no Railway com migrations automáticas via Flyway. Nenhuma credencial ou segredo fica hardcoded no repositório. O Swagger está ativo em `/swagger-ui.html` para testes da API. Com isso, entregamos uma solução completa, robusta e aderente a 100% dos requisitos do SRS v3.2. Obrigado!"

---

## Checklist de Gravação (Não esquecer!)
- [ ] Duração entre 4min45s e 5min10s (tolerância máxima para não perder pontos).
- [ ] Gravação com áudio limpo (usar OBS Studio, Loom ou Clipchamp).
- [ ] Ambos os integrantes da dupla devem falar no vídeo (conforme item 9.3 do SRS).
- [ ] Sistema deve estar rodando e os dados de seed carregados.
- [ ] Enviar o link do vídeo no YouTube (público ou não listado) e o link do GitHub individualmente no portal acadêmico.
