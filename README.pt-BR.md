# Velune · API de Gestão Hoteleira

![CI](https://github.com/MarcosEduardoDev/Velune/actions/workflows/ci.yml/badge.svg)
![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)

🇬🇧 [Read in English](README.md)

API REST para gestão hoteleira, desenvolvida com Java 25 e Spring Boot. O Velune organiza hóspedes, quartos, reservas e escalas de funcionários, usando PostgreSQL para persistência.

O projeto está em desenvolvimento ativo e reúne uma API em camadas, regras de negócio testadas com JUnit e Mockito, uma interface web estática e um ambiente de desenvolvimento com PostgreSQL via Docker Compose.

## Funcionalidades

- Cadastro e consulta de hóspedes, com verificação de documento duplicado.
- Cadastro e listagem de quartos, com número único e tipos predefinidos.
- Criação, consulta por data e cancelamento de reservas.
- Verificação de disponibilidade de quartos durante o período solicitado.
- Cadastro e consulta de funcionários.
- Registro de escalas de funcionários, com criação de uma notificação associada.
- Interface web para navegar pelo painel, hóspedes, quartos, reservas e equipe.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Spring Web e Bean Validation
- Spring Data JPA
- PostgreSQL 16
- Maven
- JUnit 5 e Mockito
- Docker Compose
- GitHub Actions

## API

Os nomes das rotas, dos campos e das mensagens de erro seguem o domínio do projeto e estão em português.

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| `GET` | `/hotel` | Verifica se a API está respondendo | 200 | |
| `GET` | `/hospedes` | Lista os hóspedes | 200 | |
| `GET` | `/hospedes/{documento}` | Busca um hóspede pelo documento | 200 | 404 |
| `POST` | `/hospedes` | Cadastra um hóspede | 201 | 400, 409 |
| `GET` | `/quartos` | Lista os quartos | 200 | |
| `POST` | `/quartos` | Cadastra um quarto | 201 | 400, 409 |
| `GET` | `/reservas` | Lista as reservas | 200 | |
| `GET` | `/reservas/{data}` | Lista reservas ativas na data (`yyyy-MM-dd`) | 200 | |
| `POST` | `/reservas` | Cria uma reserva | 201 | 400, 404, 409 |
| `DELETE` | `/reservas/{quartoId}/{data}` | Cancela a reserva ativa do quarto na data | 204 | 404 |
| `GET` | `/funcionarios` | Lista os funcionários | 200 | |
| `POST` | `/funcionarios` | Cadastra um funcionário | 201 | 400 |
| `GET` | `/funcionarios/{id}` | Busca um funcionário pelo ID | 200 | 404 |
| `POST` | `/funcionarios/{id}/escalas` | Define uma escala e cria uma notificação | 201 | 400, 404 |
| `GET` | `/funcionarios/{id}/escalas` | Retorna nome, cargo e escalas do funcionário | 200 | 404 |

Erros de validação retornam `400` com um objeto JSON que relaciona os campos às mensagens. Exceções de negócio retornam `404` ou `409`, conforme o caso.

### Exemplo: criar uma reserva

`POST /reservas`

```json
{
  "hospedeId": 1,
  "quartoId": 1,
  "dataCheckIn": "2026-12-10",
  "dataCheckOut": "2026-12-15",
  "observacao": "Chegada tarde"
}
```

Uma reserva inclui o dia do check-in e exclui o dia do check-out. Assim, outra reserva pode começar no mesmo dia em que a estadia anterior termina. Um período inválido ou dados obrigatórios ausentes retornam `400`; hóspede ou quarto inexistente retorna `404`; conflito de disponibilidade retorna `409`.

## Arquitetura

A API segue uma arquitetura em camadas:

```text
Interface web / cliente HTTP
             |
             v
        Controller   rotas REST e validação dos DTOs
             |
             v
         Service      regras de negócio
             |
             v
       Repository     acesso a dados com Spring Data JPA
             |
             v
         PostgreSQL
```

Os principais pacotes estão organizados por responsabilidade:

```text
velune
├── controller   endpoints REST
├── service      regras de negócio
├── repository   acesso a dados com Spring Data JPA
├── model        entidades JPA e enums
├── dto          objetos de entrada e resposta
└── exception    exceções de negócio e tratamento global
```

O schema SQL está em [`db/schema.sql`](db/schema.sql). O Docker Compose monta esse arquivo para inicialização do PostgreSQL.

## Executar com Docker

Pré-requisitos: Docker Desktop com Docker Compose, JDK 25 e Maven. O Compose inicia o banco PostgreSQL; a API é executada localmente com Maven.

1. Clone o repositório:

```powershell
git clone https://github.com/MarcosEduardoDev/Velune.git
cd Velune
```

2. Crie o arquivo `.env` e troque o valor de `POSTGRES_PASSWORD` por uma senha sua:

```powershell
copy .env.example .env
```

No Linux ou macOS, use `cp .env.example .env`.

3. Inicie o banco:

```powershell
docker compose up -d
```

Na primeira inicialização do banco, o PostgreSQL executa `db/schema.sql` e cria as tabelas.

4. Configure a conexão da API no PowerShell, usando a mesma senha definida em `.env`:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5433/velune'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'a-mesma-senha-definida-no-env'
mvn spring-boot:run
```

No Linux ou macOS, configure as mesmas variáveis com `export` e execute `mvn spring-boot:run`.

5. Acesse <http://localhost:8080/hotel> para conferir a resposta da API. A interface web fica disponível em <http://localhost:8080/>. Se ainda não houver quartos cadastrados, `GET /quartos` retorna `[]`.

Para parar e remover o container:

```powershell
docker compose down
```

O Compose não define um volume nomeado para os dados. A imagem do PostgreSQL pode criar um volume anônimo: `docker compose down` não o remove por padrão, e um próximo `up` não o reutiliza automaticamente. Para remover também esses volumes e reiniciar o banco sem os dados anteriores, use `docker compose down -v`. [Documentação do Docker Compose](https://docs.docker.com/reference/cli/docker/compose/down/).

## Executar com PostgreSQL local

Também é possível executar a API sem Docker. Para isso, instale JDK 25, Maven e PostgreSQL, crie um banco chamado `velune` e execute nele [`db/schema.sql`](db/schema.sql).

Configure a conexão no PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/velune'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'sua_senha'
mvn spring-boot:run
```

Não versione senhas reais. O arquivo `.env` está incluído no `.gitignore`.

## Testes

Os testes atuais são testes unitários da camada de service, feitos com JUnit 5 e Mockito. Eles não precisam de banco de dados:

```bash
mvn test
```

A suíte cobre cadastro de hóspedes e quartos, regras de disponibilidade de reservas, períodos sobrepostos, limite entre check-out e novo check-in, definição de escalas e montagem do DTO de escalas do funcionário.

O workflow [`ci.yml`](.github/workflows/ci.yml) executa `mvn test` em pushes para `master` e em pull requests.

## Interface web

A interface estática fica em `src/main/resources/static` e é servida pela própria aplicação Spring Boot. Ela permite consultar e cadastrar hóspedes e quartos, além de criar, consultar e cancelar reservas.

Alguns elementos ainda usam dados de demonstração, como as movimentações do painel e a página da equipe. Portanto, a interface ainda não representa todas as operações da API com dados reais.

## Limitações conhecidas

- A API ainda não tem autenticação nem autorização.
- A verificação de disponibilidade acontece na camada Java; duas solicitações simultâneas podem passar pela checagem antes de qualquer uma salvar a reserva.
- Salários e valores de diárias usam `double`.
- A maioria dos endpoints retorna entidades diretamente. O endpoint de escalas do funcionário usa DTO de resposta, mas os demais endpoints de funcionário ainda retornam a entidade.
- O cancelamento localiza a reserva pelo quarto e por uma data, não pelo ID da reserva.
- Ainda não há paginação.
- Os testes cobrem a camada de service; ainda não há testes de controller nem de integração.
- Ainda não há ferramenta de migração de banco. O schema está definido em um script SQL.

## Autor

**Marcos Eduardo Oliveira Silva** · Desenvolvedor Java Backend Júnior

[LinkedIn](https://www.linkedin.com/in/marcos-eduardo-245587345/) · [GitHub](https://github.com/MarcosEduardoDev)