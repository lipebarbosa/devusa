# 3. Vitrine Fullstack: E-Commerce de Moda e System Inspector

Data: 2026-11-24

## Status

Aceito

## Contexto

Para apresentar o projeto DevUsa em portfólio para recrutadores, tech leads e clientes, a aplicação não deve parecer um modelo genérico de IA ou um template inacabado. É necessário unir:
1. Uma experiência de usuário refinada no estilo de marcas de moda contemporâneas (minimalista, elegante, tipografia refinada).
2. Uma forma interativa e visual de demonstrar a engenharia de backend e as decisões arquiteturais tomadas (máquina de estados do pedido, controle de concorrência com reservas e locking pessimista, tokens JWT, RBAC e rastreabilidade).

## Decisão

Criar uma camada de frontend em **React + TypeScript + Vite** contendo um **System Inspector ("Modo Engenheiro")** integrado:
- **Modo Loja:** Catálogo com variantes de tamanho/cor, carrinho reativo e checkout em etapas com Idempotency-Key.
- **Modo Engenheiro (System Inspector):** Drawer / painel flutuante que exibe em tempo real:
  - Transições da Máquina de Estados (`OrderStatus.canTransitionTo`).
  - Nível de estoque total vs. reservado por variante.
  - Logs de eventos transacionais e requisições HTTP geradas.
  - Diagrama de contexto e decisões de arquitetura (ADRs).

## Consequências

- O projeto passa a ser compreendido tanto por pessoas de negócios/design quanto por engenheiros seniores durante entrevistas técnicas.
- Facilidade de deploy: o frontend pode ser hospedado estaticamente (Vercel / Cloudflare Pages) enquanto o backend Spring Boot roda em contêiner (Render / Railway / AWS).

