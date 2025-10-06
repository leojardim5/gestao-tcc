# Gestão de TCC - Backend

Este é o backend do sistema de Gestão de TCCs, construído com Spring Boot 3, PostgreSQL, MapStruct, Lombok e OpenAPI.

## Pré-requisitos

Antes de começar, certifique-se de ter os seguintes softwares instalados:

*   **Java Development Kit (JDK) 17**
*   **Apache Maven 3.x**
*   **PostgreSQL 15+** (ou Docker para rodar o PostgreSQL em um container)

## Configuração do Banco de Dados

O projeto utiliza PostgreSQL. Para o ambiente de desenvolvimento, você pode configurar um banco de dados local.

### `application-dev.yml`

O arquivo `src/main/resources/application-dev.yml` contém as configurações para o perfil de desenvolvimento. Certifique-se de que as credenciais do seu PostgreSQL local estejam corretas.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gestaotcc_db
    username: postgres
    password: 1234
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update # Temporariamente 'update' para desenvolvimento sem Flyway. Mudar para 'validate' com Flyway.
    show-sql: true
  flyway:
    enabled: false # Temporariamente desabilitado
```

**Nota:** Para este estágio inicial, o Flyway está desabilitado e `ddl-auto` está como `update` para que o Hibernate crie o esquema do banco de dados automaticamente. Em versões futuras, o Flyway será habilitado para gerenciar as migrações de schema.

## Como Rodar a Aplicação

1.  **Clone o repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd gestao-tcc/backend
    ```

2.  **Crie o banco de dados:**
    Certifique-se de que um banco de dados PostgreSQL chamado `gestaotcc_db` exista e que o usuário `postgres` com a senha `1234` (ou as credenciais que você configurou) tenha acesso a ele.

3.  **Compile e execute a aplicação:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

    A aplicação será iniciada na porta padrão 8080.

## Documentação da API (Swagger UI)

Após iniciar a aplicação, a documentação interativa da API estará disponível em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Você pode usar esta interface para explorar os endpoints, enviar requisições e testar a API.

## Exemplos de Requisições (cURL)

Aqui estão alguns exemplos de como interagir com a API usando `cURL`.

### Usuários

#### Criar um novo usuário (ALUNO)

```bash
curl -X POST "http://localhost:8080/api/usuarios" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "nome": "Ana Silva",
           "email": "ana.silva@example.com",
           "senha": "senhaSegura123",
           "papel": "ALUNO"
         }'
```

#### Criar um novo usuário (ORIENTADOR)

```bash
curl -X POST "http://localhost:8080/api/usuarios" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "nome": "Prof. Carlos",
           "email": "carlos.prof@example.com",
           "senha": "senhaSegura123",
           "papel": "ORIENTADOR"
         }'
```

#### Listar todos os usuários

```bash
curl -X GET "http://localhost:8080/api/usuarios?page=0&size=10"
```

#### Buscar usuário por ID

```bash
curl -X GET "http://localhost:8080/api/usuarios/{id_do_usuario}"
```

### TCCs

#### Criar um novo TCC

```bash
curl -X POST "http://localhost:8080/api/tccs" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "alunoId": "UUID_DO_ALUNO",
           "orientadorId": "UUID_DO_ORIENTADOR",
           "titulo": "Análise de Desempenho de Aplicações Spring Boot",
           "resumo": "Este trabalho investiga...",
           "dataInicio": "2025-03-01",
           "dataEntregaPrevista": "2025-06-30"
         }'
```

#### Listar TCCs por orientador

```bash
curl -X GET "http://localhost:8080/api/tccs?orientadorId=UUID_DO_ORIENTADOR&page=0&size=10"
```

### Submissões

#### Criar uma nova submissão

```bash
curl -X POST "http://localhost:8080/api/submissoes" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "tccId": "UUID_DO_TCC",
           "arquivoUrl": "https://example.com/tcc/documento_v1.pdf"
         }'
```

#### Decidir uma submissão

```bash
curl -X PATCH "http://localhost:8080/api/submissoes/{id_da_submissao}/decisao" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "status": "APROVADO"
         }'
```

### Reuniões

#### Agendar uma reunião

```bash
curl -X POST "http://localhost:8080/api/reunioes" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "tccId": "UUID_DO_TCC",
           "dataHora": "2025-06-02T14:00:00Z",
           "tema": "Reunião de Alinhamento",
           "resumo": "Discussão sobre o progresso do TCC.",
           "tipo": "ONLINE"
         }'
```

### Comentários

#### Adicionar um comentário

```bash
curl -X POST "http://localhost:8080/api/comentarios" \
     -H "Content-Type: application/json" \
     -d 
'{'
           "submissaoId": "UUID_DA_SUBMISSAO",
           "autorId": "UUID_DO_AUTOR",
           "texto": "Excelente trabalho na introdução, mas revisar a metodologia."
         }'
```

### Notificações

#### Listar notificações de um usuário

```bash
curl -X GET "http://localhost:8080/api/notificacoes?usuarioId=UUID_DO_USUARIO&lidas=false"
```

#### Marcar notificação como lida

```bash
curl -X PATCH "http://localhost:8080/api/notificacoes/{id_da_notificacao}/lida"
```

## Próximos Passos

*   Implementação das migrations com Flyway.
*   Adição de segurança (Spring Security com JWT).
*   Testes unitários e de integração abrangentes.
*   Implementação de seeds de desenvolvimento.
