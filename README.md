# DevUsa

Plataforma de e-commerce de moda feita com Java e Spring Boot. É um projeto em desenvolvimento, usado para praticar engenharia de backend: modelagem de domínio, controle de estoque, pedidos, pagamentos e testes.

> **Status:** planejamento e fundação. Ainda não há código executável.

## Objetivo

**Problema de negócio.** Uma loja de moda que vende online precisa garantir que não vende o que não tem em estoque, que pedido e pagamento nunca ficam inconsistentes e que a equipe consegue operar o dia a dia (estoque, separação, envio). O DevUsa simula essa operação de ponta a ponta.

**Objetivo do projeto.** Construir esse sistema de forma incremental, praticando consistência transacional, concorrência, segurança, testes automatizados e documentação de decisões técnicas.

## Escopo do MVP

Fluxo mínimo de compra:

1. Cliente se cadastra e faz login.
2. Navega pelo catálogo (busca, filtros, paginação, variantes por tamanho/cor).
3. Monta o carrinho.
4. Finaliza a compra (checkout).
5. Paga (pagamento **simulado**).
6. Pedido é confirmado e o cliente acompanha o status.
7. A expedição separa e envia o pedido.

**Dentro do MVP**

- Cadastro e autenticação.
- Catálogo com variantes, busca, filtros e paginação.
- Carrinho.
- Estoque por variante, com reserva temporária durante a compra.
- Pedidos com estados e histórico.
- Pagamento simulado.
- Separação e envio de pedidos.
- Notificações de eventos do pedido.
- Indicadores básicos de vendas para o gerente.

**Fora do MVP**

- Pagamento com provedor real.
- Recuperação de carrinho abandonado.
- Outros itens: a definir.

## Perfis de usuário

| Perfil | O que faz |
|---|---|
| Cliente | Consulta produtos, gerencia o próprio carrinho, compra e acompanha seus pedidos. |
| Gerente | Consulta indicadores de vendas e acompanha a operação. |
| Responsável pelo estoque | Gerencia inventário, registra movimentações e acompanha a disponibilidade. |
| Funcionário de expedição | Vê pedidos liberados e atualiza as etapas de separação e envio. |

- **Cadastro de produtos e preços:** responsável a definir.
- As permissões são aplicadas no backend: cada perfil acessa só o que lhe cabe.

## Regras de negócio iniciais

### Estoque

- A disponibilidade é controlada por **variante** do produto.
- Disponível = quantidade em estoque − reservas ativas. Nunca pode ficar negativo, mesmo com compras simultâneas.
- Reservar reduz o disponível, mas não dá baixa no estoque. A baixa definitiva acontece quando o pagamento é confirmado.
- Reserva expirada ou cancelada devolve a quantidade **uma única vez**. Liberar duas vezes não pode alterar o estoque.

### Pedidos e pagamentos

- O pedido tem estados explícitos. Uma transição inválida é rejeitada. Estados: a definir.
- Pedido aguardando pagamento expira em **[prazo a definir]**. Ao expirar, o pedido é cancelado e a reserva é liberada.
- Confirmar o mesmo pagamento mais de uma vez tem o mesmo efeito de confirmar uma vez (idempotência).
- Pagamento recusado: o que acontece com o pedido e com a reserva é a definir.
- Mudanças de estado do pedido são registradas (o que mudou e quando).

### Carrinho e checkout

- O backend calcula o total. Preços enviados pelo cliente são ignorados.
- Preço e disponibilidade são revalidados no checkout.
- O pedido guarda o nome e o preço do item no momento da compra (a confirmar).
- Carrinhos abandonados expiram por rotina. A recuperação fica para o futuro.

### Notificações

- Eventos relevantes (pagamento confirmado, cancelamento, mudança de status) geram notificação.
- Falha no envio de notificação **não** desfaz nem bloqueia o pedido.
- Canal de envio e política de nova tentativa: a definir.

## Decisões em aberto

- Em que momento começa a reserva de estoque: ao iniciar o checkout ou ao criar o pedido.
- Prazo de expiração do pedido aguardando pagamento.
- Estados do pedido e transições permitidas.
- Destino do pedido e da reserva quando o pagamento é recusado.
- Quem cadastra produtos e preços.
- Tipo de autenticação (token, sessão etc.).
- Licença do projeto.
- Ferramenta de integração contínua.

## Em desenvolvimento

- Primeira versão do README e das definições de domínio.
- Próximo passo: desenhar o fluxo de checkout e os estados do pedido.

## Roadmap

1. **Fundação:** estrutura do projeto, ambiente, convenções.
2. **Identidade:** cadastro, login e permissões.
3. **Catálogo:** produtos, variantes, busca, filtros e paginação.
4. **Estoque:** disponibilidade, reservas e controle de concorrência.
5. **Carrinho:** itens e validação de disponibilidade.
6. **Pedidos:** criação, estados e histórico.
7. **Pagamentos:** simulação, idempotência e expiração de pedidos.
8. **Expedição:** separação e envio.
9. **Notificações e indicadores.**
10. **Qualidade:** ampliação de testes, documentação da API e CI.
11. **Evolução:** observabilidade e avaliação de implantação em nuvem.

A ordem pode mudar conforme o aprendizado durante a implementação.

## Stack prevista (ainda não confirmada)

Java, Spring Boot, Spring Data JPA, PostgreSQL, Flyway, Spring Security, JUnit 5 e Mockito, Maven, Docker.

## Licença

Ainda não definida. Até lá, nenhuma permissão de uso ou redistribuição é concedida.
