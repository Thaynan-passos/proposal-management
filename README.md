# Proposal Management API

Sistema de gerenciamento de propostas comerciais entre **Influenciadores** (`INFLUENCER`) e **Marcas** (`BRAND`), construído com **Spring Boot**, **Spring Security**, **Spring Data JPA** e arquitetura inspirada em **DDD (Domain-Driven Design) / Clean Architecture**.

---

## 🏛 Arquitetura

O projeto adota separação clara de responsabilidades:

- **Domain Layer (`proposal/domain`, `auth/domain`)**:
  - Entidades e Value Objects (`Proposal`, `ProposalId`, `Owner`, `OwnerId`, `ProposalStatus`).
  - Regras de negócio puras e transições de estado (`accept()`, `reject()`, `cancel()`).
  - Interfaces de repositório de domínio (`ProposalRepository`).
- **Application Layer (`proposal/application`)**:
  - Casos de uso desacoplados da infraestrutura (`CreateProposalUseCase`, `FindProposalsUseCase`, `UpdateProposalStatusUseCase`).
- **Infrastructure Layer (`infrastructure`)**:
  - **Persistence**: Entidades JPA (`ProposalJpaEntity`, `User`), repositórios Spring Data e adaptadores (`ProposalRepositoryImpl`).
  - **Security**: Filtro de autenticação baseado em JSON (`RestUsernamePasswordAuthenticationFilter`), gerenciamento de sessões, encriptação BCrypt e controle de acesso com `@PreAuthorize`.
  - **HTTP / REST**: Controllers REST (`ProposalController`, `Controller`), DTOs e tratador global de exceções (`GlobalExceptionHandler`).

---

## 👥 Usuários de Teste Pré-cadastrados

Ao iniciar a aplicação, os seguintes usuários são carregados automaticamente no banco:

| Usuário | Senha | Perfil (Role) | Descrição |
| :--- | :--- | :--- | :--- |
| `fitness_vibe` | `password` | `INFLUENCER` | Influenciador fitness |
| `tech_guru` | `password` | `INFLUENCER` | Criador de conteúdo de tecnologia |
| `logistics` | `password` | `BRAND` | Empresa / Marca contratante |

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 25** (ou JDK 21+ com compatibilidade)
- **Docker e Docker Compose** (para o container MySQL)

### 1. Iniciar Banco de Dados via Docker Compose
```bash
docker compose up -d
```
> O banco MySQL será iniciado na porta local `3307` conforme configurado no [compose.yml](file:///c:/Users/tavila/Documents/GitHub/proposal-management/compose.yml).

### 2. Executar a Aplicação Spring Boot
```bash
./gradlew bootRun
```
> Com o módulo `spring-boot-docker-compose` ativo, o Spring Boot também pode gerenciar o container automaticamente em tempo de desenvolvimento.

### 3. Rodar os Testes Automatizados
```bash
./gradlew test
```
> Os testes utilizam banco H2 em memória sem necessidade de Docker em execução.

---

## 📡 Endpoints da API

### Autenticação & Perfil
- `POST /api/auth/login`: Autenticação via JSON `{"username": "...", "password": "..."}`.
- `GET /api/auth/me`: Retorna os dados do usuário autenticado.
- `GET /influencer`: Exclusivo para usuários com papel `ROLE_INFLUENCER`.
- `GET /brand`: Exclusivo para usuários com papel `ROLE_BRAND`.

### Propostas (`/api/proposals`)
- `POST /api/proposals`: Cria uma nova proposta.
  ```json
  {
    "title": "Campanha de Lançamento de Produto",
    "description": "3 reels e 5 stories no Instagram",
    "amount": 2500.00
  }
  ```
- `GET /api/proposals`: Lista propostas (para influenciadores lista as suas; use `?mine=true` ou `false`).
- `GET /api/proposals/{id}`: Detalhes de uma proposta específica.
- `PATCH /api/proposals/{id}/accept`: Aceita a proposta (**Apenas marcas / BRAND**).
- `PATCH /api/proposals/{id}/reject`: Rejeita a proposta (**Apenas marcas / BRAND**).
- `PATCH /api/proposals/{id}/cancel`: Cancela a proposta (**Apenas o proprietário que criou**).

---

## 🧪 Testes Manuais com HTTP Client

Você pode executar as requisições prontas no arquivo:
👉 [http/proposals.http](file:///c:/Users/tavila/Documents/GitHub/proposal-management/http/proposals.http)
