# Backend · Bom e Fit Comanda Digital

API REST em Java 17 / Spring Boot 3.5. MySQL 8.4, Flyway (`V1` schema, `V2` seed), Spring Security stateless com JWT, regras por perfil, DTOs, validação e erros centralizados.

## Executar localmente

Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `FRONTEND_URL` e opcionalmente `JWT_EXPIRATION_MS` no processo. `.env` não é carregado automaticamente pelo Spring.

Use o Maven Wrapper incluso: `./mvnw spring-boot:run` (Linux/macOS) ou `./mvnw.cmd spring-boot:run` (Windows). Na primeira execução ele baixa Maven 3.10.0 e valida o SHA-256 configurado em `.mvn/wrapper/maven-wrapper.properties`; é necessário acesso à internet. A documentação da API fica em `http://localhost:8080/swagger-ui.html`; health check em `GET /api/health`.

Use segredo JWT Base64 aleatório com ao menos 32 bytes fora da demonstração local. O usuário seed `admin@email.com` / `senha123` serve somente para desenvolvimento acadêmico e deve ser trocado antes de uma publicação.

O build do backend e a inicialização com MySQL 8.0.46 descartável já foram validados; Flyway aplicou V1/V2 e JPA passou por `ddl-auto=validate`. Docker/Compose com MySQL 8.4 e deploy público continuam pendentes. Veja `../PROJECT_CONTEXT.md` para o estado detalhado.
