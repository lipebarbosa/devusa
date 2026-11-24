import type { Product, Order, OrderStatus } from './types';

const API_BASE = '/api/v1';

export const api = {
  async getProducts(): Promise<Product[] | null> {
    try {
      const res = await fetch(`${API_BASE}/catalog/products`);
      if (!res.ok) return null;
      return await res.json();
    } catch {
      return null;
    }
  },

  async seedDemoData(): Promise<boolean> {
    try {
      const res = await fetch(`${API_BASE}/catalog/seed`, { method: 'POST' });
      return res.ok;
    } catch {
      return false;
    }
  },

  async checkout(payload: {
    customerName: string;
    customerEmail: string;
    idempotencyKey?: string;
    items: { variantId: string; quantity: number }[];
  }): Promise<Order | null> {
    try {
      const res = await fetch(`${API_BASE}/orders/checkout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) return null;
      return await res.json();
    } catch {
      return null;
    }
  },

  async transitionOrder(orderId: string, nextStatus: OrderStatus): Promise<Order | null> {
    try {
      const res = await fetch(`${API_BASE}/orders/${orderId}/transition`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nextStatus })
      });
      if (!res.ok) return null;
      return await res.json();
    } catch {
      return null;
    }
  }
};
