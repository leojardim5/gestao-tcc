# 🚀 Deploy Rápido - 1 Semana Gratuito

## ⚡ Opção Mais Rápida: Railway (5 minutos)

### 📋 O QUE VOCÊ PRECISA FAZER:

---

## PASSO 1: Criar Conta no Railway (1 minuto)

1. Acesse: **https://railway.app**
2. Clique em **"Start a New Project"**
3. Faça login com **GitHub** (conecte sua conta)
4. Autorize o Railway a acessar seus repositórios

---

## PASSO 2: Criar Projeto e Adicionar Banco (2 minutos)

1. Clique em **"New Project"**
2. Selecione **"Deploy from GitHub repo"**
3. Escolha seu repositório `gestao-tcc`
4. Railway vai criar o projeto

### Adicionar PostgreSQL:
1. No projeto criado, clique no botão **"+ New"** (canto inferior esquerdo)
2. Selecione **"Database"**
3. Escolha **"Add PostgreSQL"**
4. ✅ Pronto! Banco criado automaticamente

---

## PASSO 3: Deploy do Backend (2 minutos)

1. No mesmo projeto, clique em **"+ New"** novamente
2. Selecione **"GitHub Repo"**
3. Escolha o mesmo repositório `gestao-tcc`
4. Railway vai detectar que é Java/Maven

### Configurar Backend:
1. Clique no serviço que foi criado (provavelmente chamado "gestao-tcc")
2. Vá na aba **"Settings"**
3. Configure:
   - **Root Directory**: `backend`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/gestaotcc-backend-0.0.1-SNAPSHOT.jar`

### Adicionar Variáveis de Ambiente (IMPORTANTE):
1. No serviço do backend, clique na aba **"Variables"**
2. Clique em **"+ New Variable"** e adicione uma por uma:

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

**IMPORTANTE**: Para `DATABASE_URL`, `DB_USER` e `DB_PASSWORD`, use os valores que começam com `${{Postgres.` - Railway injeta automaticamente!

3. Clique em **"Deploy"** ou aguarde o deploy automático

### Gerar URL do Backend:
1. No serviço do backend, vá em **"Settings"**
2. Role até **"Networking"**
3. Clique em **"Generate Domain"**
4. Copie a URL gerada (ex: `gestao-tcc-backend-production.up.railway.app`)
5. **ANOTE ESSA URL** - você vai precisar!

---

## PASSO 4: Deploy do Frontend (2 minutos)

1. No mesmo projeto Railway, clique em **"+ New"** novamente
2. Selecione **"GitHub Repo"**
3. Escolha o mesmo repositório `gestao-tcc`
4. Railway vai detectar que é Next.js

### Configurar Frontend:
1. Clique no serviço do frontend criado
2. Vá na aba **"Settings"**
3. Configure:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npm start`

### Adicionar Variável de Ambiente:
1. No serviço do frontend, clique na aba **"Variables"**
2. Clique em **"+ New Variable"**:

```
NEXT_PUBLIC_API_URL=https://SUA-URL-DO-BACKEND-AQUI.railway.app
NODE_ENV=production
```

**SUBSTITUA** `SUA-URL-DO-BACKEND-AQUI` pela URL que você copiou no Passo 3!

3. Clique em **"Deploy"** ou aguarde o deploy automático

### Gerar URL do Frontend:
1. No serviço do frontend, vá em **"Settings"**
2. Role até **"Networking"**
3. Clique em **"Generate Domain"**
4. Copie a URL gerada (ex: `gestao-tcc-production.up.railway.app`)

---

## PASSO 5: Atualizar CORS no Backend (1 minuto)

1. Volte no serviço do **backend**
2. Vá em **"Variables"**
3. Adicione ou edite a variável:

```
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://SUA-URL-DO-FRONTEND-AQUI.railway.app
```

**SUBSTITUA** `SUA-URL-DO-FRONTEND-AQUI` pela URL do frontend que você gerou!

4. O backend vai fazer redeploy automaticamente

---

## ✅ PRONTO!

Acesse: `https://sua-url-do-frontend.railway.app`

---

## 🔍 Verificar se Está Funcionando

1. **Backend**: Acesse `https://sua-url-backend.railway.app/api/auth/health` (deve retornar algo)
2. **Frontend**: Acesse a URL do frontend e tente fazer login
3. **Logs**: Veja os logs em "Deployments" → "View Logs" se algo der errado

---

## ⚠️ IMPORTANTE - Se Der Erro:

### Backend não conecta no banco:
- Verifique se `DATABASE_URL` está usando `${{Postgres.DATABASE_URL}}`
- Verifique os logs do backend

### Frontend não encontra API:
- Verifique se `NEXT_PUBLIC_API_URL` está correto (com https://)
- Verifique se CORS está configurado com a URL do frontend
- Veja o console do navegador (F12) para erros

### Build falha:
- Veja os logs em "Deployments"
- Teste build local primeiro: `cd backend && ./mvnw clean package`

---

## 💰 Custo

- **Railway**: $5 grátis por mês
- **Para 1 semana**: Totalmente grátis! 🎉

---

## 📝 Checklist Rápido

- [ ] Conta Railway criada
- [ ] Projeto criado no Railway
- [ ] PostgreSQL adicionado
- [ ] Backend deployado com variáveis de ambiente
- [ ] URL do backend gerada e copiada
- [ ] Frontend deployado com `NEXT_PUBLIC_API_URL`
- [ ] URL do frontend gerada
- [ ] CORS atualizado no backend com URL do frontend
- [ ] Testado acesso ao site

---

## 🆘 Precisa de Ajuda?

- Veja os logs: "Deployments" → "View Logs"
- Railway tem chat de suporte
- Verifique se todas as variáveis estão configuradas

