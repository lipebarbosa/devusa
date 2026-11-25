# Deploy do Backend no Render

Este documento descreve passo a passo como publicar o backend da aplicação **DevUsa** no Render.

1. **Criar Banco PostgreSQL**
   - Acesse https://render.com e crie um serviço **PostgreSQL** gratuito.
   - Anote as variáveis `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
2. **Configurar Serviço Web**
   - No Render, crie um novo serviço **Web Service**.
   - Selecione a opção **Docker** e aponte para o `Dockerfile` na raiz do repositório.
   - Defina `Plan: free` e `Region: oregon`.
   - Em **Env Vars** adicione:
     - `SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}`
     - `SPRING_DATASOURCE_USERNAME=${DB_USER}`
     - `SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}`
     - `SECURITY_JWT_SECRET` (gerar valor aleatório, ex.: `openssl rand -hex 32`).
     - `APP_CORS_ALLOWED_ORIGINS=https://*.vercel.app`
3. **Deploy**
   - Salve e aguarde a construção da imagem Docker.
   - Verifique o health check em `https://<service>.onrender.com/actuator/health`.
4. **Popular catálogo**
   - Após o serviço estar saudável, execute:
     ```bash
     curl -X POST https://<service>.onrender.com/api/v1/catalog/seed
     ```
   - A resposta deve ser `Catálogo de demonstração populado com sucesso.`.

> **Importante**: O frontend já está configurado na Vercel com a variável `VITE_API_URL` apontando para a URL acima.

