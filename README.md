# Velune

<p align="center">
  <strong>Hotel Management System · Java Backend Project</strong><br>
  Uma aplicação em desenvolvimento para praticar construção de APIs backend com Java e Spring Boot, usando um domínio realista de gestão hoteleira.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 25">
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/JUnit_+_Mockito-Testing-25A162?style=flat-square" alt="JUnit and Mockito">
</p>

> **Status: em desenvolvimento.** Hóspedes, quartos e reservas estão conectados à API e ao PostgreSQL. O módulo de equipe e alguns indicadores do painel ainda usam dados de demonstração.

---

## Visão geral

O **Velune** é um projeto prático para desenvolver minhas habilidades em backend Java. Em vez de ficar apenas em exercícios isolados, estou construindo uma aplicação que reúne regras de negócio, persistência de dados, validação e uma interface web.

O foco principal é aprender e aplicar:
- Java e Spring Boot para construir uma API REST;
- separação de responsabilidades entre Controller, Service e Repository;
- persistência com Spring Data JPA / Hibernate e PostgreSQL;
- validação de entradas, tratamento de erros e regras de negócio;
- testes automatizados e evolução incremental do código.

## Funcionalidades implementadas

- Cadastro e consulta de hóspedes com persistência no PostgreSQL.
- Cadastro e consulta de quartos com persistência no PostgreSQL.
- Cadastro e consulta de reservas.
- Verificação de conflito de datas ao criar uma reserva.
- Busca de reservas que incluem uma data específica pela API.
- Cancelamento de reserva pela API, localizado atualmente pelo ID do quarto e uma data.
- Interface web para navegar pelas seções de painel, hóspedes, quartos, reservas e equipe.

## Tecnologias

| Tecnologia | Uso no projeto |
|---|---|
| **Java 25** | Linguagem principal |
| **Spring Boot 4.1.1** | Aplicação backend |
| **Spring Web** | Endpoints HTTP / REST |
| **Bean Validation** | Validação dos dados de entrada |
| **Spring Data JPA / Hibernate** | Persistência e mapeamento objeto-relacional |
| **PostgreSQL** | Banco de dados relacional |
| **Maven** | Dependências e execução da aplicação |
| **JUnit e Mockito** | Ferramentas para testes |
| **HTML, CSS e JavaScript** | Interface web servida pela aplicação |

## Arquitetura

A API segue uma estrutura em camadas:

```text
Cliente HTTP / Interface web
          |
          v
      Controller  -> recebe requisições e encaminha os dados
          |
          v
        Service   -> aplica regras de negócio
          |
          v
      Repository  -> acessa os dados
          |
          v
      PostgreSQL
```

- **Controller:** expõe os endpoints e conecta as requisições à aplicação.
- **Service:** concentra regras como a verificação de disponibilidade de quartos.
- **Repository:** usa Spring Data JPA para consultar e persistir entidades.
- **DTO:** representa os dados recebidos pela API.
- **Model:** representa as entidades e os conceitos do domínio.
- **Exception handling:** organiza o tratamento de erros e respostas de validação.

O Spring Boot também serve os arquivos estáticos de `src/main/resources/static`; o JavaScript da interface consome os endpoints da API.

> O repositório também contém classes de estudo anteriores, como `Main`, `Hotel`, `Recepcao` e `RH`, que usam listas em memória. Elas são exercícios separados do fluxo atual com Spring Data JPA.

## Endpoints disponíveis

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/hotel` | Verifica se a aplicação responde |
| `GET` | `/hospedes` | Lista hóspedes |
| `GET` | `/hospedes/{documento}` | Busca hóspede pelo documento |
| `POST` | `/hospedes` | Cadastra hóspede |
| `GET` | `/quartos` | Lista quartos |
| `POST` | `/quartos` | Cadastra quarto |
| `GET` | `/reservas` | Lista reservas |
| `GET` | `/reservas/{data}` | Busca reservas que incluem a data (`yyyy-MM-dd`) |
| `POST` | `/reservas` | Cria reserva |
| `DELETE` | `/reservas/{quartoId}/{data}` | Cancela reserva localizada pelo quarto e data |

Para criar uma reserva, `POST /reservas` recebe `hospedeId`, `quartoId`, `dataCheckIn`, `dataCheckOut` e, opcionalmente, `observacao`. As datas usam o formato `yyyy-MM-dd`, e o check-out deve ser posterior ao check-in.

A busca por data considera o check-in como parte da estadia e o check-out como o primeiro dia fora dela.

## Executar localmente

### Pré-requisitos

- JDK 25
- Maven
- PostgreSQL

### 1. Clonar o repositório

```bash
git clone https://github.com/MarcosEduardoDev/Velune.git
cd Velune
```

### 2. Preparar o banco de dados

Crie um banco PostgreSQL chamado `Velune`. **Atenção:** neste momento, o projeto ainda não inclui migrações nem um script completo para criar o schema. As tabelas de hóspedes, quartos e reservas precisam existir e corresponder às entidades JPA antes de iniciar a aplicação.

### 3. Configurar a conexão

No PowerShell, defina as variáveis de ambiente para a sessão atual do terminal:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/Velune"
$env:SPRING_DATASOURCE_USERNAME = "seu_usuario"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha"
```

Use suas próprias credenciais e nunca versione senhas reais.

### 4. Iniciar a aplicação

```bash
mvn spring-boot:run
```

Com o banco e o schema configurados, acesse [http://localhost:8080](http://localhost:8080).

## Estado atual e próximos passos

O projeto está em construção. As limitações conhecidas incluem:

- A interface de reservas ainda não usa a busca por data disponível na API.
- O cancelamento atual identifica uma reserva por quarto e data, em vez de selecionar diretamente uma reserva específica.
- A seção de equipe e alguns indicadores do painel são demonstrações visuais sem API ligada ao banco.
- Ainda não há autenticação, autorização ou migrações do banco de dados.

Próximas melhorias:
- Tornar o cancelamento direcionado a uma reserva específica.
- Conectar a busca por data à interface.
- Adicionar scripts de schema ou uma ferramenta de migração.
- Expandir os testes automatizados.
- Evoluir o módulo de equipe e substituir os dados de demonstração.

---

<p align="center">
  Projeto pessoal de aprendizado prático em <strong>Java Backend e Spring Boot</strong>.
</p>
