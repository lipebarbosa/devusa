# Guia de Deploy Público — DevUsa Fullstack Showcase

Este guia ensina como publicar o projeto **DevUsa** em links públicos gratuitos (ou freemium) para apresentar aos avaliadores.

---

## Opção 1: Backend no Render ou Railway + Frontend na Vercel (Recomendada)

### 1. Backend (Spring Boot + PostgreSQL) no Render / Railway
1. **Criar Banco PostgreSQL:**
   - No [Render.com](https://render.com) ou [Railway.app](https://railway.app), crie uma instância gratuita de **PostgreSQL**.
   - Copie as variáveis geradas: `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_PORT`, `DB_HOST`.
2. **Subir o Serviço Web:**
   - Conecte seu repositório GitHub.
   - O Render/Railway detectará automaticamente o [Dockerfile](file:///d:/Projetos/codigos-git/devusa/Dockerfile) na raiz.
   - Configure as variáveis de ambiente:
     * `DB_NAME`: nome do banco
     * `DB_USER`: usuário do banco
     * `DB_PASSWORD`: senha do banco
     * `DB_PORT`: porta (geralmente 5432)
     * `SPRING_DATASOURCE_URL`: `jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}`
   - Ao iniciar, o **Flyway** executará automaticamente as migrações `V1`, `V2`, `V3` e `V4`.
   - Chame `POST /api/v1/catalog/seed` uma única vez para popular o catálogo demonstrativo.

---

### 2. Frontend (React + TypeScript) na Vercel ou Cloudflare Pages
1. No painel da [Vercel](https://vercel.com):
   - Importe o mesmo repositório do GitHub.
   - Defina o **Root Directory** como `frontend`.
   - O framework detectará automaticamente **Vite**.
   - Build Command: `npm run build`
   - Output Directory: `dist`
2. Clique em **Deploy**.
3. Em menos de 2 minutos você terá uma URL pública (ex.: `https://devusa-store.vercel.app`) pronta para colocar no seu currículo e LinkedIn!

---

## O que mostrar na apresentação / entrevista:
1. **Experiência do Cliente:** Adicione itens à sacola, altere tamanho/cor e veja o estoque disponível recalculando.
2. **O "System Inspector":** Clique no botão **"Abrir Modo Engenheiro (System Inspector)"** no topo.
3. **Máquina de Estados ao Vivo:** Acompanhe o pedido passando por `AGUARDANDO_PAGAMENTO` → `PAGO` → `EM_SEPARACAO` → `ENVIADO` → `ENTREGUE`.
4. **Garantias de Concorrência:** Explique como o Pessimistic Lock (`FOR UPDATE`) e a reserva temporária impedem estoque negativo ou vendas duplicadas.

