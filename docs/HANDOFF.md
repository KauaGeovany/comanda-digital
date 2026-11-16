# Handoff para continuidade por outra IA

Atualizado em 2026-10-20. Este documento é um ponto de partida; confirme o estado do checkout, ferramentas e processos antes de agir.

## Fontes de contexto (ler nesta ordem)

1. `AGENTS.md` — regras permanentes para trabalhar neste repositório.
2. `docs/SRS_Comanda_Digital_v3_2026-2.pdf` — especificação acadêmica, fonte primária dos requisitos.
3. `PROJECT_CONTEXT.md` — arquitetura observada, inventário de fluxos, matriz resumida de aderência, decisões e evidências de validação.
4. `README.md`, `backend/README.md`, `deploy/README.md` — execução e empacotamento.

## Estado do checkout

- O diretório é `C:\Users\conta\Documents\bomefit`.
- O projeto foi iniciado a partir de um esqueleto Angular e já ganhou frontend integrado a uma API Spring Boot e persistência MySQL/Flyway.
- No início deste handoff, `git status --short` mostrou modificações em arquivos existentes (`.gitignore`, `README.md`, `package.json`/lock e aplicação Angular) e muitos arquivos novos, incluindo `backend/`, `compose.yaml`, Dockerfiles, documentação, páginas e serviços. **O trabalho está sem commit e deve ser preservado.** Revise o diff antes de continuar; não faça limpeza/reset.
- `PROJECT_CONTEXT.md` descreve funcionalidades encontradas no código e verificações anteriores. Isso não significa que todos os cenários foram testados ou que os containers/deploy estejam prontos.

## Arquitetura e funções já presentes

Angular 22 / TypeScript no cliente; API Java 17 / Spring Boot 3.5; MySQL 8.4 com JPA, Flyway e JWT. Há fluxo público de cardápio/carrinho/cadastro/checkout simulado/acompanhamento; áreas administrativas de categorias, pratos e ficha técnica, pedidos/cozinha, ingredientes/estoque, fornecedores/compras, usuários internos e dashboard. Existem quatro perfis (ADMIN, GERENTE, COZINHEIRO, CLIENTE). Consulte `PROJECT_CONTEXT.md` para os limites de cada fluxo e endpoints.

## Validações registradas e lacunas

Conforme o registro em `PROJECT_CONTEXT.md`: compilação Angular e backend 100% verde; suite com 19 testes automatizados JUnit 5/Mockito validada no backend com 0 falhas cobrindo as regras críticas do SRS (RN01 a RN05, RN07, RN10, RNF01 e RF-018); a API já iniciou contra MySQL descartável e passou smoke check; Flyway aplicou migrations e JPA validou o schema naquela execução. Não há execução dos containers com Docker (máquina sem daemon), percurso ponta a ponta completo nem deploy público. Não presuma que serviços temporários ainda estão em execução.

## Próxima sequência recomendada

1. Conferir `git status --short`, diffs, branches e a presença/configuração local dos pré-requisitos sem sobrescrever mudanças.
2. Comparar cada critério do SRS com o código e as evidências atuais; priorizar requisitos de avaliação sem cobertura comprovada.
3. Tornar a execução reproduzível na máquina disponível. Se Docker não estiver instalado, registrar a alternativa de ambiente e não declarar o Compose validado.
4. Validar por fluxo: login/perfis, cardápio e pedido, baixa/estorno de estoque, ficha técnica/custos, compra/recebimento, dashboard e persistência. Registrar o comando, ambiente e resultado real.
5. Atualizar este handoff e `PROJECT_CONTEXT.md` ao concluir cada etapa; depois preparar a demonstração/pitch acadêmico descrita no SRS.

## Cuidados de ambiente

- `.env` e `work/` são ignorados pelo Git. A sessão anterior usou credenciais e MySQL temporários em `work/`; eles podem não existir ou não estar ativos, e não devem ser presumidos nem compartilhados.
- O Compose requer Docker Engine. O modo local requer Node, Java 17 e MySQL; o Maven Wrapper está em `backend/`.
- Login seed `admin@email.com` / `senha123` é somente para demonstração local.
- O pagamento é acadêmico/simulado; não há gateway financeiro ou integração logística. Não anunciar URL pública até realizar e verificar um deploy real.
