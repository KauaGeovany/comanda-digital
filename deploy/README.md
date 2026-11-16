# Estrutura de implantação

`docker compose up --build` sobe o conjunto completo para avaliação local. O Compose já separa a rede interna do MySQL, espera o health check antes da API, injeta os segredos no backend, persiste o banco em volume e publica o Angular através do Nginx com fallback do Router.

## Variáveis para um ambiente publicado

- `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`: credenciais exclusivas do banco.
- `JWT_SECRET`: Base64 aleatório com pelo menos 32 bytes. Gere um valor novo para cada ambiente; `.env.example` contém apenas um marcador que precisa ser substituído.
- `FRONTEND_URL`: origem pública exata do navegador, usada pela configuração CORS do backend.
- `API_PUBLIC_URL`: endereço público HTTPS da API, usado pelo script de inicialização Nginx para gerar `config.js` no navegador.
- `FRONTEND_PORT`, `API_PORT` e `MYSQL_PORT`: portas publicadas no host; em um serviço gerenciado, o proxy pode definir essas portas.

O arquivo `compose.yaml` não termina TLS. Em publicação, coloque os serviços atrás do proxy TLS da plataforma, defina `FRONTEND_URL` e `API_PUBLIC_URL` para domínios HTTPS públicos e mantenha MySQL acessível somente pela rede privada. Configure backups do volume de banco e rotacione credenciais fora do repositório.

## Situação

Os Dockerfiles, Nginx, configuração de API do frontend e Compose estão preparados. A configuração passou por `docker-compose-windows-x86_64.exe --env-file .env.example config --quiet` com Docker Compose standalone 5.6.0; o executável foi conferido pelo checksum oficial. Isso não constrói imagens nem inicia serviços. O ambiente atual não possui Docker Engine/Desktop, então a imagem frontend, backend e MySQL 8.4 do Compose ainda precisam ser executadas em um host com daemon disponível.

## Validação pendente

Não há URLs de produção. A sintaxe e interpolação Compose foram validadas; Dockerfiles e imagens precisam ser construídos e iniciados em um ambiente com Docker antes de publicar.

