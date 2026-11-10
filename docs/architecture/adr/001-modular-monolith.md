# 1. Monólito Modular como Arquitetura Inicial

Data: 2026-10-17

## Status

Aceito

## Contexto

A plataforma de e-commerce **DevUsa** precisa entregar funcionalidades divididas em múltiplos domínios de negócio bem delimitados: Identidade, Catálogo, Carrinho, Estoque, Pedidos, Pagamentos e Notificações. 

Uma abordagem inicial baseada diretamente em múltiplos microsserviços traria uma sobrecarga prematura de infraestrutura operacional, orquestração de rede, consistência distribuída (dual writes, transações distribuídas) e complexidade de deploy no estágio inicial do projeto. Por outro lado, um monólito tradicional acoplado ("big ball of mud") dificultaria testes, refatorações e uma eventual extração futura de serviços consumidores de eventos (como Analytics e Notificações).

## Decisão

Adotaremos a arquitetura de **Monólito Modular** (Modular Monolith) em uma única aplicação Spring Boot 3 / Java 21:

1. **Separação por Pacotes de Bounded Contexts:**
   - `com.devusa.identity`: Autenticação, usuários e RBAC.
   - `com.devusa.catalog`: Produtos, variações, SKUs e categorias.
   - `com.devusa.cart`: Carrinho de compras e cálculo de itens.
   - `com.devusa.inventory`: Controle de estoque, reservas e concorrência.
   - `com.devusa.order`: Domínio hexagonal de pedidos e máquina de estados.
   - `com.devusa.payment`: Gateway de pagamento simulado e idempotência.
   - `com.devusa.notification`: Disparo e templates de notificações aos usuários.
   - `com.devusa.shared`: Objetos de valor transversais e utilitários compartilhados.

2. **Fronteiras e Encapsulamento:**
   - Cada módulo encapsula suas tabelas, entidades e regras internas.
   - Nenhum módulo deve acessar internamente as tabelas ou repositórios JPA de outro contexto diretamente. A comunicação intermódulos é feita através de interfaces públicas de serviço (APIs internas de módulo) ou eventos de domínio.
   - Validação contínua da arquitetura por meio de testes automatizados com **ArchUnit**.

3. **Evolução Gradual:**
   - Serviços assíncronos e consumidores de eventos (Analytics com WebFlux/MongoDB e Notification) serão extraídos para serviços autônomos nas fases posteriores planejadas (Semana 13).

## Consequências

### Positivas
- **Velocidade de desenvolvimento e refatoração:** Todo o código reside em uma única base de dados e repositório, permitindo refatorações seguras com suporte total da IDE e compilação Java.
- **Simplicidade de execução e depuração:** Todo o sistema pode ser executado localmente com apenas um comando (`docker compose up` + aplicação Spring Boot).
- **Fácil extração futura:** Como as fronteiras de pacotes e dependências são estritamente respeitadas, a extração de um contexto para microsserviço no futuro é natural e de baixo risco.

### Negativas / Riscos
- **Risco de acoplamento acidental:** Desenvolvedores podem ser tentados a injetar repositórios de outros módulos diretamente se não houver disciplina ou ferramentas de validação automatizada (mitigado pelo ArchUnit).
- **Ciclo de deploy único:** Qualquer alteração exige o deploy da aplicação inteira durante a fase de monólito.

