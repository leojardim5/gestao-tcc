# Sistema de Gestão de TCCs - Guia de Execução

## 🚀 Execução Rápida

### Opção 1: Script Automático (Recomendado)
```bash
# Execute no diretório raiz do projeto
start-system.bat
```

### Opção 2: Execução Manual

#### Backend (Spring Boot)
```bash
cd backend
start-backend.bat
# ou manualmente:
java -jar target\gestaotcc-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Frontend (Next.js)
```bash
cd frontend
start-frontend.bat
# ou manualmente:
"C:\Program Files\nodejs\node.exe" ".\node_modules\.bin\next" dev
```

## 📋 Pré-requisitos

### Software Necessário
- **Java 17+** - Para o backend Spring Boot
- **Node.js 18+** - Para o frontend Next.js
- **PostgreSQL** - Banco de dados (porta 5432)

### Configuração do Banco
- **Host:** localhost:5432
- **Database:** gestaotcc_db
- **Username:** postgres
- **Password:** 1234

## 🔧 URLs de Acesso

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8081
- **Swagger UI:** http://localhost:8081/swagger-ui.html

## 🐛 Solução de Problemas

### Backend não inicia
1. Verifique se o PostgreSQL está rodando
2. Confirme as credenciais do banco em `backend/src/main/resources/application-dev.yml`
3. Execute `mvn clean package` no diretório backend

### Frontend não inicia
1. Execute `npm install` no diretório frontend
2. Verifique se o Node.js está no PATH
3. Tente usar o caminho completo: `"C:\Program Files\nodejs\node.exe"`

### Problemas de CORS
- O backend já está configurado para aceitar requisições do frontend
- Verifique se as URLs estão corretas no arquivo `frontend/src/services/api.ts`

## 📁 Estrutura do Projeto

```
gestao-tcc/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Código Java
│   ├── src/main/resources/ # Configurações e migrations
│   └── target/             # JAR compilado
├── frontend/               # Next.js App
│   ├── src/app/           # Páginas
│   ├── src/components/    # Componentes React
│   └── src/services/      # Chamadas à API
└── start-system.bat       # Script de execução
```

## 🎯 Funcionalidades Implementadas

### Backend
- ✅ CRUD de Usuários (Alunos, Orientadores, Coordenadores)
- ✅ CRUD de TCCs
- ✅ Sistema de Submissões
- ✅ Comentários e Feedback
- ✅ Reuniões
- ✅ Notificações
- ✅ Swagger/OpenAPI

### Frontend
- ✅ Interface responsiva com Tailwind CSS
- ✅ Páginas de login, dashboard, TCCs
- ✅ Formulários com validação
- ✅ Integração com API via Axios
- ✅ Gerenciamento de estado com Zustand

## 🔄 Próximos Passos

1. **Segurança:** Implementar JWT e autenticação real
2. **Testes:** Adicionar testes unitários e de integração
3. **Deploy:** Configurar Docker e deploy em produção
4. **Relatórios:** Implementar geração de relatórios PDF
5. **Notificações:** Sistema de email/SMS

## 📞 Suporte

Em caso de problemas:
1. Verifique os logs do backend no console
2. Abra o DevTools do navegador para ver erros do frontend
3. Confirme se todos os serviços estão rodando nas portas corretas
