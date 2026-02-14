import { MayorGeneral } from "./mayor-general";

export interface MayorGeneralPage {
    saldoInicial: number;
  movimientos: MayorGeneral[];
  totalRegistros: number;
}
