# Documentação Técnica - Sistema de Gestão de TCCs

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura do Sistema](#arquitetura-do-sistema)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Banco de Dados](#banco-de-dados)
6. [Backend (Spring Boot)](#backend-spring-boot)
7. [Frontend (Next.js)](#frontend-nextjs)
8. [APIs e Endpoints](#apis-e-endpoints)
9. [Autenticação e Segurança](#autenticação-e-segurança)
10. [Deploy e Infraestrutura](#deploy-e-infraestrutura)
11. [Monitoramento e Logs](#monitoramento-e-logs)
12. [Testes](#testes)
13. [Manutenção e Backup](#manutenção-e-backup)

## 🎯 Visão Geral

O Sistema de Gestão de TCCs é uma aplicação web completa que centraliza e organiza todo o ciclo de vida de Trabalhos de Conclusão de Curso, integrando alunos, orientadores e coordenação em um ambiente único e colaborativo.

### Funcionalidades Principais
- ✅ Gestão completa de usuários (Alunos, Orientadores, Coordenadores)
- ✅ Sistema de autenticação JWT com refresh tokens
- ✅ Criação e acompanhamento de TCCs
- ✅ Sistema de convites de orientação
- ✅ Controle de status de TCC (Rascunho, Pendente Aprovação, Em Andamento, etc.)
- ✅ Sistema de submissões com upload de arquivos
- ✅ Comentários e feedback em tempo real
- ✅ Agendamento de reuniões
- ✅ Notificações automáticas com WebSocket
- ✅ Badge de notificações na sidebar
- ✅ Dashboard com estatísticas
- ✅ Relatórios em PDF
- ✅ Autorização baseada em roles
- ✅ CORS configurado
- ✅ Deletar TCC quando orientador recusa convite

## 🏗️ Arquitetura do Sistema

### Arquitetura em 3 Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                   │
│                     (Next.js + React)                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Login     │ │  Dashboard  │ │   TCCs      │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Submissões  │ │ Reuniões    │ │ Relatórios  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE NEGÓCIO                        │
│                    (Spring Boot)                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Controllers │ │  Services   │ │  Security   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Mappers   │ │ Validators  │ │ WebSocket   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE DADOS                          │
│                   (PostgreSQL)                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Usuários  │ │    TCCs     │ │ Submissões  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Comentários │ │  Reuniões   │ │Notificações │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.3.0** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Spring WebSocket** - Comunicação em tempo real
- **JWT** - Tokens de autenticação
- **Flyway** - Migração de banco de dados
- **MapStruct** - Mapeamento de objetos
- **Lombok** - Redução de boilerplate
- **Swagger/OpenAPI** - Documentação de APIs

### Frontend
- **Next.js 14** - Framework React
- **React 18** - Biblioteca de interface
- **TypeScript** - Tipagem estática
- **Tailwind CSS** - Framework CSS
- **React Hook Form** - Gerenciamento de formulários
- **Zod** - Validação de schemas
- **Axios** - Cliente HTTP
- **React Query** - Gerenciamento de estado servidor
- **Zustand** - Gerenciamento de estado cliente
- **Radix UI** - Componentes acessíveis

### Banco de Dados
- **PostgreSQL 15** - Banco de dados relacional
- **Flyway** - Controle de versão do banco

### Infraestrutura
- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers
- **Nginx** - Servidor web e proxy reverso

## 📁 Estrutura do Projeto

```
gestao-tcc/
├── backend/                          # Backend Spring Boot
│   ├── src/main/java/com/leonardo/gestaotcc/
│   │   ├── config/                   # Configurações
│   │   ├── controller/               # Controllers REST
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── entity/                   # Entidades JPA
│   │   ├── enums/                    # Enumerações
│   │   ├── exception/                # Tratamento de exceções
│   │   ├── mapper/                   # Mapeadores MapStruct
│   │   ├── repository/               # Repositórios JPA
│   │   ├── security/                 # Configurações de segurança
│   │   └── service/                  # Lógica de negócio
│   ├── src/main/resources/
│   │   ├── db/migration/             # Scripts Flyway
│   │   └── application*.yml          # Configurações
│   └── Dockerfile                    # Imagem Docker
├── frontend/                         # Frontend Next.js
│   ├── src/
│   │   ├── app/                      # Páginas (App Router)
│   │   ├── components/               # Componentes React
│   │   ├── hooks/                    # Hooks customizados
│   │   ├── interfaces/               # Tipos TypeScript
│   │   ├── services/                 # Serviços de API
│   │   ├── store/                    # Estado global
│   │   └── utils/                    # Utilitários
│   └── Dockerfile                    # Imagem Docker
├── docker-compose.yml                # Orquestração
├── deploy.sh                         # Script de deploy
├── backup-restore.sh                 # Script de backup
└── README-EXECUCAO.md                # Guia de execução
```

## 🗄️ Banco de Dados

### Modelo de Dados

```sql
-- Usuários
usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    papel ENUM('ALUNO', 'ORIENTADOR', 'COORDENADOR'),
    ativo BOOLEAN DEFAULT true,
    criado_em TIMESTAMP,
    atualizado_em TIMESTAMP
)

-- TCCs
tccs (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    resumo TEXT NOT NULL,
    status ENUM('RASCUNHO', 'EM_ANDAMENTO', 'AGUARDANDO_DEFESA', 'CONCLUIDO'),
    data_inicio DATE,
    data_entrega_prevista DATE,
    aluno_id UUID REFERENCES usuarios(id),
    orientador_id UUID REFERENCES usuarios(id),
    coorientador_id UUID REFERENCES usuarios(id),
    criado_em TIMESTAMP,
    atualizado_em TIMESTAMP
)

-- Submissões
submissoes (
    id UUID PRIMARY KEY,
    tcc_id UUID REFERENCES tccs(id),
    versao INTEGER NOT NULL,
    arquivo_url VARCHAR(255) NOT NULL,
    status ENUM('EM_REVISAO', 'APROVADO', 'REPROVADO'),
    enviado_em TIMESTAMP
)

-- Comentários
comentarios (
    id UUID PRIMARY KEY,
    submissao_id UUID REFERENCES submissoes(id),
    autor_id UUID REFERENCES usuarios(id),
    texto TEXT NOT NULL,
    criado_em TIMESTAMP
)

-- Reuniões
reunioes (
    id UUID PRIMARY KEY,
    tcc_id UUID REFERENCES tccs(id),
    data_hora TIMESTAMP NOT NULL,
    tema VARCHAR(255) NOT NULL,
    resumo TEXT,
    tipo ENUM('PRESENCIAL', 'ONLINE')
)

-- Convites de Orientação
convites_orientacao (
    id UUID PRIMARY KEY,
    aluno_id UUID REFERENCES usuarios(id),
    orientador_id UUID REFERENCES usuarios(id),
    tcc_id UUID REFERENCES tccs(id),
    mensagem TEXT NOT NULL,
    status ENUM('PENDENTE', 'ACEITO', 'REJEITADO'),
    data_envio TIMESTAMP,
    data_resposta TIMESTAMP
)

-- Notificações
notificacoes (
    id UUID PRIMARY KEY,
    usuario_id UUID REFERENCES usuarios(id),
    tipo ENUM('PRAZO', 'REUNIAO', 'COMENTARIO', 'SISTEMA', 'CONVITE_ORIENTACAO'),
    mensagem VARCHAR(255) NOT NULL,
    lida BOOLEAN DEFAULT false,
    criada_em TIMESTAMP
)
```

### Migrations (Flyway)

1. **V1__usuarios_enums.sql** - Criação de ENUMs e tabela de usuários
2. **V2__tcc_core.sql** - Tabelas principais (TCCs, submissões, reuniões, comentários, notificações)
3. **V3__views_indices.sql** - Views e índices para performance
4. **V4__seed_minimo.sql** - Dados iniciais para desenvolvimento

## 🔧 Backend (Spring Boot)

### Estrutura de Camadas

#### Controllers
- `AuthController` - Autenticação e registro
- `UsuarioController` - CRUD de usuários
- `TccController` - CRUD de TCCs
- `ConviteOrientacaoController` - Sistema de convites de orientação
- `NotificationBadgeController` - Badge de notificações
- `SubmissaoController` - Gerenciamento de submissões
- `ComentarioController` - Sistema de comentários
- `ReuniaoController` - Agendamento de reuniões
- `NotificacaoController` - Notificações
- `DashboardController` - Estatísticas
- `ReportController` - Relatórios
- `FileUploadController` - Upload de arquivos

#### Services
- `AuthService` - Lógica de autenticação
- `UsuarioService` - Lógica de usuários
- `TccService` - Lógica de TCCs
- `SubmissaoService` - Lógica de submissões
- `NotificationService` - Notificações em tempo real
- `DashboardService` - Estatísticas
- `ReportService` - Geração de relatórios
- `FileStorageService` - Gerenciamento de arquivos

#### Security
- `SecurityConfig` - Configuração de segurança
- `JwtService` - Geração e validação de JWT
- `JwtAuthenticationFilter` - Filtro de autenticação
- `CustomUserDetailsService` - Serviço de usuários

### Configurações

#### application.yml
```yaml
spring:
  application:
    name: gestao-tcc
  datasource:
    url: jdbc:postgresql://localhost:5432/gestaotcc_db
    username: postgres
    password: 1234
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8081

jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000
```

## 🎨 Frontend (Next.js)

### Estrutura de Componentes

#### Layout
- `AppLayout` - Layout principal
- `Header` - Cabeçalho com navegação
- `Sidebar` - Menu lateral

#### Páginas
- `/login` - Página de login
- `/dashboard` - Dashboard principal
- `/tccs` - Lista de TCCs
- `/tccs/[id]` - Detalhes do TCC
- `/submissoes` - Submissões
- `/reunioes` - Reuniões
- `/usuarios` - Usuários
- `/notificacoes` - Notificações

#### Componentes
- `TccForm` - Formulário de TCC
- `TccTable` - Tabela de TCCs
- `SubmissoesTab` - Aba de submissões
- `ComentariosTab` - Aba de comentários
- `ReunioesTab` - Aba de reuniões

### Estado Global (Zustand)

```typescript
// store/session.ts
interface SessionStore {
  user: Usuario | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

// store/tcc.ts
interface TccStore {
  tccs: Tcc[];
  loading: boolean;
  fetchTccs: () => Promise<void>;
  createTcc: (tcc: CreateTccRequest) => Promise<void>;
}
```

### Serviços de API

```typescript
// services/api.ts
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptors para autenticação
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## 🔌 APIs e Endpoints

### Autenticação
```
POST /api/auth/login          # Login
POST /api/auth/register       # Registro
```

### Usuários
```
GET    /api/usuarios          # Listar usuários
POST   /api/usuarios          # Criar usuário
GET    /api/usuarios/{id}     # Buscar usuário
PUT    /api/usuarios/{id}     # Atualizar usuário
PATCH  /api/usuarios/{id}     # Desativar usuário
```

### TCCs
```
GET    /api/tccs              # Listar TCCs (filtrado por usuário)
POST   /api/tccs              # Criar TCC
GET    /api/tccs/{id}         # Buscar TCC (com autorização)
PUT    /api/tccs/{id}         # Atualizar TCC
PATCH  /api/tccs/{id}/status  # Alterar status
```

### Convites de Orientação
```
POST   /api/convites/aluno/{id}                           # Enviar convite
PUT    /api/convites/orientador/{id}/convite/{id}/responder # Responder convite
GET    /api/convites/orientador/{id}/pendentes           # Listar convites pendentes
GET    /api/convites/orientador/{id}/pendentes/count     # Contar convites pendentes
GET    /api/convites/aluno/{id}                          # Listar convites do aluno
```

### Badge de Notificações
```
GET    /api/notification-badge/user/{id}/count    # Contar notificações totais
GET    /api/notification-badge/user/{id}/total    # Detalhes das notificações
```

### Submissões
```
GET    /api/submissoes?tccId={id}  # Listar submissões
POST   /api/submissoes             # Criar submissão
POST   /api/submissoes/upload      # Upload de arquivo
PATCH  /api/submissoes/{id}/decisao # Decidir submissão
```

### Dashboard
```
GET    /api/dashboard/stats              # Estatísticas gerais
GET    /api/dashboard/tccs-by-status     # TCCs por status
GET    /api/dashboard/submissoes-by-status # Submissões por status
```

### Relatórios
```
GET    /api/reports/tccs        # Relatório de TCCs
GET    /api/reports/submissoes  # Relatório de submissões
GET    /api/reports/usuarios    # Relatório de usuários
```

### WebSocket
```
/ws                           # Endpoint WebSocket
/topic/global                 # Mensagens globais
/queue/notifications          # Notificações por usuário
```

## 🔐 Autenticação e Segurança

### JWT (JSON Web Tokens)

#### Configuração
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

#### Geração de Token
```java
@Service
public class JwtService {
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
```

### CORS
```java
@Configuration
public class CorsConfiguration {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## 🚀 Deploy e Infraestrutura

### Docker Compose

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: gestaotcc_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://postgres:5432/gestaotcc_db
    ports:
      - "8081:8081"
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - backend
```

### Scripts de Deploy

#### deploy.sh
```bash
#!/bin/bash
echo "Iniciando deploy..."
docker-compose down
docker-compose up --build -d
echo "Deploy concluído!"
```

#### backup-restore.sh
```bash
#!/bin/bash
# Backup
docker-compose exec -T postgres pg_dump -U postgres gestaotcc_db > backup.sql

# Restore
docker-compose exec -T postgres psql -U postgres -d gestaotcc_db < backup.sql
```

## 📊 Monitoramento e Logs

### Logs Estruturados
```java
@Slf4j
@Service
public class TccService {
    public TccDto createTcc(CreateTccRequest request) {
        log.info("Criando TCC: {}", request.getTitulo());
        try {
            Tcc tcc = tccRepository.save(tcc);
            log.info("TCC criado com sucesso: {}", tcc.getId());
            return tccMapper.toDto(tcc);
        } catch (Exception e) {
            log.error("Erro ao criar TCC: {}", e.getMessage());
            throw e;
        }
    }
}
```

### Health Checks
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            // Verificar conexão com banco
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

## 🧪 Testes

### Testes Unitários (Backend)
```java
@ExtendWith(MockitoExtension.class)
class TccServiceTest {
    @Mock
    private TccRepository tccRepository;
    
    @InjectMocks
    private TccService tccService;
    
    @Test
    void shouldCreateTccSuccessfully() {
        // Given
        CreateTccRequest request = new CreateTccRequest();
        request.setTitulo("Teste TCC");
        
        // When
        TccDto result = tccService.createTcc(request);
        
        // Then
        assertThat(result.getTitulo()).isEqualTo("Teste TCC");
    }
}
```

### Testes de Integração
```java
@SpringBootTest
@AutoConfigureTestDatabase
class TccControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateTcc() {
        CreateTccRequest request = new CreateTccRequest();
        request.setTitulo("Teste TCC");
        
        ResponseEntity<TccDto> response = restTemplate.postForEntity(
            "/api/tccs", request, TccDto.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

### Testes E2E (Frontend)
```typescript
// tests/e2e/tcc.spec.ts
import { test, expect } from '@playwright/test';

test('should create TCC', async ({ page }) => {
  await page.goto('/tccs');
  await page.click('button[data-testid="create-tcc"]');
  await page.fill('input[name="titulo"]', 'Teste TCC');
  await page.fill('textarea[name="resumo"]', 'Resumo do teste');
  await page.click('button[type="submit"]');
  
  await expect(page.locator('text=Teste TCC')).toBeVisible();
});
```

## 🔧 Manutenção e Backup

### Backup Automático
```bash
# Cron job para backup diário
0 2 * * * /path/to/backup-restore.sh backup
```

### Monitoramento de Performance
```java
@Component
public class PerformanceMonitor {
    @EventListener
    public void handleRequest(RequestEvent event) {
        if (event.getDuration() > 1000) {
            log.warn("Request lento detectado: {}ms", event.getDuration());
        }
    }
}
```

### Limpeza de Dados
```java
@Scheduled(cron = "0 0 2 * * ?") // Diariamente às 2h
public void cleanupExpiredTokens() {
    // Limpar tokens expirados
}

@Scheduled(cron = "0 0 3 * * ?") // Diariamente às 3h
public void cleanupOldNotifications() {
    // Limpar notificações antigas
}
```

## 📈 Métricas e KPIs

### Métricas de Negócio
- Total de TCCs por status
- Tempo médio de conclusão
- Taxa de aprovação de submissões
- Número de reuniões por TCC
- Satisfação dos usuários

### Métricas Técnicas
- Tempo de resposta das APIs
- Uso de CPU e memória
- Disponibilidade do sistema
- Erros por endpoint
- Performance do banco de dados

## 🔄 Roadmap de Melhorias

### Versão 1.1
- [ ] Sistema de avaliação por pares
- [ ] Integração com sistemas acadêmicos
- [ ] App mobile (React Native)
- [ ] Chat em tempo real

### Versão 1.2
- [ ] IA para sugestões de melhorias
- [ ] Sistema de versionamento de documentos
- [ ] Integração com repositórios acadêmicos
- [ ] Relatórios avançados com gráficos

### Versão 2.0
- [ ] Microserviços
- [ ] Kubernetes
- [ ] CI/CD automatizado
- [ ] Monitoramento com Prometheus/Grafana

---

## 📞 Suporte e Contato

Para dúvidas técnicas ou suporte:
- **Documentação**: Este arquivo
- **Issues**: GitHub Issues
- **Email**: suporte@gestaotcc.com

---

*Documentação atualizada em: Janeiro 2025*
*Versão do sistema: 1.0.0*
