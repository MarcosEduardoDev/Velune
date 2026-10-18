# Velune · API de Gestão Hoteleira

![CI](https://github.com/MarcosEduardoDev/Velune/actions/workflows/ci.yml/badge.svg)
![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)

🇬🇧 [Read in English](README.md)

API REST de gestão hoteleira, feita com Java e Spring Boot. Ela gerencia hóspedes, quartos, reservas e escalas de funcionários, e guarda os dados no PostgreSQL. O projeto tem arquitetura em camadas, regras de negócio cobertas por testes unitários e um pipeline de CI que roda os testes a cada push.

> **Status:** projeto de portfólio em desenvolvimento ativo. Veja as [Limitações conhecidas](#limitações-conhecidas).

## Um tour de 2 minutos

1. **Regra de reservas:** o [`ReservaService`](src/main/java/velune/service/ReservaService.java) verifica a disponibilidade do quarto. Uma estadia inclui o dia do check-in e exclui o dia do check-out, então um quarto pode ser reservado a partir do dia em que outro hóspede sai. Os casos de borda estão testados no [`ReservaServiceTest`](src/test/java/velune/service/ReservaServiceTest.java).
2. **Tratamento de erros:** o [`GlobalExceptionHandler`](src/main/java/velune/exception/GlobalExceptionHandler.java) transforma exceções de negócio em `404` e `409`, e erros de validação em `400`.
3. **Transação:** o [`FuncionarioService`](src/main/java/velune/service/FuncionarioService.java) cria uma escala e a notificação dela em uma única operação `@Transactional`.
4. **DTO de resposta:** `GET /funcionarios/{id}/escalas` devolve só nome, cargo e escalas. Salário e idade não são expostos.
5. **CI:** o [`ci.yml`](.github/workflows/ci.yml) roda `mvn test` a cada push e pull request.

## API

Os nomes dos campos e as mensagens de erro estão em português, a linguagem de domínio do projeto.

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| `GET` | `/hotel` | Verifica se a API responde | 200 | |
| `GET` | `/hospedes` | Lista hóspedes | 200 | |
| `GET` | `/hospedes/{documento}` | Busca hóspede pelo documento | 200 | 404 |
| `POST` | `/hospedes` | Cadastra hóspede | 201 | 400, 409 |
| `GET` | `/quartos` | Lista quartos | 200 | |
| `POST` | `/quartos` | Cadastra quarto | 201 | 400, 409 |
| `GET` | `/reservas` | Lista reservas | 200 | |
| `GET` | `/reservas/{data}` | Reservas que incluem a data (`yyyy-MM-dd`) | 200 | |
| `POST` | `/reservas` | Cria reserva | 201 | 400, 404, 409 |
| `DELETE` | `/reservas/{quartoId}/{data}` | Cancela a reserva de um quarto em uma data | 204 | 404 |
| `GET` | `/funcionarios` | Lista funcionários | 200 | |
| `POST` | `/funcionarios` | Cadastra funcionário | 201 | 400 |
| `GET` | `/funcionarios/{id}` | Busca funcionário | 200 | 404 |
| `POST` | `/funcionarios/{id}/escalas` | Define uma escala e cria uma notificação | 201 | 400, 404 |
| `GET` | `/funcionarios/{id}/escalas` | Nome, cargo e escalas do funcionário | 200 | 404 |

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

Devolve `201 Created`. Se o quarto já estiver reservado no período, devolve `409 Conflict` com uma mensagem.

### Exemplo: escalas de um funcionário

`GET /funcionarios/1/escalas`

```json
{
  "nome": "Ronaldo",
  "cargo": "GERENTE",
  "escalas": [
    { "turno": "MANHA", "data": "2026-12-10" },
    { "turno": "MANHA", "data": "2026-12-11" }
  ]
}
```

## Arquitetura

```text
Cliente HTTP / interface web
        |
        v
   Controller   recebe a requisição, valida o DTO de entrada
        |
        v
    Service     regras de negócio
        |
        v
   Repository   Spring Data JPA
        |
        v
   PostgreSQL
```

```text
velune
├── controller   endpoints REST
├── service      regras de negócio
├── repository   interfaces do Spring Data JPA
├── model        entidades JPA e enums
├── dto          objetos de entrada e de resposta
└── exception    exceções próprias e o tratador global
```

## Decisões de projeto

- **As regras ficam na camada de service.** Os repositories só leem e gravam dados.
- **A unicidade é garantida duas vezes:** uma checagem no service devolve um `409` claro, e as constraints `UNIQUE` do banco ([`db/schema.sql`](db/schema.sql)) são a garantia final. Vale para o documento do hóspede e o número do quarto.
- **As exceções de negócio são não verificadas** e tratadas em um só lugar, então controllers e services ficam sem `try/catch`.
- **DTOs de entrada com Bean Validation** mantêm dados inválidos fora dos services.
- **O schema do banco é SQL explícito**, e o Hibernate não altera as tabelas.
- **Os services são testados com Mockito**, então os testes não precisam de banco e rodam rápido no CI.

## Testes

São 10 testes unitários da camada de service (JUnit 5 e Mockito). Eles cobrem:

- hóspedes e quartos duplicados são recusados;
- disponibilidade de quarto, incluindo a borda do dia do check-out e datas que se sobrepõem;
- definir uma escala salva o funcionário com uma escala e uma notificação;
- o cartão de escalas do funcionário é montado corretamente, e um funcionário inexistente gera erro de não encontrado.

Para rodar, sem banco de dados:

```bash
mvn test
```

## Executar localmente

Requisitos: JDK 25, Maven e PostgreSQL.

1. Clone o repositório:

```bash
git clone https://github.com/MarcosEduardoDev/Velune.git
cd Velune
```

2. Crie um banco PostgreSQL chamado `Velune` e rode nele o [`db/schema.sql`](db/schema.sql). O script pode ser rodado mais de uma vez com segurança.

3. Defina a conexão no terminal (exemplo em PowerShell). Nunca versione senhas reais.

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/Velune"
$env:SPRING_DATASOURCE_USERNAME = "seu_usuario"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha"
```

4. Inicie a aplicação:

```bash
mvn spring-boot:run
```

Abra `http://localhost:8080/hotel` para conferir que a API responde.

## Limitações conhecidas

- **Sem autenticação nem autorização.**
- **Overbooking é possível sob concorrência.** A checagem de disponibilidade roda em Java antes de salvar, então duas requisições simultâneas poderiam passar. Proteção no banco está planejada.
- **Dinheiro usa `double`** (salários e preços dos quartos). `BigDecimal` está planejado.
- **A maioria dos endpoints devolve entidades.** DTOs de resposta existem só onde a entidade exporia campos sensíveis (escalas de funcionário).
- **O cancelamento localiza a reserva por quarto e data**, e não pelo id da reserva.
- **Sem paginação.**
- **Os testes cobrem só a camada de service.** Ainda não há testes de controller nem de integração.
- **Ainda sem ferramenta de migração nem Docker.** O schema é um único script SQL.

## Roteiro

- Docker Compose com PostgreSQL
- Documentação OpenAPI / Swagger
- Migrações de banco com Flyway
- Testes de integração (controllers e repositories)
- Endpoints de reserva por id
- DTOs de resposta em todos os endpoints
- Front-end em Vue.js

## Sobre a interface web

A interface web estática em `src/main/resources/static` foi gerada com auxílio de IA. O foco deste projeto é o backend. A página de equipe da interface ainda usa dados de demonstração.

## Autor

**Marcos Eduardo Oliveira Silva**, desenvolvedor Java backend júnior.
[LinkedIn](https://www.linkedin.com/in/marcos-eduardo-245587345/) · [GitHub](https://github.com/MarcosEduardoDev)