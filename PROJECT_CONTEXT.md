# PROJECT_CONTEXT.md — Bom e Fit / Comanda Digital

## Escopo e fontes

Esta é a nova versão acadêmica da Comanda Digital do Bom e Fit, separada do projeto anterior `darkkitchen` (HTML/CSS/JavaScript com Firebase). A v1 não é dependência e não foi modificada. Os requisitos funcionais e de avaliação são guiados pelo SRS Comanda Digital v3.2 (agosto de 2026); o histórico do Cursor anexado é apenas contexto de origem e decisões anteriores. Nenhum requisito foi presumido como implementado sem conferência no repositório.

## Arquitetura atual

- **Frontend:** Angular 22.2.x, TypeScript, componentes standalone, Angular Router, HttpClient, interceptor JWT, guards, formulários template-driven e ficha técnica com Reactive Forms/FormArray, notificações toast acessíveis, CSS responsivo e Chart.js 4 carregado sob demanda no dashboard. A API local padrão é `http://localhost:8080/api`; no container o Nginx injeta a URL configurável em `public/config.js`.
- **Backend:** Java 17, Spring Boot 3.5.x, Spring MVC, Spring Data JPA/Hibernate, Spring Security stateless, JWT (JJWT), Bean Validation e SpringDoc OpenAPI.
- **Persistência:** MySQL 8.4, esquema snake_case, Flyway (`V1__create_schema.sql`, `V2__seed_data.sql`) e `spring.jpa.hibernate.ddl-auto=validate`.
- **Perfis:** ADMIN, GERENTE, COZINHEIRO e CLIENTE. Senhas BCrypt; endpoints protegidos por autenticação e autorização por perfil.
- **Execução/deploy:** `compose.yaml` define MySQL, backend e frontend/Nginx; Dockerfiles multi-stage. Essas instruções ainda não foram validadas em containers neste ambiente.

## Funcionalidades conferidas no código

### Cliente

- Cardápio público sem login, filtro por categoria, paginação, cards de prato e detalhe carregado pela API com foto, descrição, preço e tempo de preparo.
- Carrinho persistente no navegador, quantidades, remoção ao reduzir a zero, subtotal/total e observações individuais por prato.
- Cadastro de cliente (nome, email, senha, telefone, endereço), login JWT e retorno à rota de checkout ao autenticar.
- Checkout com campo obrigatório de endereço, lembrado apenas durante a sessão atual do navegador, e pagamento acadêmico simulado. A tela só envia com perfil CLIENTE; a API valida pedido/estoque e congela os preços dos itens.
- Histórico paginado de pedidos do cliente, timeline com seis etapas, cancelamento permitido antes do preparo com motivo obrigatório em formulário e atualização automática do status na tela a cada 15 segundos.

### Administração e cozinha

- Categorias: criar, listar, editar e desativar.
- Pratos: criar, listar, editar, ativar/desativar/pausar via status, imagem por URL e tempo de preparo. Ficha técnica existente pode ser carregada e editada com rendimento, modo de preparo, itens, unidades e fator de correção.
- Custo por porção e food cost calculados no serviço Spring; a tela lista os valores e aplica as faixas de cor verde (até 30%), amarelo (até 35%) e vermelho (acima de 35%). Não há integração de imagem/upload; usa-se URL.
- Pedidos da cozinha com filtros por status, datas e canal, paginação, detalhes, dados de entrega e timeline. Gerência/admin podem cancelar pedidos não terminais com motivo obrigatório; a API estorna o estoque quando já houve baixa. Ciclo de status inclui RECEBIDO, CONFIRMADO, EM_PREPARO, PRONTO, SAIU_ENTREGA, FINALIZADO e CANCELADO.
- Ingredientes: CRUD (incluindo desativação lógica), saldos, mínimos, custos, entrada manual com lote/validade, perdas com motivo e consulta de movimentações recentes.
  - RF-032: alertas de ingredientes abaixo do mínimo no dashboard e contador vermelho no item Estoque do menu para ADMIN/GERENTE.
- Fornecedores: cadastro/edição/desativação, validação de CNPJ, catálogo por ingrediente, comparação de preços e gráfico de histórico de preços por fornecedor/ingrediente.
- Compras: criação de rascunho com múltiplos itens do catálogo, envio, recebimento, cancelamento, totalização e atualização de estoque/custo no serviço.
- Usuários internos: ADMIN pode criar, listar, editar e desativar gerente/cozinheiro. Seed ADMIN é protegido contra edição/desativação.
- Dashboard: faturamento, total de pedidos e ticket médio de hoje (dia de negócio em `America/Sao_Paulo`), food cost médio, pedidos em preparo e alertas; o intervalo selecionável filtra apenas top 5 e gráfico diário de vendas.

### Backend, segurança e dados

- Controllers, services, repositories, entities, DTOs, migrations versionadas, validação e tratamento global de erros.
- Movimentações de estoque registradas como razão de eventos; recebimento de compra e confirmação de pedido são transacionais. Cancelamento permitido faz estorno auditável; falta de estoque impede confirmação.
- Endpoints de health, Swagger, autenticação/cadastro, cardápio, pedidos, dashboard, catálogo, estoque, fornecedor, cotação/histórico, compra e usuário interno.
- Seed Flyway: `admin@email.com` / `senha123`, categorias, 20 ingredientes, seis pratos com ficha técnica e saldos iniciais. A credencial só serve para demonstração local e precisa ser trocada antes de qualquer exposição.

## Aderência ao SRS: dez blocos de pontuação

| Bloco | Situação pelo código | Observação |
|---|---|---|
| Cardápio, detalhe e carrinho | Implementado | Itens/quantidade/observações; imagens são URLs. |
| Jornada de cadastro a acompanhamento | Implementado | Pagamento e endereço são simulados/confirmados no checkout; sem gateway. |
| CRUD de cardápio | Implementado | Inclui modo de preparo; imagem por URL. |
| Ficha técnica e custos | Implementado | Cálculo no backend e faixas de food cost no painel. |
| Pedidos e cozinha | Implementado | Filtro, paginação, detalhe e timeline. |
| Pedido integrado ao estoque | Implementado | Baixa transacional e estorno em cancelamento elegível. |
| Fornecedores e compras | Implementado | Cotação, histórico de preço e compras multi-item. |
| Controle de estoque | Implementado | CRUD, lotes/validade, entradas, perdas, saldo, alertas e razão; badge de alertas vermelho no menu para ADMIN/GERENTE. |
| Dashboard | Implementado no código | KPIs diários independentes dos gráficos e ranking com período selecionável; smoke check anterior cobriu resposta autenticada antes da separação de KPIs. |
| Autenticação e RBAC | Implementado | JWT, quatro perfis, interceptor e guards. |

“Implementado” significa que o fluxo foi encontrado no código e templates; não significa que foi confirmado em execução conectada ao MySQL ou em deploy. A ficha técnica usa Reactive Forms/FormArray conforme recomendado. A navegação de páginas está conectada ao cardápio público, histórico de pedidos do cliente, pedidos administrativos, ingredientes/saldos, movimentações, fornecedores, pratos, categorias, compras e usuários internos. Os campos de seleção carregam uma página maior separada para manter as opções disponíveis.

## Verificação feita e limites do ambiente

- `npx tsc -p tsconfig.app.json --noEmit`: concluído sem erros após as últimas alterações.
- `npx ngc -p tsconfig.app.json --noEmit`: concluído sem erros; valida tipos Angular e templates.
- `npm run build -- --configuration production`: concluído sem advertências, com bundle inicial de 440,44 kB (estimativa de transferência 109,21 kB), abaixo do limite de aviso de 500 kB. Chart.js foi separado em chunk lazy de 204,97 kB (estimativa de transferência 60,99 kB), solicitado apenas quando os gráficos do dashboard são renderizados. O build precisou de execução com acesso ampliado porque o esbuild recebeu `Acesso negado` ao enumerar diretório ancestral dentro do sandbox.
- O Maven Wrapper 3.3.4 foi adicionado em `backend/`, fixado em Maven 3.10.0 com SHA-256 de distribuição configurado. `backend/mvnw.cmd -B -DskipTests package` foi executado com sucesso após a separação dos KPIs diários, compilou as 100 classes Java e empacotou o JAR Spring Boot sem Maven global. Testes automatizados não foram executados.
- `npx ngc -p tsconfig.app.json --noEmit` e `npx tsc -p tsconfig.app.json --noEmit` passaram após alinhar a tela/API do dashboard ao RF-034 e adicionar o badge do RF-032: pedidos/faturamento/ticket do dia no fuso `America/Sao_Paulo`; gráficos e top 5 continuam filtrados pelo intervalo escolhido; o contador de estoque usa `totalElements` e é consultado apenas para ADMIN/GERENTE.
- A API foi iniciada contra uma instância MySQL 8.0.46 descartável, isolada em outra pasta/porta e removida após a validação. Flyway aplicou V1 e V2 em schema vazio e o Hibernate passou por `ddl-auto=validate`; health retornou `ok`, cardápio listou seis pratos/cinco categorias, login do ADMIN seed emitiu JWT e o dashboard aceitou o token. Isso é smoke check de inicialização/rotas selecionadas, não valida todos os fluxos nem a versão MySQL 8.4 do Compose.
- A autoconfiguração de usuário in-memory do Spring Security foi desativada, porque a API usa login e autenticação JWT próprios; a nova inicialização não gerou senha temporária no log.
- Comparação estática entre a migration V1 e as entidades JPA: 15 tabelas no SQL, 15 entidades mapeadas, sem tabela faltante e sem divergência nos nomes explícitos de `@Column`/`@JoinColumn`. Isso não substitui `ddl-auto=validate` em runtime.
- `npm audit --omit=dev`: zero vulnerabilidades reportadas depois da atualização para Angular 22.2.3.
- `npm audit` completo ainda informa vulnerabilidades moderadas de dependências de desenvolvimento; não foi aplicado `--force`. O npm também avisou sobre scripts de instalação de dependências que não estão aprovados pelo mecanismo local; não foram aprovados.
- O backend compilou e iniciou no smoke check acima. O empacotamento acionou aviso HTTP 401 ao consultar metadados de um repositório Maven privado Flyway, mas todos os artefatos necessários vieram do Maven Central e o build concluiu com sucesso.
- `docker-compose-windows-x86_64.exe version 5.6.0` foi obtido do release oficial, conferido pelo SHA-256 publicado e `--env-file .env.example config --quiet` concluiu com código 0. Isso valida sintaxe/interpolação Compose; não executa containers.
- Docker Engine/Desktop não está instalado/disponível; Dockerfiles, build das imagens e MySQL 8.4 do Compose ainda não foram executados. WSL está presente, mas sem distribuição. Não foi feito deploy, não existem URLs públicas verificadas.
- Testes automatizados no backend: suite com 19 testes unitários implementada com JUnit 5 e Mockito em `backend/src/test/java/br/com/bomefit/comandadigital/service/`, cobrindo RN01, RN02, RN03, RN04, RN05, RN07, RN10, RNF01 e RF-018. Executado com `./mvnw.cmd test`: 19 testes, 0 falhas, 0 erros (BUILD SUCCESS). Testes do frontend ainda requerem runner de navegador para o Vitest.

## Dependências e arquivos importantes

- `package.json` / `package-lock.json`: dependências Angular/Chart.js e versões instaláveis.
- `backend/pom.xml`: dependências Spring Boot, JWT, MySQL, Flyway e Swagger.
- `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn/wrapper/maven-wrapper.properties`: Maven Wrapper 3.3.4 fixado em Maven 3.10.0, com SHA-256 da distribuição configurado; não requer Maven global.
- `backend/src/main/resources/application.yml`: porta, banco, Flyway, CORS e JWT por variáveis de ambiente.
- `.env.example`: parâmetros de referência para execução local; contém apenas um marcador inválido de `JWT_SECRET`, que deve ser substituído por uma chave aleatória gerada localmente. `.env` fica ignorado pelo Git.
- `compose.yaml`, `Dockerfile`, `backend/Dockerfile`, `deploy/nginx.conf`, `deploy/config-api.sh`: empacotamento local da stack e configuração estática.
- `docs/DER_Comanda_Digital.svg`: imagem vetorial do DER com 15 entidades e relações de FK da migration inicial; `docs/DER_Comanda_Digital.mmd` é a fonte Mermaid editável.
- `README.md`, `backend/README.md`, `deploy/README.md`: instruções operacionais atualmente não validadas em runtime.

## Pendências para a entrega acadêmica

1. Instalar/usar um ambiente com Docker Engine, construir as duas imagens com MySQL 8.4 e validar fluxo real ponta a ponta; o `config --quiet` já passou via Compose standalone, mas não há daemon para build/run.
2. Rever feedback restante de carregamento e validação; ações principais já usam toast global e acessível, enquanto erros de formulário também aparecem inline.
3. Escolher provedor, configurar domínio, banco gerenciado, HTTPS, CORS e segredos de produção; publicar e só então registrar URLs públicas verdadeiras no README.
4. Validar cenários completos com o frontend e conferir DER contra o schema executado. O smoke check atual cobre apenas inicialização e rotas selecionadas.
5. Gravar o vídeo pitch de cinco minutos e preparar a entrega individual exigidos pelo SRS. Há um roteiro de demonstração em `docs/ROTEIRO_VIDEO_PITCH.md`; ele não substitui a gravação e deve ser seguido somente após validar os fluxos ao vivo.

## Decisões técnicas e limites

- MySQL foi escolhido entre as opções permitidas; a versão `darkkitchen` permanece independente.
- O estado de estoque é derivado das movimentações, preservando auditoria; registros não são apagados em cascata.
- Custos e food cost são calculados com os custos atuais de ingrediente; compras recebidas atualizam preço e geram histórico.
- O segredo do JWT não tem fallback no backend nem chave reutilizável no exemplo; gere Base64 aleatório com pelo menos 32 bytes para cada ambiente.
- Pagamento é `PAGO_SIMULADO`; não existe integração financeira nem logística externa.
- Frontend de produção e API foram compilados; a API também passou por smoke check local com MySQL descartável. Compose/containers, experiência integrada ponta a ponta e publicação continuam sem validação. Não afirmar disponibilidade pública até realizar o deploy.
