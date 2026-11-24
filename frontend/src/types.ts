export interface Variant {
  id: string;
  sku: string;
  size: string;
  color: string;
  price: number;
  availableStock: number;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  basePrice: number;
  categoryName: string;
  categorySlug: string;
  imageUrl?: string;
  variants: Variant[];
}

export interface CartItem {
  product: Product;
  variant: Variant;
  quantity: number;
}

export type OrderStatus =
  | 'AGUARDANDO_PAGAMENTO'
  | 'PAGO'
  | 'EM_SEPARACAO'
  | 'ENVIADO'
  | 'ENTREGUE'
  | 'CANCELADO'
  | 'EXPIRADO';

export interface Order {
  id: string;
  customerName: string;
  customerEmail: string;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  items: {
    variantId: string;
    productName: string;
    sku: string;
    unitPrice: number;
    quantity: number;
  }[];
}

export interface SystemEvent {
  id: string;
  timestamp: string;
  module: 'CATALOG' | 'INVENTORY' | 'ORDER' | 'IDENTITY';
  action: string;
  details: string;
  status: 'SUCCESS' | 'WARNING' | 'ERROR';
}
