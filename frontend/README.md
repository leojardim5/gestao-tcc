# Gestão de TCC - Frontend

Este é o frontend para o sistema de Gestão de TCCs, construído com Next.js, TypeScript, e TailwindCSS.

## Stack de Tecnologias

- **Framework:** Next.js 14 (App Router)
- **Linguagem:** TypeScript
- **Estilização:** TailwindCSS
- **UI Components:** Radix UI, shadcn/ui (conceito)
- **Data Fetching:** React Query (`@tanstack/react-query`)
- **State Management:** Zustand
- **Formulários:** React Hook Form + Zod
- **HTTP Client:** Axios
- **Testes Unitários:** Vitest + Testing Library
- **Testes E2E:** Playwright
- **Qualidade de Código:** ESLint, Prettier, Husky, Commitlint

## Começando

### Pré-requisitos

- Node.js (v18+)
- npm ou yarn
- Backend do Gestão de TCC rodando (disponível em `http://localhost:8080`)

### Instalação

1. Clone o repositório.
2. Navegue até a pasta `frontend`:
   ```bash
   cd frontend
   ```
3. Instale as dependências:
   ```bash
   npm install
   ```

### Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz da pasta `frontend` e adicione a URL da sua API backend:

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Rodando em Desenvolvimento

Para iniciar o servidor de desenvolvimento, execute:

```bash
npm run dev
```

A aplicação estará disponível em [http://localhost:3000](http://localhost:3000).

## Scripts Disponíveis

- `npm run dev`: Inicia o servidor de desenvolvimento.
- `npm run build`: Compila a aplicação para produção.
- `npm run start`: Inicia o servidor de produção (requer `npm run build` antes).
- `npm run lint`: Executa o ESLint para verificar erros de linting.
- `npm test`: Executa os testes unitários com Vitest.
- `npm run test:e2e`: Executa os testes end-to-end com Playwright.

## Estrutura de Pastas

- `/src/app`: Rotas da aplicação (App Router).
- `/src/components`: Componentes React reutilizáveis.
- `/src/services`: Funções para comunicação com a API (Axios).
- `/src/hooks`: Hooks customizados.
- `/src/store`: Stores do Zustand para gerenciamento de estado global.
- `/src/interfaces`: Tipos e interfaces TypeScript.
- `/src/utils`: Funções utilitárias.
- `/src/styles`: Estilos globais.
- `/src/tests`: Arquivos de teste (unitários e E2E).

## Fluxo de Uso (Simulado)

1.  **Login:** Acesse `http://localhost:3000/login`.
2.  Preencha um nome, email e selecione um "papel" (Aluno, Orientador, Coordenador) para simular o login.
3.  **Dashboard:** Após o login, você será redirecionado para o dashboard com um resumo das informações.
4.  **Navegação:** Utilize a barra lateral para navegar entre as seções de TCCs, Usuários, etc.
5.  **TCCs:**
    - Crie um novo TCC clicando no botão "Novo TCC".
    - Edite ou exclua TCCs existentes através do menu de ações na tabela.
    - Clique no título de um TCC para ver a página de detalhes.
6.  **Detalhes do TCC:**
    - Navegue entre as abas para ver Submissões, Reuniões e Comentários relacionados ao TCC.

## Docker

Para construir e rodar a aplicação com Docker:

```bash
# Construir a imagem
docker build -t gestao-tcc-frontend .

# Rodar o container
docker run -p 3000:3000 gestao-tcc-frontend
```
