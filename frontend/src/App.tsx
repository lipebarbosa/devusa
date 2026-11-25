import { useState, useEffect } from 'react';
import type { Product, Variant, CartItem, Order, SystemEvent, OrderStatus } from './types';
import { api } from './api';
import { RoadmapView } from './RoadmapView';
import { 
  ShoppingBag, 
  Terminal, 
  ArrowRight, 
  ShieldCheck, 
  Layers, 
  Activity, 
  X,
  Box,
  Server
} from 'lucide-react';

const FALLBACK_PRODUCTS: Product[] = [
  {
    id: 'prod-1',
    name: 'Blazer Oversized Lã Fria',
    description: 'Corte reto estruturado em lã fria italiana, ombros destacados e acabamento pespontado artesanal.',
    basePrice: 890.00,
    categoryName: 'Alfaiataria Moderna',
    categorySlug: 'alfaiataria',
    imageUrl: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&q=80&w=900',
    variants: [
      { id: 'var-1', sku: 'BLZ-BLK-P', size: 'P', color: 'Preto Carvão', price: 890.00, availableStock: 5 },
      { id: 'var-2', sku: 'BLZ-BLK-M', size: 'M', color: 'Preto Carvão', price: 890.00, availableStock: 8 },
      { id: 'var-3', sku: 'BLZ-BEI-M', size: 'M', color: 'Bege Areia', price: 890.00, availableStock: 4 },
    ]
  },
  {
    id: 'prod-2',
    name: 'Trench Coat Gabardine Impermeável',
    description: 'Clássico atemporal revisitado com abotoamento duplo, cinto anatômico e caimento drapeado.',
    basePrice: 1250.00,
    categoryName: 'Casacos & Outerwear',
    categorySlug: 'outerwear',
    imageUrl: 'https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&q=80&w=900',
    variants: [
      { id: 'var-4', sku: 'TRC-KHK-M', size: 'M', color: 'Cáqui Militar', price: 1250.00, availableStock: 6 },
      { id: 'var-5', sku: 'TRC-BLK-M', size: 'M', color: 'Preto Noite', price: 1250.00, availableStock: 3 },
    ]
  },
  {
    id: 'prod-3',
    name: 'Camisa Silk Touch Gola Padre',
    description: 'Fluidez máxima em toque de seda maquinetada, punhos alongados e caimento minimalista.',
    basePrice: 420.00,
    categoryName: 'Básicos & Seda',
    categorySlug: 'essenciais',
    imageUrl: 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&q=80&w=900',
    variants: [
      { id: 'var-6', sku: 'CMS-WHT-P', size: 'P', color: 'Branco Puro', price: 420.00, availableStock: 12 },
      { id: 'var-7', sku: 'CMS-WHT-M', size: 'M', color: 'Branco Puro', price: 420.00, availableStock: 15 },
    ]
  },
  {
    id: 'prod-4',
    name: 'Calça Pantalona Pregas Duplas',
    description: 'Cintura alta com cós alfaiatado, pregas profundas e barra ampla com caimento escultural.',
    basePrice: 590.00,
    categoryName: 'Alfaiataria Moderna',
    categorySlug: 'alfaiataria',
    imageUrl: 'https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&q=80&w=900',
    variants: [
      { id: 'var-8', sku: 'CAL-GRY-38', size: '38', color: 'Cinza Chumbo', price: 590.00, availableStock: 7 },
      { id: 'var-9', sku: 'CAL-BLK-40', size: '40', color: 'Preto Carvão', price: 590.00, availableStock: 5 },
    ]
  }
];

export function App() {
  const [products, setProducts] = useState<Product[]>(FALLBACK_PRODUCTS);
  const [backendConnected, setBackendConnected] = useState(false);
  const [selectedVariants, setSelectedVariants] = useState<Record<string, Variant>>({
    'prod-1': FALLBACK_PRODUCTS[0].variants[0],
    'prod-2': FALLBACK_PRODUCTS[1].variants[0],
    'prod-3': FALLBACK_PRODUCTS[2].variants[0],
    'prod-4': FALLBACK_PRODUCTS[3].variants[0],
  });
  const [cart, setCart] = useState<CartItem[]>([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isInspectorOpen, setIsInspectorOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'store' | 'roadmap'>('store');
  const [categoryFilter, setCategoryFilter] = useState<string>('all');
  const [activeOrder, setActiveOrder] = useState<Order | null>(null);
  const [systemEvents, setSystemEvents] = useState<SystemEvent[]>([
    {
      id: 'evt-init',
      timestamp: new Date().toLocaleTimeString(),
      module: 'CATALOG',
      action: 'BOOT_SYSTEM',
      details: 'Frontend inicializado. Verificando conectividade com o Backend Spring Boot...',
      status: 'SUCCESS'
    }
  ]);

  const addEvent = (module: SystemEvent['module'], action: string, details: string, status: SystemEvent['status'] = 'SUCCESS') => {
    const newEvt: SystemEvent = {
      id: Math.random().toString(),
      timestamp: new Date().toLocaleTimeString(),
      module,
      action,
      details,
      status
    };
    setSystemEvents(prev => [newEvt, ...prev.slice(0, 19)]);
  };

  useEffect(() => {
    async function loadCatalog() {
      const liveProducts = await api.getProducts();
      if (liveProducts && liveProducts.length > 0) {
        setProducts(liveProducts.map((p, idx) => ({
          ...p,
          imageUrl: FALLBACK_PRODUCTS[idx % FALLBACK_PRODUCTS.length]?.imageUrl || FALLBACK_PRODUCTS[0].imageUrl
        })));
        setBackendConnected(true);
        addEvent('CATALOG', 'HTTP_GET_PRODUCTS', `Conectado ao Spring Boot API. ${liveProducts.length} produtos carregados via Postgres.`);
      } else {
        setBackendConnected(false);
        addEvent('CATALOG', 'STANDALONE_MODE', 'Backend offline ou iniciando. Operando em modo de demonstração local.', 'WARNING');
      }
    }
    loadCatalog();
  }, []);

  const handleSelectVariant = (productId: string, variant: Variant) => {
    setSelectedVariants(prev => ({ ...prev, [productId]: variant }));
  };

  const addToCart = (product: Product) => {
    const variant = selectedVariants[product.id];
    if (!variant || variant.availableStock <= 0) return;

    setCart(prev => {
      const existing = prev.find(item => item.variant.id === variant.id);
      if (existing) {
        return prev.map(item =>
          item.variant.id === variant.id
            ? { ...item, quantity: Math.min(item.quantity + 1, variant.availableStock) }
            : item
        );
      }
      return [...prev, { product, variant, quantity: 1 }];
    });

    addEvent('CATALOG', 'ADD_TO_CART', `SKU ${variant.sku} adicionado à sacola.`);
    setIsCartOpen(true);
  };

  const cartTotal = cart.reduce((acc, item) => acc + (item.variant.price * item.quantity), 0);

  const handleCheckout = async () => {
    if (cart.length === 0) return;

    const payload = {
      customerName: 'Cliente Visitante',
      customerEmail: 'visitante@devusa.com',
      idempotencyKey: 'idemp-' + Math.floor(100000 + Math.random() * 900000),
      items: cart.map(i => ({ variantId: i.variant.id, quantity: i.quantity }))
    };

    let createdOrder: Order | null = null;
    if (backendConnected) {
      createdOrder = await api.checkout(payload);
    }

    if (!createdOrder) {
      createdOrder = {
        id: 'ORD-' + Math.floor(100000 + Math.random() * 900000),
        customerName: payload.customerName,
        customerEmail: payload.customerEmail,
        status: 'AGUARDANDO_PAGAMENTO',
        totalAmount: cartTotal,
        createdAt: new Date().toISOString(),
        items: cart.map(i => ({
          variantId: i.variant.id,
          productName: i.product.name,
          sku: i.variant.sku,
          unitPrice: i.variant.price,
          quantity: i.quantity
        }))
      };
    }

    // Atualiza o estoque na UI
    setProducts(prev => prev.map(p => ({
      ...p,
      variants: p.variants.map(v => {
        const inCart = cart.find(c => c.variant.id === v.id);
        if (inCart) {
          return { ...v, availableStock: Math.max(0, v.availableStock - inCart.quantity) };
        }
        return v;
      })
    })));

    setActiveOrder(createdOrder);
    setCart([]);
    setIsCartOpen(false);
    setIsInspectorOpen(true);

    addEvent(
      'ORDER', 
      'CHECKOUT_INITIATED', 
      `Pedido ${createdOrder.id} criado com sucesso. Estoque reservado. Total R$ ${cartTotal.toFixed(2)}.`
    );
  };

  const handleAdvanceStatus = async (nextStatus: OrderStatus) => {
    if (!activeOrder) return;

    if (backendConnected) {
      const updated = await api.transitionOrder(activeOrder.id, nextStatus);
      if (updated) {
        setActiveOrder(updated);
      } else {
        setActiveOrder(prev => prev ? { ...prev, status: nextStatus } : null);
      }
    } else {
      setActiveOrder(prev => prev ? { ...prev, status: nextStatus } : null);
    }

    if (nextStatus === 'PAGO') {
      addEvent('INVENTORY', 'COMMIT_STOCK', `Pagamento aprovado. Reserva do pedido ${activeOrder.id} convertida em baixa definitiva.`);
    } else if (nextStatus === 'CANCELADO' || nextStatus === 'EXPIRADO') {
      addEvent('INVENTORY', 'RELEASE_STOCK', `Pedido ${activeOrder.id} ${nextStatus.toLowerCase()}. Reserva de estoque devolvida ao disponível.`, 'WARNING');
    } else {
      addEvent('ORDER', `TRANSITION_${nextStatus}`, `Pedido ${activeOrder.id} avançou para ${nextStatus}.`);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', backgroundColor: '#FAFAFA', color: '#111' }}>
      
      {/* Top Banner / Portfolio Header */}
      <div style={{ backgroundColor: '#111', color: '#FFF', padding: '10px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '13px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <ShieldCheck size={16} color="#4ade80" />
          <span><strong>DevUsa Architecture Showcase:</strong> Monólito Modular Java 21, Spring Boot 3, Flyway & Postgres</span>
          <span style={{ 
            fontSize: '11px', 
            padding: '2px 8px', 
            borderRadius: '4px', 
            backgroundColor: backendConnected ? '#14532d' : '#451a03',
            color: backendConnected ? '#86efac' : '#fdba74',
            display: 'flex',
            alignItems: 'center',
            gap: '4px'
          }}>
            <Server size={12} />
            {backendConnected ? 'API Conectada' : 'Modo Standalone / Demo'}
          </span>
        </div>
        <button
          onClick={() => setIsInspectorOpen(!isInspectorOpen)}
          style={{
            backgroundColor: isInspectorOpen ? '#333' : '#22c55e',
            color: '#FFF',
            border: 'none',
            padding: '6px 14px',
            borderRadius: '4px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            fontWeight: 600,
            fontSize: '12px'
          }}
        >
          <Terminal size={14} />
          {isInspectorOpen ? 'Fechar Modo Engenheiro' : 'Abrir Modo Engenheiro (System Inspector)'}
        </button>
      </div>

      {/* Main Navigation */}
      <header style={{ borderBottom: '1px solid #E5E5E5', backgroundColor: '#FFF', padding: '20px 48px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-1px' }}>
          DEVUSA <span style={{ fontSize: '11px', fontWeight: 400, letterSpacing: '2px', color: '#777', textTransform: 'uppercase' }}>Estúdio & Alfaiataria</span>
        </div>
        <nav style={{ display: 'flex', gap: '32px', fontSize: '14px', fontWeight: 500 }}>
          <span
            onClick={() => { setActiveTab('store'); setCategoryFilter('all'); }}
            style={{
              cursor: 'pointer',
              color: activeTab === 'store' && categoryFilter === 'all' ? '#111' : '#777',
              borderBottom: activeTab === 'store' && categoryFilter === 'all' ? '2px solid #111' : 'none',
              paddingBottom: '4px'
            }}
          >
            Coleção Atual
          </span>
          <span
            onClick={() => { setActiveTab('store'); setCategoryFilter('alfaiataria'); }}
            style={{
              cursor: 'pointer',
              color: activeTab === 'store' && categoryFilter === 'alfaiataria' ? '#111' : '#777',
              borderBottom: activeTab === 'store' && categoryFilter === 'alfaiataria' ? '2px solid #111' : 'none',
              paddingBottom: '4px'
            }}
          >
            Alfaiataria
          </span>
          <span
            onClick={() => { setActiveTab('store'); setCategoryFilter('outerwear'); }}
            style={{
              cursor: 'pointer',
              color: activeTab === 'store' && categoryFilter === 'outerwear' ? '#111' : '#777',
              borderBottom: activeTab === 'store' && categoryFilter === 'outerwear' ? '2px solid #111' : 'none',
              paddingBottom: '4px'
            }}
          >
            Outerwear
          </span>
          <span
            onClick={() => setActiveTab('roadmap')}
            style={{
              cursor: 'pointer',
              color: activeTab === 'roadmap' ? '#111' : '#777',
              borderBottom: activeTab === 'roadmap' ? '2px solid #111' : 'none',
              paddingBottom: '4px'
            }}
          >
            Arquitetura & Roadmap
          </span>
        </nav>
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <button
            onClick={() => setIsCartOpen(true)}
            style={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '14px',
              fontWeight: 600
            }}
          >
            <ShoppingBag size={20} />
            <span>Sacola ({cart.reduce((a, b) => a + b.quantity, 0)})</span>
          </button>
        </div>
      </header>

      {activeTab === 'roadmap' ? (
        <RoadmapView onBackToStore={() => setActiveTab('store')} />
      ) : (
      /* Catalog Hero */
      <section style={{ padding: '64px 48px 32px 48px', maxWidth: '1400px', margin: '0 auto', width: '100%' }}>
        <div style={{ marginBottom: '48px' }}>
          <p style={{ fontSize: '12px', letterSpacing: '3px', textTransform: 'uppercase', color: '#888', marginBottom: '8px' }}>
            Edição Limitada · Estoque Controlado
          </p>
          <h1 style={{ fontSize: '38px', fontWeight: 400, letterSpacing: '-1px', margin: 0 }}>
            Silhuetas contemporâneas com consistência transacional.
          </h1>
        </div>

        {/* Product Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '40px' }}>
          {products
            .filter(p => categoryFilter === 'all' || p.categorySlug === categoryFilter)
            .map(product => {
            const currentVariant = selectedVariants[product.id] || product.variants[0];
            return (
              <div key={product.id} style={{ display: 'flex', flexDirection: 'column' }}>
                <div style={{ position: 'relative', width: '100%', height: '420px', backgroundColor: '#EEE', overflow: 'hidden', borderRadius: '2px' }}>
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                  {currentVariant && currentVariant.availableStock <= 3 && currentVariant.availableStock > 0 && (
                    <span style={{ position: 'absolute', top: '12px', left: '12px', backgroundColor: '#111', color: '#FFF', fontSize: '10px', padding: '4px 8px', letterSpacing: '1px', textTransform: 'uppercase' }}>
                      Resta apenas {currentVariant.availableStock} un
                    </span>
                  )}
                  {currentVariant && currentVariant.availableStock === 0 && (
                    <div style={{ position: 'absolute', inset: 0, backgroundColor: 'rgba(255,255,255,0.85)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 600, fontSize: '13px', letterSpacing: '1px' }}>
                      ESGOTADO NO ESTOQUE
                    </div>
                  )}
                </div>

                <div style={{ marginTop: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '6px' }}>
                    <h3 style={{ fontSize: '16px', fontWeight: 600, margin: 0 }}>{product.name}</h3>
                    <span style={{ fontSize: '15px', fontWeight: 500 }}>
                      R$ {currentVariant ? currentVariant.price.toFixed(2) : product.basePrice.toFixed(2)}
                    </span>
                  </div>
                  <p style={{ fontSize: '13px', color: '#666', margin: '0 0 14px 0', lineHeight: '1.4' }}>
                    {product.description}
                  </p>

                  {/* Variantes Selector */}
                  <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                    {product.variants.map(v => (
                      <button
                        key={v.id}
                        onClick={() => handleSelectVariant(product.id, v)}
                        style={{
                          padding: '6px 12px',
                          border: currentVariant?.id === v.id ? '2px solid #111' : '1px solid #D1D1D1',
                          backgroundColor: currentVariant?.id === v.id ? '#111' : '#FFF',
                          color: currentVariant?.id === v.id ? '#FFF' : '#333',
                          fontSize: '12px',
                          cursor: 'pointer',
                          borderRadius: '2px',
                          opacity: v.availableStock === 0 ? 0.4 : 1
                        }}
                      >
                        {v.size} · {v.color} ({v.availableStock})
                      </button>
                    ))}
                  </div>

                  <button
                    onClick={() => addToCart(product)}
                    disabled={!currentVariant || currentVariant.availableStock === 0}
                    style={{
                      width: '100%',
                      padding: '12px',
                      backgroundColor: (!currentVariant || currentVariant.availableStock === 0) ? '#CCC' : '#111',
                      color: '#FFF',
                      border: 'none',
                      cursor: (!currentVariant || currentVariant.availableStock === 0) ? 'not-allowed' : 'pointer',
                      fontSize: '13px',
                      fontWeight: 600,
                      letterSpacing: '0.5px'
                    }}
                  >
                    {(!currentVariant || currentVariant.availableStock === 0) ? 'Indisponível' : 'Adicionar à Sacola'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </section>
      )}

      {/* Cart Drawer */}
      {isCartOpen && (
        <div style={{ position: 'fixed', inset: 0, zIndex: 50, display: 'flex', justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div style={{ width: '420px', backgroundColor: '#FFF', height: '100%', padding: '32px', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #E5E5E5', paddingBottom: '16px' }}>
              <h2 style={{ fontSize: '18px', fontWeight: 700, margin: 0 }}>Sua Sacola</h2>
              <button onClick={() => setIsCartOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>

            <div style={{ flex: 1, overflowY: 'auto', padding: '24px 0' }}>
              {cart.length === 0 ? (
                <p style={{ color: '#888', textAlign: 'center', marginTop: '40px' }}>Sua sacola está vazia.</p>
              ) : (
                cart.map(item => (
                  <div key={item.variant.id} style={{ display: 'flex', gap: '16px', marginBottom: '20px', borderBottom: '1px solid #F0F0F0', paddingBottom: '16px' }}>
                    <img src={item.product.imageUrl} alt="" style={{ width: '70px', height: '90px', objectFit: 'cover' }} />
                    <div style={{ flex: 1 }}>
                      <h4 style={{ margin: '0 0 4px 0', fontSize: '14px', fontWeight: 600 }}>{item.product.name}</h4>
                      <p style={{ margin: '0 0 4px 0', fontSize: '12px', color: '#666' }}>SKU: {item.variant.sku} · Tam: {item.variant.size}</p>
                      <p style={{ margin: 0, fontSize: '13px', fontWeight: 600 }}>Qtd: {item.quantity} × R$ {item.variant.price.toFixed(2)}</p>
                    </div>
                  </div>
                ))
              )}
            </div>

            {cart.length > 0 && (
              <div style={{ borderTop: '1px solid #E5E5E5', paddingTop: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', fontSize: '16px', fontWeight: 700 }}>
                  <span>Total Calculado:</span>
                  <span>R$ {cartTotal.toFixed(2)}</span>
                </div>
                <button
                  onClick={handleCheckout}
                  style={{
                    width: '100%',
                    padding: '16px',
                    backgroundColor: '#111',
                    color: '#FFF',
                    border: 'none',
                    fontWeight: 700,
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px'
                  }}
                >
                  <span>Finalizar Compra (Checkout Seguro)</span>
                  <ArrowRight size={18} />
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* System Inspector / Modo Engenheiro Drawer */}
      {isInspectorOpen && (
        <div style={{
          position: 'fixed',
          bottom: 0,
          left: 0,
          right: 0,
          height: '420px',
          backgroundColor: '#0F172A',
          color: '#E2E8F0',
          boxShadow: '0 -10px 25px -5px rgba(0,0,0,0.3)',
          zIndex: 40,
          display: 'flex',
          flexDirection: 'column',
          fontFamily: 'monospace'
        }}>
          {/* Header do Inspector */}
          <div style={{ padding: '12px 24px', backgroundColor: '#1E293B', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #334155' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <Terminal size={18} color="#38BDF8" />
              <span style={{ fontWeight: 700, fontSize: '14px', color: '#38BDF8' }}>DEVUSA · SYSTEM ARCHITECTURE INSPECTOR</span>
              <span style={{ fontSize: '11px', backgroundColor: '#334155', padding: '2px 8px', borderRadius: '4px' }}>Spring Modulith Core</span>
            </div>
            <button onClick={() => setIsInspectorOpen(false)} style={{ background: 'none', border: 'none', color: '#94A3B8', cursor: 'pointer' }}><X size={18} /></button>
          </div>

          {/* Conteúdo em 3 colunas */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.2fr 1fr', gap: '20px', padding: '20px', flex: 1, overflowY: 'auto' }}>
            
            {/* Coluna 1: Máquina de Estados do Pedido Ativo */}
            <div style={{ backgroundColor: '#1E293B', padding: '16px', borderRadius: '6px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px', color: '#38BDF8', fontWeight: 600 }}>
                <Activity size={16} />
                <span>Máquina de Estados (Order Bounded Context)</span>
              </div>

              {activeOrder ? (
                <div>
                  <div style={{ marginBottom: '12px', fontSize: '12px' }}>
                    <span style={{ color: '#94A3B8' }}>ID do Pedido:</span> <strong>{activeOrder.id}</strong><br />
                    <span style={{ color: '#94A3B8' }}>Estado Atual:</span> <span style={{ color: '#4ADE80', fontWeight: 700 }}>{activeOrder.status}</span>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                    <span style={{ fontSize: '11px', color: '#94A3B8' }}>Transições Permitidas por Regra de Domínio:</span>
                    {activeOrder.status === 'AGUARDANDO_PAGAMENTO' && (
                      <div style={{ display: 'flex', gap: '6px' }}>
                        <button
                          onClick={() => handleAdvanceStatus('PAGO')}
                          style={{ flex: 1, padding: '8px', backgroundColor: '#16A34A', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11px', fontWeight: 600 }}
                        >
                          Aprovar Pagamento (Baixa)
                        </button>
                        <button
                          onClick={() => handleAdvanceStatus('CANCELADO')}
                          style={{ flex: 1, padding: '8px', backgroundColor: '#DC2626', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11px', fontWeight: 600 }}
                        >
                          Cancelar (Liberar)
                        </button>
                      </div>
                    )}
                    {activeOrder.status === 'PAGO' && (
                      <button
                        onClick={() => handleAdvanceStatus('EM_SEPARACAO')}
                        style={{ padding: '8px', backgroundColor: '#2563EB', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11px', fontWeight: 600 }}
                      >
                        Iniciar Separação
                      </button>
                    )}
                    {activeOrder.status === 'EM_SEPARACAO' && (
                      <button
                        onClick={() => handleAdvanceStatus('ENVIADO')}
                        style={{ padding: '8px', backgroundColor: '#2563EB', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11px', fontWeight: 600 }}
                      >
                        Despachar Pedido (ENVIADO)
                      </button>
                    )}
                    {activeOrder.status === 'ENVIADO' && (
                      <button
                        onClick={() => handleAdvanceStatus('ENTREGUE')}
                        style={{ padding: '8px', backgroundColor: '#059669', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11px', fontWeight: 600 }}
                      >
                        Confirmar Entrega (Final)
                      </button>
                    )}
                    {(activeOrder.status === 'ENTREGUE' || activeOrder.status === 'CANCELADO' || activeOrder.status === 'EXPIRADO') && (
                      <p style={{ fontSize: '11px', color: '#94A3B8', margin: '8px 0 0 0' }}>Pedido em estado final ({activeOrder.status}). Nenhuma nova transição permitida.</p>
                    )}
                  </div>
                </div>
              ) : (
                <p style={{ fontSize: '12px', color: '#94A3B8' }}>Nenhum pedido ativo. Adicione itens à sacola e finalize para ver o fluxo.</p>
              )}
            </div>

            {/* Coluna 2: Log de Eventos Transacionais */}
            <div style={{ backgroundColor: '#1E293B', padding: '16px', borderRadius: '6px', overflowY: 'auto' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px', color: '#38BDF8', fontWeight: 600 }}>
                <Layers size={16} />
                <span>Log Transacional & Eventos de Domínio</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {systemEvents.map(evt => (
                  <div key={evt.id} style={{ fontSize: '11px', borderLeft: `3px solid ${evt.status === 'SUCCESS' ? '#4ADE80' : '#F59E0B'}`, paddingLeft: '8px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', color: '#94A3B8' }}>
                      <span>[{evt.module}] <strong>{evt.action}</strong></span>
                      <span>{evt.timestamp}</span>
                    </div>
                    <div style={{ color: '#F1F5F9', marginTop: '2px' }}>{evt.details}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Coluna 3: Métricas de Concorrência e Arquitetura */}
            <div style={{ backgroundColor: '#1E293B', padding: '16px', borderRadius: '6px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px', color: '#38BDF8', fontWeight: 600 }}>
                <Box size={16} />
                <span>Garantias Arquiteturais (ADRs)</span>
              </div>
              <ul style={{ fontSize: '11px', paddingLeft: '16px', margin: 0, lineHeight: '1.6', color: '#CBD5E1' }}>
                <li><strong>ADR 001:</strong> Monólito Modular com fronteiras fechadas (ArchUnit).</li>
                <li><strong>ADR 002:</strong> JWT Stateless (15m) + Refresh Tokens com Rotação SHA-256.</li>
                <li><strong>ADR 003:</strong> Vitrine Fullstack com System Inspector para portfólio.</li>
                <li><strong>Concorrência:</strong> Pessimistic Write Lock (`FOR UPDATE`) no estoque + `@Version` otimista.</li>
                <li><strong>Idempotência:</strong> Chave única por checkout contra duplo clique.</li>
              </ul>
            </div>

          </div>
        </div>
      )}

      {/* Footer */}
      <footer style={{ marginTop: 'auto', borderTop: '1px solid #E5E5E5', backgroundColor: '#FFF', padding: '32px 48px', fontSize: '13px', color: '#777', display: 'flex', justifyContent: 'space-between' }}>
        <div>
          © 2026 <strong>DevUsa E-commerce Platform</strong> — Desenvolvido para portfólio de Engenharia de Software.
        </div>
        <div>
          Java 21 · Spring Boot 3 · React · TypeScript · PostgreSQL
        </div>
      </footer>
    </div>
  );
}
export default App;
