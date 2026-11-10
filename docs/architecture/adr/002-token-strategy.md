# 2. Estratégia de Autenticação e Tokens (JWT e Refresh Token com Rotação)

Data: 2026-10-24

## Status

Aceito

## Contexto

A plataforma **DevUsa** é um e-commerce que atende clientes finais navegando pelo catálogo e realizando checkout, além de funcionários internos operando estoques, expedição e relatórios gerenciais (RBAC). A arquitetura do backend precisa ser escalável, stateless para as requisições de API, segura contra roubo de credenciais e capaz de revogar acessos de forma determinística.

## Decisão

Adotamos a estratégia de autenticação baseada em **Access Tokens JWT de curta duração** complementados por **Refresh Tokens persistidos com rotação obrigatória**:

1. **Access Token (JWT Stateless):**
   - Assinado com HMAC-SHA256 (`Keys.hmacShaKeyFor`).
   - Vida útil curta: 15 minutos (900 segundos).
   - Payload contém `sub` (User UUID), `email`, `name` e `role` (`CLIENTE`, `GERENTE`, `ESTOQUE`, `EXPEDICAO`).
   - Permite autorização rápida em memória sem round-trip ao banco de dados em cada request.

2. **Refresh Token (Stateful com Rotação e Hashing):**
   - Vida útil: 7 dias.
   - O token bruto é um UUID randômico e imprevisível entregue ao cliente.
   - No banco de dados (`refresh_tokens`), armazena-se apenas o hash SHA-256 do token bruto (`token_hash`), garantindo que um vazamento de banco não exponha sessões ativas.
   - **Rotação:** A cada chamada a `POST /api/v1/auth/refresh`, o token utilizado é imediatamente revogado (`revoked = true`) e um novo par (access token + refresh token) é emitido. Se um refresh token já revogado for reutilizado, isso indica possível roubo de sessão e uma política de revogação em cadeia pode ser acionada.

3. **RBAC no Spring Security:**
   - O filtro `JwtAuthenticationFilter` extrai o perfil do token e popula o `SecurityContext` com a authority `ROLE_<ROLE>`.
   - Regras de autorização fina são declaradas diretamente nos métodos com `@PreAuthorize("hasRole('GERENTE')")` via `@EnableMethodSecurity`.

## Consequências

### Positivas
- Alta performance nas consultas da API (validação local de assinatura do JWT).
- Menor janela de exposição caso um access token seja interceptado (expira em 15 min).
- Controle de revogação e encerramento de sessão viabilizado pelos refresh tokens.

### Negativas / Mitigações
- Um access token comprometido permanece válido até sua expiração natural (15 min). A curta duração mitiga esse risco.

