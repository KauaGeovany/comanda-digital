# AGENTS.md — instruções para agentes de IA

## Antes de começar

1. Leia `docs/HANDOFF.md` para o estado do trabalho e `PROJECT_CONTEXT.md` para arquitetura, funcionalidades, decisões e validações anteriores.
2. Use `docs/SRS_Comanda_Digital_v3_2026-2.pdf` como fonte primária dos requisitos e critérios acadêmicos. Se houver conflito, aponte-o e siga o SRS; não transforme suposições em requisitos.
3. Este repositório é a nova Comanda Digital do Bom e Fit. Mantenha-o separado do projeto legado `darkkitchen`; não altere o legado.
4. Comece com `git status --short` e examine as alterações existentes. Há trabalho local importante não commitado. Preserve arquivos modificados e não execute `git reset`, `git clean`, checkout destrutivo nem substitua arquivos sem entender o diff.

## Regras de trabalho

- Converse e documente em português do Brasil.
- Antes de alterar código, confira as implementações e os requisitos relacionados; evite reescritas amplas quando uma mudança localizada resolver.
- Não afirme que um fluxo está validado só porque existe no código. Diferencie implementação, compilação, teste, smoke check, execução ponta a ponta e deploy.
- Nunca exponha segredos. `.env`, credenciais locais, builds e arquivos em `work/` não devem ser adicionados ao Git. Use `.env.example` somente como modelo e confirme que valores de segredo são marcadores.
- Mantenha Angular no frontend, Spring Boot/Java 17 no backend e MySQL/Flyway na persistência, salvo decisão explicitamente documentada.
- Registre decisões relevantes, resultados de validação e novas pendências em `PROJECT_CONTEXT.md` ou `docs/HANDOFF.md`.

## Mapa rápido

- `src/`: frontend Angular standalone, rotas, páginas, guards e integração HTTP.
- `backend/src/main/java/br/com/bomefit/comandadigital/`: API Spring MVC, segurança JWT, controllers, serviços, repositórios, entidades e DTOs.
- `backend/src/main/resources/db/migration/`: schema e seed Flyway.
- `compose.yaml`, `Dockerfile`, `backend/Dockerfile`, `deploy/`: execução em containers e Nginx.
- `docs/DER_Comanda_Digital.mmd` e `.svg`: modelo de dados.
- `docs/ROTEIRO_VIDEO_PITCH.md`: roteiro de demonstração, não é vídeo entregue.

## Comandos conhecidos

- Frontend: `npm ci` e `npm start` na raiz (padrão `http://localhost:4200`).
- API: configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e `FRONTEND_URL`; em `backend/`, rode `./mvnw.cmd spring-boot:run` no Windows ou `./mvnw spring-boot:run` em Unix (padrão `http://localhost:8080/api`).
- Stack completa: copie `.env.example` para `.env`, gere um `JWT_SECRET` aleatório Base64 com pelo menos 32 bytes e rode `docker compose up --build`, se Docker Engine estiver instalado.
- Seed de demonstração local: `admin@email.com` / `senha123`. Troque antes de qualquer exposição externa.

Confira versões, pré-requisitos e limitações em `README.md`, `backend/README.md` e `PROJECT_CONTEXT.md`; processos e credenciais temporários de outra sessão podem não existir mais.
