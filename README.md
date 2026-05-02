# 📦 SaaS Subscription System

API REST para gerenciamento de assinaturas SaaS, com controle de clientes, planos, faturamento e limites de uso.

---

## 🚀 Tecnologias

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Docker
- JUnit / Mockito

---

## 📌 Funcionalidades

- Cadastro e gerenciamento de clientes
- Criação de planos de assinatura
- Controle de faturamento e pagamentos
- Limite de criação de projetos por plano
- Validação de regras de negócio

---

## 🧱 Arquitetura

O projeto segue arquitetura em camadas:

- Controller → entrada de requisições
- Service → regras de negócio
- Repository → acesso ao banco de dados

---

## 🔌 Principais endpoints

### Clientes

- POST /customers — cria um cliente
- GET /customers — lista clientes
- GET /customers/{id} — busca cliente por ID

### Assinaturas

- POST /subscriptions - cria uma assinatura
- GET /subscriptions/{id} - busca assinatura por ID

### Projetos

- POST /projects - cria um projeto
- GET /projects/{id} - busca projeto por ID

---

## 🧪 Testes

O projeto possui testes unitários cobrindo regras de negócio e validações.

```bash
./mvnw test
```

---

## ⚙️ Variáveis de ambiente

O projeto utiliza variáveis de ambiente para configuração do banco de dados:

- DB_HOST
- DB_PORT
- DB_NAME
- DB_USER
- DB_PASSWORD

As configurações podem ser feitas via arquivo `.env` ou diretamente no `docker-compose`.

---

## ▶️ Como rodar o projeto

### Pré-requisitos

- Java 17+
- Docker

### Passos

```bash
git clone https://github.com/daviamaral7/sistema-de-assinaturas
cd sistema-de-assinaturas
docker-compose up
```

---

## 📄 Observações

Projeto desenvolvido com foco em boas práticas de desenvolvimento backend, incluindo validação, tratamento de exceções e
testes automatizados.