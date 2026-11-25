import { CheckCircle2, Clock, CircleDashed, Shield, Database, Cpu, Layers, GitBranch } from 'lucide-react';

interface RoadmapItem {
  week: string;
  title: string;
  status: 'DONE' | 'IN_PROGRESS' | 'PLANNED';
  details: string;
  tags: string[];
}

const ROADMAP_ITEMS: RoadmapItem[] = [
  {
    week: 'Semana 1',
    title: 'Fundação, Bounded Contexts & Governança',
    status: 'DONE',
    details: 'Monólito modular em Java 21 + Spring Boot, Flyway, Docker Compose, GitHub Actions CI, regras ArchUnit e ADR 001.',
    tags: ['Java 21', 'ArchUnit', 'Flyway', 'CI/CD']
  },
  {
    week: 'Semana 2',
    title: 'Identity I — Autenticação, JWT & RBAC',
    status: 'DONE',
    details: 'Cadastro público (role CLIENTE), hashing BCrypt, Access Token JWT (15m), Refresh Token com rotação e hash SHA-256, @PreAuthorize e ADR 002.',
    tags: ['Spring Security', 'JWT', 'SHA-256', 'RBAC']
  },
  {
    week: 'Semana 3',
    title: 'Identity II — Base de API & Contratos',
    status: 'IN_PROGRESS',
    details: 'Tratamento global RFC 9457 (ProblemDetail), CORS configurado para Vercel, OpenAPI/Swagger com Bearer JWT. Em seguida: Testcontainers + JaCoCo >= 80%.',
    tags: ['ProblemDetail', 'OpenAPI', 'CORS', 'Testcontainers']
  },
  {
    week: 'Semanas 4–5',
    title: 'Catálogo Completo, Busca & Cache',
    status: 'IN_PROGRESS',
    details: 'Já ativo no MVP: Categorias, Produtos, Variações (SKU/tamanho/cor) e Seed. Próximo passo: CRUD Admin, PostgreSQL Full-Text Search e Cache.',
    tags: ['JPA', 'Full-Text Search', 'Keyset Pagination', 'Cache']
  },
  {
    week: 'Semana 6',
    title: 'Carrinho Distribuído com Redis',
    status: 'PLANNED',
    details: 'Persistência de carrinho cart:{userId} no Redis com TTL, merge de carrinho anônimo no login e recálculo de preços.',
    tags: ['Redis', 'TTL', 'Merge']
  },
  {
    week: 'Semana 7',
    title: 'Estoque & Benchmark de Concorrência',
    status: 'IN_PROGRESS',
    details: 'Já ativo no MVP: Lock pessimista (FOR UPDATE) + @Version otimista garantindo estoque >= 0. Próximo passo: Benchmark de 100 threads + Job de expiração.',
    tags: ['FOR UPDATE', '@Version', '100 Threads', 'ADR 005']
  },
  {
    week: 'Semanas 8–9',
    title: 'Order Hexagonal, Máquina de Estados & Pagamento',
    status: 'IN_PROGRESS',
    details: 'Já ativo no MVP: OrderStatus FSM, snapshot de preço/nome e Idempotency-Key no checkout. Próximo passo: FakePaymentGateway e compensação automática.',
    tags: ['State Machine', 'Idempotency-Key', 'Saga', 'Payment']
  },
  {
    week: 'Semanas 10 & 13',
    title: 'Eventos (Outbox + RabbitMQ) & Extração de Serviços',
    status: 'PLANNED',
    details: 'Transactional Outbox, DLQ, consumidores idempotentes e extração de notification-service e analytics-service (WebFlux + MongoDB).',
    tags: ['RabbitMQ', 'Outbox', 'WebFlux', 'MongoDB']
  },
  {
    week: 'Semanas 14–18',
    title: 'Observabilidade, Qualidade Avançada & Cloud',
    status: 'PLANNED',
    details: 'Prometheus, Grafana, OpenTelemetry, logs JSON com X-Correlation-ID, mutation testing (PIT), testes de carga (k6), WebSocket/SSE e AWS.',
    tags: ['OpenTelemetry', 'Prometheus', 'k6', 'AWS']
  }
];

export function RoadmapView({ onBackToStore }: { onBackToStore: () => void }) {
  const doneCount = ROADMAP_ITEMS.filter(i => i.status === 'DONE').length;
  const inProgressCount = ROADMAP_ITEMS.filter(i => i.status === 'IN_PROGRESS').length;

  return (
    <section style={{ padding: '48px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', flexWrap: 'wrap', gap: '16px', marginBottom: '40px', borderBottom: '1px solid #E5E5E5', paddingBottom: '24px' }}>
        <div>
          <p style={{ fontSize: '12px', letterSpacing: '3px', textTransform: 'uppercase', color: '#888', margin: '0 0 8px 0' }}>
            Construção em Público · MVP Incremental
          </p>
          <h1 style={{ fontSize: '34px', fontWeight: 500, letterSpacing: '-0.8px', margin: 0 }}>
            Arquitetura & Roadmap do Projeto
          </h1>
          <p style={{ fontSize: '14px', color: '#555', marginTop: '10px', maxWidth: '720px', lineHeight: '1.5' }}>
            O DevUsa está sendo lapidado em ciclos semanais. Abaixo você acompanha exatamente o que já roda hoje no MVP
            e os próximos blocos de engenharia previstos no plano completo de 18 semanas.
          </p>
        </div>
        <button
          onClick={onBackToStore}
          style={{
            padding: '10px 20px',
            backgroundColor: '#111',
            color: '#FFF',
            border: 'none',
            borderRadius: '2px',
            fontSize: '13px',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          Voltar para a Loja
        </button>
      </div>

      {/* Resumo de métricas do projeto */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '40px' }}>
        <div style={{ backgroundColor: '#FFF', border: '1px solid #E5E5E5', padding: '20px', borderRadius: '4px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#16A34A', fontSize: '12px', fontWeight: 700 }}>
            <CheckCircle2 size={16} />
            <span>CONCLUÍDOS</span>
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700, marginTop: '8px' }}>{doneCount} blocos</div>
          <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>Fundação, CI, ArchUnit e Identity JWT</div>
        </div>

        <div style={{ backgroundColor: '#FFF', border: '1px solid #E5E5E5', padding: '20px', borderRadius: '4px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#D97706', fontSize: '12px', fontWeight: 700 }}>
            <Clock size={16} />
            <span>EM LAPIDAÇÃO (MVP ATIVO)</span>
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700, marginTop: '8px' }}>{inProgressCount} blocos</div>
          <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>Catálogo, Estoque, Checkout e Base de API</div>
        </div>

        <div style={{ backgroundColor: '#FFF', border: '1px solid #E5E5E5', padding: '20px', borderRadius: '4px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#2563EB', fontSize: '12px', fontWeight: 700 }}>
            <Layers size={16} />
            <span>ARQUITETURA</span>
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700, marginTop: '8px' }}>Monólito Modular</div>
          <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>Java 21 · Spring Boot · PostgreSQL · React</div>
        </div>
      </div>

      {/* Garantias já implementadas */}
      <div style={{ backgroundColor: '#111', color: '#FFF', padding: '28px', borderRadius: '4px', marginBottom: '40px' }}>
        <h2 style={{ fontSize: '18px', fontWeight: 600, margin: '0 0 18px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Shield size={18} color="#4ADE80" />
          <span>O que já está implementado e testado hoje</span>
        </h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', fontSize: '13px', lineHeight: '1.6', color: '#D4D4D8' }}>
          <div>
            <strong style={{ color: '#FFF', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Cpu size={14} color="#38BDF8" /> Máquina de Estados (Order)
            </strong>
            Transições estritas em domínio puro (`OrderStatus.canTransitionTo`) impedindo estados inválidos.
          </div>
          <div>
            <strong style={{ color: '#FFF', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Database size={14} color="#38BDF8" /> Estoque Anti-Negativo
            </strong>
            Lock pessimista (`FOR UPDATE`) + controle otimista (`@Version`) e check constraints no PostgreSQL.
          </div>
          <div>
            <strong style={{ color: '#FFF', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <GitBranch size={14} color="#38BDF8" /> Tokens & Idempotência
            </strong>
            JWT stateless (15m), Refresh Token rotacionado com SHA-256 e `Idempotency-Key` no checkout.
          </div>
        </div>
      </div>

      {/* Timeline de Semanas */}
      <h2 style={{ fontSize: '20px', fontWeight: 600, marginBottom: '20px' }}>Linha do Tempo (Plano de 18 Semanas)</h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
        {ROADMAP_ITEMS.map((item) => {
          const badgeStyle =
            item.status === 'DONE'
              ? { bg: '#DCFCE7', text: '#166534', label: 'Concluído', icon: <CheckCircle2 size={14} /> }
              : item.status === 'IN_PROGRESS'
              ? { bg: '#FEF3C7', text: '#92400E', label: 'Ativo no MVP / Em Lapidação', icon: <Clock size={14} /> }
              : { bg: '#F3F4F6', text: '#4B5563', label: 'Planejado', icon: <CircleDashed size={14} /> };

          return (
            <div
              key={item.week}
              style={{
                backgroundColor: '#FFF',
                border: '1px solid #E5E5E5',
                borderRadius: '4px',
                padding: '20px 24px',
                display: 'flex',
                flexDirection: 'column',
                gap: '10px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <span style={{ fontSize: '12px', fontWeight: 700, color: '#888', textTransform: 'uppercase', letterSpacing: '1px' }}>
                    {item.week}
                  </span>
                  <h3 style={{ fontSize: '16px', fontWeight: 600, margin: 0 }}>{item.title}</h3>
                </div>
                <span
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '6px',
                    backgroundColor: badgeStyle.bg,
                    color: badgeStyle.text,
                    fontSize: '12px',
                    fontWeight: 600,
                    padding: '4px 10px',
                    borderRadius: '999px'
                  }}
                >
                  {badgeStyle.icon}
                  {badgeStyle.label}
                </span>
              </div>
              <p style={{ fontSize: '13px', color: '#555', margin: 0, lineHeight: '1.5' }}>{item.details}</p>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                {item.tags.map(tag => (
                  <span key={tag} style={{ fontSize: '11px', backgroundColor: '#F4F4F5', color: '#3F3F46', padding: '2px 8px', borderRadius: '2px', fontFamily: 'monospace' }}>
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

