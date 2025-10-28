# 🎓 Sistema de Gestão de TCCs (Trabalhos de Conclusão de Curso)

Sistema completo para gerenciamento de Trabalhos de Conclusão de Curso, desenvolvido com Spring Boot e Next.js, incluindo autenticação JWT, sistema de convites de orientação e notificações em tempo real.

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Versões do Projeto](#-versões-do-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Funcionalidades](#-funcionalidades)
- [Instalação e Execução](#-instalação-e-execução)
- [Documentação da API](#-documentação-da-api)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Segurança](#-segurança)
- [Contribuição](#-contribuição)

## 🎯 Visão Geral

O Sistema de Gestão de TCCs é uma aplicação web completa que facilita o processo de orientação acadêmica, permitindo que alunos solicitem orientação de professores, gerenciem seus trabalhos e acompanhem o progresso através de um sistema integrado de notificações.

### Características Principais

- **🔐 Autenticação JWT** - Sistema seguro de login e autorização
- **👥 Gestão de Usuários** - Alunos, Orientadores e Coordenadores
- **📝 Gestão de TCCs** - Criação, edição e acompanhamento de trabalhos
- **🤝 Sistema de Convites** - Alunos podem solicitar orientação
- **🔔 Notificações** - Sistema em tempo real com WebSocket
- **📊 Dashboard** - Visão geral do progresso
- **🔒 Controle de Acesso** - Cada usuário vê apenas seus dados

## 🚀 Versões do Projeto

### 📅 Versão 1.0 - Sistema Base de Usuários
**Funcionalidades Implementadas:**
- ✅ Sistema de autenticação e cadastro
- ✅ Gestão de usuários (Alunos, Orientadores, Coordenadores)
- ✅ Switch de disponibilidade para orientadores
- ✅ Interface responsiva com Tailwind CSS
- ✅ Integração frontend-backend básica

**Tecnologias:** Spring Boot, Next.js, PostgreSQL, JWT

### 📅 Versão 2.0 - Sistema de Convites e TCCs
**Funcionalidades Implementadas:**
- ✅ Criação e gestão de TCCs
- ✅ Sistema de convites de orientação
- ✅ Status de TCC (Rascunho, Pendente Aprovação, Em Andamento, etc.)
- ✅ Notificações em tempo real
- ✅ Badge de notificações na sidebar
- ✅ Autorização baseada em roles
- ✅ CORS configurado
- ✅ Deletar TCC quando orientador recusa

**Melhorias de Segurança:**
- ✅ Filtros de autorização por usuário
- ✅ Validação de permissões
- ✅ Proteção contra acesso não autorizado

## 🛠 Tecnologias

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.x** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **Flyway** - Migração de banco
- **JWT** - Tokens de autenticação
- **MapStruct** - Mapeamento de objetos
- **Swagger/OpenAPI** - Documentação da API
- **WebSocket** - Notificações em tempo real

### Frontend
- **Next.js 14** - Framework React
- **TypeScript** - Tipagem estática
- **Tailwind CSS** - Estilização
- **Axios** - Cliente HTTP
- **Zustand** - Gerenciamento de estado
- **React Query** - Cache e sincronização
- **React Hook Form** - Formulários

### DevOps
- **Maven** - Gerenciamento de dependências
- **Docker** - Containerização
- **Git** - Controle de versão

## 🏗 Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend       │    │   Database       │
│   (Next.js)     │◄──►│  (Spring Boot)   │◄──►│  (PostgreSQL)    │
│   Port: 3000    │    │   Port: 8081     │    │   Port: 5432     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │
         │              ┌─────────────────┐
         └──────────────►│   WebSocket     │
                        │  (Notificações) │
                        └─────────────────┘
```

### Padrões Arquiteturais

- **MVC** - Model-View-Controller no backend
- **Repository Pattern** - Acesso a dados
- **DTO Pattern** - Transferência de objetos
- **Service Layer** - Lógica de negócio
- **Component Pattern** - Frontend React

## ⚡ Funcionalidades

### 🔐 Autenticação e Autorização
- Login com email e senha
- Geração de tokens JWT
- Refresh tokens
- Controle de acesso baseado em roles
- Proteção de rotas

### 👥 Gestão de Usuários
- **Alunos**: Podem criar TCCs e solicitar orientação
- **Orientadores**: Podem aceitar/rejeitar convites e orientar TCCs
- **Coordenadores**: Acesso total ao sistema
- Switch de disponibilidade para orientadores

### 📝 Gestão de TCCs
- Criação de TCCs com título, resumo e tema
- Status automático baseado no fluxo
- Associação com orientadores
- Histórico de alterações

### 🤝 Sistema de Convites
- Alunos enviam convites para orientadores
- Orientadores podem aceitar ou rejeitar
- Notificações automáticas
- Status do TCC atualizado automaticamente

### 🔔 Notificações
- Sistema em tempo real com WebSocket
- Badge de contagem na sidebar
- Notificações para convites pendentes
- Notificações gerais do sistema

### 📊 Dashboard
- Visão geral para cada tipo de usuário
- Estatísticas de TCCs
- Convites pendentes
- Notificações não lidas

## 🚀 Instalação e Execução

### Pré-requisitos

- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 13+**
- **Maven 3.6+**

### Configuração do Banco de Dados

```sql
-- Criar banco de dados
CREATE DATABASE gestaotcc_db;

-- Configurações
Host: localhost:5432
Database: gestaotcc_db
Username: postgres
Password: 1234
```

### Execução Rápida

```bash
# Clone o repositório
git clone <repository-url>
cd gestao-tcc

# Execute o sistema completo
start-system.bat
```

### Execução Manual

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

### URLs de Acesso

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html

## 📚 Documentação da API

### Endpoints Principais

#### Autenticação
```
POST /api/auth/login          # Login
POST /api/auth/register       # Cadastro
```

#### Usuários
```
GET    /api/usuarios          # Listar usuários
POST   /api/usuarios          # Criar usuário
PUT    /api/usuarios/{id}     # Atualizar usuário
DELETE /api/usuarios/{id}     # Deletar usuário
```

#### TCCs
```
GET    /api/tccs              # Listar TCCs (filtrado por usuário)
POST   /api/tccs              # Criar TCC
PUT    /api/tccs/{id}         # Atualizar TCC
GET    /api/tccs/{id}         # Obter TCC específico
```

#### Convites
```
POST   /api/convites/aluno/{id}                    # Enviar convite
PUT    /api/convites/orientador/{id}/responder    # Responder convite
GET    /api/convites/orientador/{id}/pendentes    # Listar convites pendentes
```

#### Notificações
```
GET    /api/notification-badge/user/{id}/count    # Contar notificações
GET    /api/notifications/user/{id}               # Listar notificações
```

### Exemplos de Uso

#### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "aluno@teste.com",
    "senha": "password"
  }'
```

#### Criar TCC
```bash
curl -X POST http://localhost:8081/api/tccs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "titulo": "Meu TCC",
    "resumo": "Resumo do trabalho",
    "tema": "Tecnologia",
    "curso": "Ciência da Computação"
  }'
```

## 📁 Estrutura do Projeto

```
gestao-tcc/
├── 📁 backend/                          # Spring Boot API
│   ├── 📁 src/main/java/com/leonardo/gestaotcc/
│   │   ├── 📁 config/                   # Configurações
│   │   │   ├── CorsConfiguration.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebSocketConfig.java
│   │   ├── 📁 controller/               # Controllers REST
│   │   │   ├── AuthController.java
│   │   │   ├── TccController.java
│   │   │   ├── ConviteOrientacaoController.java
│   │   │   └── NotificationBadgeController.java
│   │   ├── 📁 dto/                      # Data Transfer Objects
│   │   │   ├── TccDto.java
│   │   │   ├── UsuarioDto.java
│   │   │   └── auth/
│   │   ├── 📁 entity/                   # Entidades JPA
│   │   │   ├── Usuario.java
│   │   │   ├── Tcc.java
│   │   │   └── ConviteOrientacao.java
│   │   ├── 📁 enums/                    # Enumeradores
│   │   │   ├── PapelUsuario.java
│   │   │   ├── StatusTcc.java
│   │   │   └── StatusConvite.java
│   │   ├── 📁 repository/               # Repositórios JPA
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── TccRepository.java
│   │   │   └── ConviteOrientacaoRepository.java
│   │   ├── 📁 service/                  # Serviços de negócio
│   │   │   ├── AuthService.java
│   │   │   ├── TccServiceImpl.java
│   │   │   └── ConviteOrientacaoService.java
│   │   ├── 📁 security/                 # Segurança
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtService.java
│   │   │   └── CustomUserDetails.java
│   │   └── 📁 mapper/                   # Mapeadores MapStruct
│   │       ├── TccMapper.java
│   │       ├── UsuarioMapper.java
│   │       └── ConviteOrientacaoMapper.java
│   ├── 📁 src/main/resources/
│   │   ├── application.properties       # Configurações
│   │   └── 📁 db/migration/            # Migrações Flyway
│   │       ├── V1__usuarios_enums.sql
│   │       ├── V2__tcc_core.sql
│   │       └── V11__recreate_users.sql
│   └── pom.xml                          # Dependências Maven
├── 📁 frontend/                         # Next.js App
│   ├── 📁 src/
│   │   ├── 📁 app/                      # Páginas Next.js
│   │   │   ├── login/page.tsx
│   │   │   ├── dashboard/page.tsx
│   │   │   ├── tccs/page.tsx
│   │   │   └── orientador/page.tsx
│   │   ├── 📁 components/              # Componentes React
│   │   │   ├── 📁 layout/
│   │   │   │   ├── AppLayout.tsx
│   │   │   │   ├── Header.tsx
│   │   │   │   └── Sidebar.tsx
│   │   │   ├── 📁 ui/                   # Componentes UI
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Input.tsx
│   │   │   │   └── Badge.tsx
│   │   │   └── 📁 tccs/
│   │   │       ├── TccForm.tsx
│   │   │       └── TccTable.tsx
│   │   ├── 📁 services/                # Serviços API
│   │   │   ├── api.ts
│   │   │   ├── auth.ts
│   │   │   ├── tccs.ts
│   │   │   └── convites.ts
│   │   ├── 📁 hooks/                    # Custom Hooks
│   │   │   ├── useAuth.ts
│   │   │   └── useNotifications.ts
│   │   ├── 📁 store/                    # Estado Zustand
│   │   │   ├── authStore.ts
│   │   │   └── notificationStore.ts
│   │   └── 📁 types/                    # Tipos TypeScript
│   │       └── index.ts
│   ├── package.json                     # Dependências NPM
│   └── tailwind.config.js               # Configuração Tailwind
├── 📄 docker-compose.yml                # Docker Compose
├── 📄 start-system.bat                  # Script de execução
└── 📄 README.md                         # Este arquivo
```

## 🔒 Segurança

### Autenticação JWT
- Tokens com expiração configurável
- Refresh tokens para renovação
- Validação de assinatura
- Blacklist de tokens inválidos

### Autorização
- Controle de acesso baseado em roles
- Filtros de autorização por endpoint
- Validação de propriedade de recursos
- Proteção contra acesso não autorizado

### CORS
- Configuração específica para frontend
- Headers de segurança
- Validação de origem

### Validação de Dados
- Validação de entrada com Bean Validation
- Sanitização de dados
- Proteção contra SQL Injection (JPA)
- Validação de tipos e formatos

## 🧪 Testes

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## 🚀 Deploy

### Docker
```bash
# Build das imagens
docker-compose build

# Executar containers
docker-compose up -d
```

### Produção
- Configurar variáveis de ambiente
- Usar banco de dados de produção
- Configurar HTTPS
- Implementar monitoramento

## 🤝 Contribuição

### Como Contribuir
1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código
- **Backend**: Java 17, Spring Boot conventions
- **Frontend**: TypeScript, ESLint, Prettier
- **Commits**: Conventional Commits
- **Documentação**: JSDoc para funções complexas

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique a documentação da API no Swagger
2. Consulte os logs do backend
3. Abra uma issue no repositório
4. Entre em contato com a equipe de desenvolvimento

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

**Desenvolvido com ❤️ para facilitar a gestão de TCCs acadêmicos**
