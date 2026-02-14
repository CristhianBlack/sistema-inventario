import { BalanceLinea } from "./balance-linea";

export interface BalanceGeneral {

  activos: BalanceLinea[];
  pasivos: BalanceLinea[];
  totalActivos: number;
  totalPasivos: number
  patrimonio: number;
}
