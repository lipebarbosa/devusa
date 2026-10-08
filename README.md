# DevUsa

**Uma plataforma de e-commerce de moda desenvolvida com foco em arquitetura de software, integridade de dados e confiabilidade das operações comerciais.**

O **DevUsa** é um projeto de e-commerce construído com Java e Spring Boot, concebido para simular os principais processos de uma operação de comércio eletrônico: gerenciamento de catálogo, carrinho de compras, controle de estoque, processamento de pedidos e pagamentos.

O projeto tem como objetivo aplicar boas práticas de engenharia de software na construção de uma aplicação evolutiva, segura e sustentável, priorizando a clareza das regras de negócio, a consistência transacional e a qualidade do código.

> **Status:** Em desenvolvimento — fase de planejamento e estruturação inicial.

---

## Sumário

* [Visão Geral](#-visão-geral)
* [Objetivos do Projeto](#-objetivos-do-projeto)
* [Escopo do MVP](#-escopo-do-mvp)
* [Perfis e Permissões](#-perfis-e-permissões)
* [Principais Regras de Negócio](#-principais-regras-de-negócio)
* [Arquitetura e Organização](#-arquitetura-e-organização)
* [Tecnologias](#-tecnologias)
* [Qualidade e Confiabilidade](#-qualidade-e-confiabilidade)
* [Roadmap](#-roadmap)
* [Execução Local](#-execução-local)
* [Contribuição](#-contribuição)
* [Licença](#-licença)

## 🛍️ Visão Geral

O DevUsa é uma plataforma de comércio eletrônico voltada para o segmento de moda, na qual clientes poderão explorar produtos, selecionar variantes disponíveis, gerenciar seus carrinhos e acompanhar suas compras.

Além da experiência de compra, o sistema deverá contemplar os processos operacionais necessários para administrar produtos, controlar a disponibilidade do estoque, acompanhar pedidos e organizar a expedição.

O desenvolvimento será conduzido de forma incremental, partindo de um MVP funcional e evoluindo conforme as necessidades técnicas e de negócio identificadas durante a implementação.

## 🎯 Objetivos do Projeto

O desenvolvimento do DevUsa busca aplicar conceitos e práticas utilizados na construção de sistemas de backend profissionais.

Os principais objetivos são:

* **Modelagem de domínio:** representar adequadamente entidades, relacionamentos, estados e regras de negócio de um e-commerce.
* **Integridade transacional:** manter a consistência das operações envolvendo pedidos, pagamentos e estoque.
* **Arquitetura modular:** organizar as responsabilidades do sistema para facilitar manutenção, testes e evolução.
* **Segurança:** implementar autenticação, autorização e proteção dos recursos da aplicação.
* **Qualidade de software:** desenvolver testes automatizados e estabelecer critérios de qualidade desde as primeiras etapas.
* **Confiabilidade operacional:** tratar falhas, concorrência, operações repetidas e situações excepcionais.
* **Evolução incremental:** construir funcionalidades de forma organizada, documentando decisões técnicas relevantes.

## 📦 Escopo do MVP

O MVP (Minimum Viable Product) será desenvolvido para validar o fluxo essencial de compra, desde a descoberta de produtos até a confirmação e o acompanhamento do pedido.

### Experiência do cliente

* Cadastro e autenticação de usuários.
* Navegação pelo catálogo de produtos.
* Busca, filtros e paginação.
* Visualização de detalhes, preços e variantes dos produtos.
* Gerenciamento do carrinho de compras.
* Finalização da compra.
* Acompanhamento do status dos pedidos.
* Recebimento de notificações relacionadas às compras.

### Operações comerciais

* Gerenciamento de produtos e suas variantes.
* Controle de disponibilidade e movimentações de estoque.
* Criação e gerenciamento de pedidos.
* Reserva temporária de estoque durante o processo de compra.
* Integração inicial com um mecanismo de pagamento simulado.
* Liberação de reservas em situações de expiração ou falha.
* Consulta de indicadores básicos de vendas e pedidos.
* Organização dos pedidos para separação e expedição.

**Limite inicial:** o processamento de pagamentos será simulado durante o MVP. Uma integração com um provedor real poderá ser considerada em uma etapa futura, após a definição dos requisitos de segurança e operação.

## 👥 Perfis e Permissões

A aplicação deverá utilizar controle de acesso baseado nas responsabilidades de cada perfil.

| Perfil                   | Responsabilidades                                                                             |
| ------------------------ | --------------------------------------------------------------------------------------------- |
| Cliente                  | Consultar produtos, gerenciar o próprio carrinho, realizar compras e acompanhar seus pedidos. |
| Gerente                  | Consultar indicadores de vendas e acompanhar a operação comercial.                            |
| Responsável pelo estoque | Gerenciar inventário, registrar movimentações e acompanhar a disponibilidade dos produtos.    |
| Funcionário de expedição | Consultar pedidos liberados para processamento e atualizar as etapas de separação e envio.    |

As permissões deverão ser aplicadas no backend, garantindo que cada usuário acesse apenas os recursos e as operações autorizados para seu perfil.

## ⚖️ Principais Regras de Negócio

As regras abaixo representam a definição inicial do domínio e serão refinadas durante a implementação.

### 1. Gestão de estoque

* A disponibilidade deverá ser controlada por variante do produto, considerando características como tamanho e cor quando aplicáveis.
* O sistema deverá impedir que compras concorrentes comprometam a quantidade disponível.
* A reserva deverá reduzir a quantidade disponível para novas compras sem representar, necessariamente, uma saída definitiva do inventário.
* A baixa definitiva deverá ocorrer conforme a transição de estado definida para a confirmação do pagamento.
* Reservas expiradas ou canceladas deverão liberar a quantidade correspondente, evitando duplicidade de liberação.

### 2. Pedidos e pagamentos

* Um pedido deverá possuir estados explícitos e transições controladas.
* Pedidos aguardando pagamento deverão possuir um prazo de expiração definido.
* A confirmação do pagamento deverá atualizar o estado do pedido e acionar os processos necessários para consolidar a compra.
* Pagamentos recusados, expirados ou cancelados deverão seguir fluxos específicos de tratamento.
* Operações repetidas de confirmação deverão ser tratadas de maneira idempotente, evitando efeitos duplicados.
* O histórico de alterações relevantes deverá permitir rastrear o ciclo de vida do pedido.

### 3. Carrinho e checkout

* O carrinho deverá refletir os itens selecionados pelo cliente.
* A disponibilidade do estoque e os preços deverão ser validados novamente durante a finalização da compra.
* O backend deverá calcular os valores da compra, sem confiar em preços enviados pelo cliente.
* A criação do pedido deverá considerar a disponibilidade dos produtos e as condições comerciais vigentes.
* Carrinhos abandonados poderão ser tratados por rotinas de expiração e, futuramente, por mecanismos de recuperação.

### 4. Notificações

* O sistema deverá identificar eventos relevantes, como confirmação de pagamento, cancelamento e atualização do pedido.
* As notificações não deverão comprometer a integridade das operações comerciais caso um serviço de envio fique indisponível.
* O canal de envio e as estratégias de retentativa serão definidos durante a implementação.

## 🏗️ Arquitetura e Organização

O DevUsa será desenvolvido com uma arquitetura modular, buscando separar as responsabilidades do sistema e manter as regras de negócio organizadas.

A divisão inicial de responsabilidades considera os seguintes módulos:

| Módulo         | Responsabilidade                                                 |
| -------------- | ---------------------------------------------------------------- |
| `Identity`     | Autenticação, usuários, perfis e autorização.                    |
| `Catalog`      | Produtos, categorias, variantes, preços e consultas ao catálogo. |
| `Cart`         | Gerenciamento do carrinho e dos itens selecionados.              |
| `Inventory`    | Disponibilidade, reservas e movimentações de estoque.            |
| `Order`        | Criação, ciclo de vida e histórico dos pedidos.                  |
| `Payment`      | Processamento simulado e controle do estado dos pagamentos.      |
| `Notification` | Comunicação de eventos relevantes ao cliente.                    |

Essa divisão representa uma proposta inicial de organização lógica, não uma definição de que cada módulo será um microsserviço independente.

A arquitetura física e a estratégia de implantação serão definidas conforme as necessidades do produto, evitando complexidade prematura.

As decisões arquiteturais relevantes poderão ser registradas por meio de ADRs (*Architecture Decision Records*), documentando o contexto, as alternativas consideradas e os motivos de cada escolha.

## 🧰 Tecnologias

A stack será consolidada progressivamente durante a fase de implementação.

| Categoria                 | Tecnologia ou abordagem                 |
| ------------------------- | --------------------------------------- |
| Linguagem                 | Java                                    |
| Framework backend         | Spring Boot                             |
| Persistência              | Spring Data JPA / Hibernate             |
| Banco de dados relacional | PostgreSQL                              |
| Versionamento de banco    | Flyway                                  |
| Segurança                 | Spring Security                         |
| Autenticação              | A definir conforme os requisitos do MVP |
| Testes automatizados      | JUnit 5 e Mockito                       |
| Build e dependências      | Maven                                   |
| Versionamento de código   | Git e GitHub                            |
| Containerização           | Docker e Docker Compose                 |
| Integração contínua       | A definir durante a implementação       |

As versões das ferramentas, as dependências e as configurações de ambiente serão documentadas conforme forem estabelecidas no projeto.

## 🧪 Qualidade e Confiabilidade

A qualidade será tratada como parte do processo de desenvolvimento, e não como uma etapa posterior.

Os principais critérios incluem:

* Testes unitários para regras de negócio.
* Testes de integração para persistência e fluxos críticos.
* Validação das transições de estado de pedidos e pagamentos.
* Testes de concorrência nos processos sensíveis de estoque.
* Tratamento consistente de erros e respostas da API.
* Validação de entradas e proteção dos recursos autenticados.
* Automação de verificações por meio de integração contínua.
* Documentação dos contratos da API e das decisões arquiteturais.

A cobertura de testes será acompanhada como uma métrica auxiliar. A prioridade será garantir que os cenários críticos e as regras de negócio estejam efetivamente validados.

## 🗺️ Roadmap

O desenvolvimento será dividido em etapas para manter o escopo controlado e facilitar a validação de cada entrega.

* [ ] **Fase 1 — Fundação:** estrutura do projeto, configuração do ambiente, convenções e documentação inicial.
* [ ] **Fase 2 — Identidade:** autenticação, usuários e controle de permissões.
* [ ] **Fase 3 — Catálogo:** cadastro, consulta, busca e paginação de produtos.
* [ ] **Fase 4 — Carrinho:** gerenciamento de itens e validação de quantidades.
* [ ] **Fase 5 — Estoque:** disponibilidade, reservas e controle de concorrência.
* [ ] **Fase 6 — Pedidos:** criação, estados, histórico e regras do ciclo de vida.
* [ ] **Fase 7 — Pagamentos:** simulação de pagamentos, idempotência e expiração de pedidos.
* [ ] **Fase 8 — Notificações:** comunicação de eventos e tratamento de falhas.
* [ ] **Fase 9 — Qualidade:** ampliação dos testes, documentação da API e pipeline de CI.
* [ ] **Fase 10 — Evolução:** melhorias operacionais, observabilidade e avaliação de implantação em nuvem.

O roadmap poderá ser ajustado conforme os aprendizados e as dependências identificadas durante o desenvolvimento.

## 🚀 Execução Local

O projeto está em fase inicial e ainda não possui um procedimento de execução local consolidado.

Quando a configuração estiver disponível, esta seção documentará:

1. Pré-requisitos e versões necessárias.
2. Configuração das variáveis de ambiente.
3. Inicialização dos serviços dependentes.
4. Execução das migrações do banco de dados.
5. Inicialização da aplicação.
6. Execução dos testes automatizados.
7. Endereços da API e da documentação interativa.

Credenciais, tokens e outros segredos deverão ser fornecidos por variáveis de ambiente, sem serem versionados no repositório.

## 🤝 Contribuição

O DevUsa é desenvolvido de forma incremental, com foco em aprendizado aplicado e boas práticas de engenharia de software.

Sugestões de melhoria, identificação de problemas e discussões sobre decisões técnicas são bem-vindas.

Mudanças futuras deverão priorizar clareza, consistência com as regras de negócio, cobertura de testes e simplicidade arquitetural.

## 📄 Licença

A licença do projeto será definida antes de sua distribuição pública como software reutilizável.

---

**DevUsa** — desenvolvendo uma plataforma de e-commerce enquanto aplico, na prática, os fundamentos da engenharia de software.
