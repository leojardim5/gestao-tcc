# 🚂 Deploy no Railway - Passo a Passo Rápido

## ⚡ Setup Rápido (5 minutos)

### 1. Criar Conta e Projeto
1. Acesse: https://railway.app
2. Login com GitHub
3. "New Project" → "Deploy from GitHub repo"
4. Selecione seu repositório

### 2. Adicionar PostgreSQL
1. No projeto, clique em **"+ New"**
2. Selecione **"Database"** → **"Add PostgreSQL"**
3. ✅ Pronto! Railway cria automaticamente

### 3. Deploy Backend
1. Clique em **"+ New"** → **"GitHub Repo"**
2. Selecione seu repositório novamente
3. Railway detecta automaticamente que é Maven/Java
4. Configure:
   - **Root Directory**: `backend`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/gestaotcc-backend-0.0.1-SNAPSHOT.jar`

### 4. Variáveis de Ambiente do Backend
No serviço do backend, vá em **"Variables"** e adicione:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=${{Postgres.DATABASE_URL}}
DB_USER=${{Postgres.PGUSER}}
DB_PASSWORD=${{Postgres.PGPASSWORD}}
MAIL_USERNAME=siga.tcc.notificacao@gmail.com
MAIL_PASSWORD=lqpe uflp pbsi kcki
APP_NAME=Gestão TCC
PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://seu-frontend.railway.app
```

**IMPORTANTE**: Substitua `https://seu-frontend.railway.app` pela URL real do frontend depois!

### 5. Deploy Frontend
1. Clique em **"+ New"** → **"GitHub Repo"**
2. Selecione seu repositório
3. Railway detecta Next.js automaticamente
4. Configure:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npm start`

### 6. Variáveis de Ambiente do Frontend
No serviço do frontend, vá em **"Variables"** e adicione:

```
NEXT_PUBLIC_API_URL=https://seu-backend.railway.app
NODE_ENV=production
```

**IMPORTANTE**: Substitua `https://seu-backend.railway.app` pela URL real do backend!

### 7. Gerar Domínios
1. **Backend**: Settings → **"Generate Domain"** (ex: `gestao-tcc-backend.railway.app`)
2. **Frontend**: Settings → **"Generate Domain"** (ex: `gestao-tcc.railway.app`)
3. **Atualize** `NEXT_PUBLIC_API_URL` no frontend com a URL do backend
4. **Atualize** `CORS_ALLOWED_ORIGINS` no backend com a URL do frontend

### 8. Pronto! 🎉
Acesse: `https://seu-frontend.railway.app`

---

## 🔧 Troubleshooting

### Backend não conecta no banco:
- Verifique se `DATABASE_URL` está usando `${{Postgres.DATABASE_URL}}`
- Railway injeta automaticamente as variáveis do PostgreSQL

### Frontend não encontra API:
- Verifique `NEXT_PUBLIC_API_URL` está correto
- Verifique CORS no backend permite o domínio do frontend
- Variáveis que começam com `NEXT_PUBLIC_` são expostas no cliente

### Build falha:
- Veja os logs em "Deployments" → "View Logs"
- Teste build local primeiro: `./mvnw clean package` e `npm run build`

---

## 💰 Custos

- **Railway**: $5 grátis por mês (suficiente para começar)
- **PostgreSQL**: Incluído no plano gratuito
- **Domínios**: Gratuitos (.railway.app)

---

## 📝 Checklist

- [ ] Backend deployado e rodando
- [ ] Frontend deployado e rodando
- [ ] PostgreSQL conectado
- [ ] Variáveis de ambiente configuradas
- [ ] CORS configurado corretamente
- [ ] Domínios gerados
- [ ] Testado login e funcionalidades básicas

