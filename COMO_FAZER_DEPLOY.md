# 🚀 COMO FAZER DEPLOY - 1 SEMANA GRATUITO

## ⚡ Railway - 5 Minutos (Mais Fácil)

---

## 📋 O QUE VOCÊ VAI FAZER (Passo a Passo):

### 1️⃣ CRIAR CONTA (1 minuto)
- Acesse: **https://railway.app**
- Clique em **"Start a New Project"**
- Faça login com **GitHub**
- Autorize Railway

### 2️⃣ CRIAR PROJETO (30 segundos)
- Clique em **"New Project"**
- Escolha **"Deploy from GitHub repo"**
- Selecione seu repositório `gestao-tcc`

### 3️⃣ ADICIONAR BANCO DE DADOS (30 segundos)
- No projeto, clique em **"+ New"** (canto inferior esquerdo)
- Selecione **"Database"** → **"Add PostgreSQL"**
- ✅ Pronto! Banco criado

### 4️⃣ DEPLOY DO BACKEND (2 minutos)

#### 4.1 Criar Serviço Backend:
- Clique em **"+ New"** novamente
- Escolha **"GitHub Repo"**
- Selecione `gestao-tcc`

#### 4.2 Configurar:
- Clique no serviço criado
- Vá em **"Settings"**
- Configure:
  ```
  Root Directory: backend
  Build Command: ./mvnw clean package -DskipTests
  Start Command: java -jar target/gestaotcc-backend-0.0.1-SNAPSHOT.jar
  ```

#### 4.3 Variáveis de Ambiente (IMPORTANTE):
- Vá em **"Variables"**
- Clique em **"+ New Variable"** e adicione:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=${{Postgres.DATABASE_URL}}
DB_USER=${{Postgres.PGUSER}}
DB_PASSWORD=${{Postgres.PGPASSWORD}}
MAIL_USERNAME=siga.tcc.notificacao@gmail.com
MAIL_PASSWORD=lqpe uflp pbsi kcki
APP_NAME=Gestão TCC
PORT=8080
```

**⚠️ IMPORTANTE**: Para `DATABASE_URL`, `DB_USER` e `DB_PASSWORD`, use exatamente `${{Postgres.DATABASE_URL}}` etc. Railway injeta automaticamente!

#### 4.4 Gerar URL do Backend:
- Vá em **"Settings"** → **"Networking"**
- Clique em **"Generate Domain"**
- **COPIE A URL** (ex: `gestao-tcc-backend-production.up.railway.app`)
- **ANOTE ESSA URL!**

### 5️⃣ DEPLOY DO FRONTEND (2 minutos)

#### 5.1 Criar Serviço Frontend:
- Clique em **"+ New"** novamente
- Escolha **"GitHub Repo"**
- Selecione `gestao-tcc`

#### 5.2 Configurar:
- Clique no serviço do frontend
- Vá em **"Settings"**
- Configure:
  ```
  Root Directory: frontend
  Build Command: npm install && npm run build
  Start Command: npm start
  ```

#### 5.3 Variável de Ambiente:
- Vá em **"Variables"**
- Adicione:

```
NEXT_PUBLIC_API_URL=https://SUA-URL-DO-BACKEND.railway.app
NODE_ENV=production
```

**⚠️ SUBSTITUA** `SUA-URL-DO-BACKEND` pela URL que você copiou no passo 4.4!

#### 5.4 Gerar URL do Frontend:
- Vá em **"Settings"** → **"Networking"**
- Clique em **"Generate Domain"**
- **COPIE A URL** (ex: `gestao-tcc-production.up.railway.app`)

### 6️⃣ ATUALIZAR CORS (1 minuto)
- Volte no serviço do **BACKEND**
- Vá em **"Variables"**
- Adicione ou edite:

```
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://SUA-URL-DO-FRONTEND.railway.app
```

**⚠️ SUBSTITUA** `SUA-URL-DO-FRONTEND` pela URL do frontend!

---

## ✅ PRONTO!

Acesse: `https://sua-url-frontend.railway.app`

---

## 🔍 VERIFICAR SE FUNCIONOU:

1. **Backend**: Acesse `https://sua-url-backend.railway.app/actuator/health`
   - Deve retornar algo (mesmo que erro 404, significa que está rodando)

2. **Frontend**: Acesse a URL do frontend
   - Deve carregar a página de login

3. **Teste Login**: Tente fazer login
   - Se funcionar, está tudo certo!

---

## 🆘 SE DER ERRO:

### Backend não conecta no banco:
- Verifique se `DATABASE_URL` está usando `${{Postgres.DATABASE_URL}}`
- Veja os logs: "Deployments" → "View Logs"

### Frontend não encontra API:
- Verifique `NEXT_PUBLIC_API_URL` está correto (com https://)
- Verifique CORS no backend
- Abra o console do navegador (F12) e veja os erros

### Build falha:
- Veja os logs em "Deployments" → "View Logs"
- Teste build local: `cd backend && ./mvnw clean package`

---

## 💰 CUSTO:

- **GRATUITO** para 1 semana! 🎉
- Railway dá $5 grátis por mês
- Para 1 semana não vai gastar nada

---

## 📝 RESUMO RÁPIDO:

1. ✅ Criar conta Railway
2. ✅ Criar projeto
3. ✅ Adicionar PostgreSQL
4. ✅ Deploy backend (com variáveis)
5. ✅ Gerar URL backend
6. ✅ Deploy frontend (com NEXT_PUBLIC_API_URL)
7. ✅ Gerar URL frontend
8. ✅ Atualizar CORS no backend
9. ✅ Testar!

---

## 🎯 DICA:

Se algo der errado, veja os **LOGS**:
- Clique no serviço
- Vá em **"Deployments"**
- Clique em **"View Logs"**
- Lá você vê todos os erros!

