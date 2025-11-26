# 🚀 Guia de Deploy Gratuito - Gestão TCC

## 📋 Opções Gratuitas de Hospedagem

### 1. **Railway** ⭐ (RECOMENDADO - Mais fácil)
- ✅ **Backend + Frontend + Banco de Dados** tudo em um lugar
- ✅ PostgreSQL gratuito incluído
- ✅ Deploy automático via GitHub
- ✅ $5 grátis por mês (suficiente para começar)
- ✅ Domínio gratuito (.railway.app)

### 2. **Render**
- ✅ Backend + Frontend + PostgreSQL gratuito
- ✅ Deploy automático via GitHub
- ⚠️ Free tier "dorme" após 15min de inatividade (acorda em ~30s)
- ✅ Domínio gratuito (.onrender.com)

### 3. **Heroku** (Não recomendado mais)
- ❌ Removeram o plano gratuito em 2022
- 💰 Agora é pago ($5-7/mês mínimo)

### 4. **Vercel** (Só Frontend) + **Supabase** (Banco)
- ✅ Vercel: Frontend Next.js gratuito (ilimitado)
- ✅ Supabase: PostgreSQL gratuito (500MB, suficiente)
- ✅ Deploy automático via GitHub

---

## 🎯 RECOMENDAÇÃO: Railway (Mais Fácil)

### Passo 1: Preparar o Projeto

#### Backend - Criar `Procfile`:
```bash
# backend/Procfile
web: java -jar target/gestaotcc-backend-0.0.1-SNAPSHOT.jar
```

#### Backend - Ajustar `application.yml`:
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# Variáveis de ambiente (Railway injeta automaticamente)
```

#### Frontend - Criar `vercel.json` (se usar Vercel):
```json
{
  "buildCommand": "cd frontend && npm install && npm run build",
  "outputDirectory": "frontend/.next",
  "devCommand": "cd frontend && npm run dev",
  "installCommand": "cd frontend && npm install"
}
```

---

## 🚂 Deploy no Railway (Passo a Passo)

### 1. Criar Conta no Railway
1. Acesse: https://railway.app
2. Faça login com GitHub
3. Clique em "New Project"
4. Selecione "Deploy from GitHub repo"
5. Escolha seu repositório

### 2. Adicionar PostgreSQL
1. No projeto Railway, clique em "+ New"
2. Selecione "Database" → "Add PostgreSQL"
3. Railway cria automaticamente e injeta as variáveis:
   - `DATABASE_URL`
   - `PGHOST`, `PGPORT`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`

### 3. Deploy do Backend
1. No projeto Railway, clique em "+ New" → "GitHub Repo"
2. Selecione seu repositório
3. Configure:
   - **Root Directory**: `backend`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/gestaotcc-backend-0.0.1-SNAPSHOT.jar`
4. Adicione variáveis de ambiente:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=${{Postgres.DATABASE_URL}}
   DB_USER=${{Postgres.PGUSER}}
   DB_PASSWORD=${{Postgres.PGPASSWORD}}
   MAIL_USERNAME=seu-email@gmail.com
   MAIL_PASSWORD=sua-senha-app
   APP_NAME=Gestão TCC
   ```

### 4. Deploy do Frontend
1. No projeto Railway, clique em "+ New" → "GitHub Repo"
2. Selecione seu repositório
3. Configure:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npm start`
4. Adicione variáveis de ambiente:
   ```
   NEXT_PUBLIC_API_URL=https://seu-backend.railway.app
   NODE_ENV=production
   ```

### 5. Configurar Domínios
1. Backend: Settings → Generate Domain (ex: `gestao-tcc-backend.railway.app`)
2. Frontend: Settings → Generate Domain (ex: `gestao-tcc.railway.app`)
3. Atualize `NEXT_PUBLIC_API_URL` no frontend com a URL do backend

---

## 🐘 Alternativa: Vercel (Frontend) + Supabase (Banco)

### Frontend no Vercel:
1. Acesse: https://vercel.com
2. Login com GitHub
3. "Add New Project" → Selecione repositório
4. Configure:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `.next`
5. Adicione variável:
   ```
   NEXT_PUBLIC_API_URL=https://seu-backend.railway.app
   ```

### Banco no Supabase:
1. Acesse: https://supabase.com
2. Crie projeto gratuito
3. Vá em Settings → Database
4. Copie a **Connection String** (URI)
5. Use no backend como `DATABASE_URL`

---

## 🔧 Configurações Necessárias

### Backend - Ajustar CORS:
```java
// backend/src/main/java/.../config/CorsConfig.java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://localhost:3000",
                        "https://seu-frontend.railway.app",
                        "https://seu-frontend.vercel.app"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

### Frontend - Ajustar API URL:
```typescript
// frontend/src/services/api.ts
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  // ...
});
```

---

## 📝 Checklist de Deploy

### Antes de Deployar:
- [ ] Testar localmente
- [ ] Remover logs de debug (`System.out.println`)
- [ ] Configurar CORS para produção
- [ ] Ajustar `application.yml` para usar variáveis de ambiente
- [ ] Criar `Procfile` no backend
- [ ] Testar build do frontend (`npm run build`)

### Variáveis de Ambiente Necessárias:

**Backend:**
- `DATABASE_URL` (Railway/Supabase fornece)
- `DB_USER`
- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `SPRING_PROFILES_ACTIVE=prod`

**Frontend:**
- `NEXT_PUBLIC_API_URL` (URL do backend em produção)

---

## 🎯 Opção Mais Rápida: Railway Tudo Junto

1. **Criar projeto Railway**
2. **Adicionar PostgreSQL** (gratuito)
3. **Deploy Backend** (Railway detecta Maven automaticamente)
4. **Deploy Frontend** (Railway detecta Next.js automaticamente)
5. **Configurar variáveis de ambiente**
6. **Pronto!** 🎉

---

## 💡 Dicas

1. **Railway** é mais fácil porque tudo fica em um lugar
2. **Supabase** tem 500MB grátis (suficiente para começar)
3. **Vercel** é ótimo para frontend Next.js (deploy automático)
4. Use **variáveis de ambiente** para tudo (nunca commite senhas!)
5. Configure **domínios customizados** depois (opcional)

---

## 🆘 Problemas Comuns

### Backend não conecta no banco:
- Verifique `DATABASE_URL` está correto
- Railway usa formato: `postgresql://user:pass@host:port/db`

### Frontend não encontra API:
- Verifique `NEXT_PUBLIC_API_URL` está configurado
- Verifique CORS no backend permite o domínio do frontend

### Build falha:
- Verifique logs no Railway/Vercel
- Teste build local primeiro: `./mvnw clean package` e `npm run build`

---

## 📚 Links Úteis

- Railway: https://railway.app
- Vercel: https://vercel.com
- Supabase: https://supabase.com
- Render: https://render.com

