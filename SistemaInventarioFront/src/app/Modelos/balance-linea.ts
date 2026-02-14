export interface BalanceLinea {
  tipo: 'ACTIVO' | 'PASIVO' | 'PATRIMONIO';
  codigo: string;
  nombre: string;
  saldo: number; 
}
