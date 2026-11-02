# Contaê API

API REST para cadastro e autenticação de usuários e gerenciamento financeiro de contas, categorias e movimentações.

## Stack

- Java 21 e Spring Boot 4
- Spring MVC, Spring Security com JWT e Spring Data JPA
- PostgreSQL; testes de persistência usam H2 e Testcontainers com PostgreSQL
- Maven Wrapper e Docker Compose

## Pré-requisitos

- JDK 21 para executar a aplicação e os testes localmente
- Docker com Docker Compose para executar o conjunto local ou os testes concorrentes com Testcontainers
- Git para clonar o repositório

## Configuração

Copie `.env.example` para `.env` e substitua os valores de exemplo antes de iniciar. O `.env` é local e não deve ser versionado.

| Variável | Uso |
| --- | --- |
| `POSTGRES_DB` | Nome do banco criado pelo Compose |
| `POSTGRES_USER` | Usuário do PostgreSQL no Compose |
| `POSTGRES_PASSWORD` | Senha obrigatória do PostgreSQL; usada pelo banco e pela conexão interna da API |
| `POSTGRES_PORT` | Porta do PostgreSQL publicada no host |
| `API_PORT` | Porta HTTP da API publicada no host |
| `JWT_SECRET` | Chave aleatória de pelo menos 32 bytes para assinatura JWT (HS256) |
| `JWT_EXPIRATION` | Validade do token em milissegundos |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexão PostgreSQL quando a API roda fora do Compose |

O Compose monta a URL interna do banco usando `POSTGRES_DB` e encaminha as credenciais `POSTGRES_USER` e `POSTGRES_PASSWORD` para a API. `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` são usados ao iniciar a API diretamente na máquina. `JWT_SECRET` e `POSTGRES_PASSWORD` são obrigatórios no Compose; não há chaves padrão.

## Executar com Docker Compose

Com o arquivo `.env` configurado:

```sh
docker compose up --build
```

A API fica disponível em `http://localhost:8080` e o PostgreSQL na porta definida por `POSTGRES_PORT`. Para parar os serviços, execute `docker compose down`. O volume `postgres_data` mantém os dados locais.

## Executar localmente

Inicie um PostgreSQL local, configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e, opcionalmente, `JWT_EXPIRATION` no `.env`. Depois execute:

```sh
./mvnw spring-boot:run
```

No Windows, use `.\mvnw.cmd spring-boot:run` no PowerShell.

## Testes

```sh
./mvnw verify
```

No Windows PowerShell:

```powershell
.\mvnw.cmd verify
```

Os testes de concorrência iniciam um PostgreSQL descartável com Testcontainers e precisam de um daemon Docker disponível.

## OpenAPI

Com a API em execução, acesse [Swagger UI](http://localhost:8080/swagger-ui/index.html) ou [OpenAPI JSON](http://localhost:8080/v3/api-docs).

## Cadastro, login e chamada autenticada

Os exemplos usam `curl` e valores de demonstração. Substitua o email e a senha pelos dados que deseja cadastrar.

Cadastre um usuário:

```sh
curl -sS -X POST http://localhost:8080/api/usuarios \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Pessoa Exemplo","email":"pessoa@example.com","senha":"SUBSTITUA_POR_SUA_SENHA"}'
```

Faça login e copie o campo `token` da resposta:

```sh
curl -sS -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"pessoa@example.com","senha":"SUBSTITUA_POR_SUA_SENHA"}'
```

Envie o JWT como Bearer token para acessar uma rota protegida:

```sh
curl -sS http://localhost:8080/contas \
  -H 'Authorization: Bearer COLE_O_TOKEN_AQUI'
```
