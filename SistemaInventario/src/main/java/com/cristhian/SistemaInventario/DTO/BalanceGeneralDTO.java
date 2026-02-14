package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;
import java.util.List;

public class BalanceGeneralDTO {

    private BigDecimal totalActivos;
    private BigDecimal totalPasivos;
    private BigDecimal patrimonio;

    private List<BalanceLineaDTO> activos;
    private List<BalanceLineaDTO> pasivos;

    public BalanceGeneralDTO() {}

    public BalanceGeneralDTO(
            BigDecimal totalActivos,
            BigDecimal totalPasivos,
            BigDecimal patrimonio,
            List<BalanceLineaDTO> lineas
    ) {
        this.totalActivos = totalActivos;
        this.totalPasivos = totalPasivos;
        this.patrimonio = patrimonio;

        this.activos = lineas.stream()
                .filter(l -> l.getTipo().equals("ACTIVO"))
                .toList();

        this.pasivos = lineas.stream()
                .filter(l -> l.getTipo().equals("PASIVO"))
                .toList();
    }

    public BigDecimal getTotalActivos() {
        return totalActivos;
    }

    public void setTotalActivos(BigDecimal totalActivos) {
        this.totalActivos = totalActivos;
    }

    public BigDecimal getTotalPasivos() {
        return totalPasivos;
    }

    public void setTotalPasivos(BigDecimal totalPasivos) {
        this.totalPasivos = totalPasivos;
    }

    public BigDecimal getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(BigDecimal patrimonio) {
        this.patrimonio = patrimonio;
    }

    public List<BalanceLineaDTO> getActivos() {
        return activos;
    }

    public void setActivos(List<BalanceLineaDTO> activos) {
        this.activos = activos;
    }

    public List<BalanceLineaDTO> getPasivos() {
        return pasivos;
    }

    public void setPasivos(List<BalanceLineaDTO> pasivos) {
        this.pasivos = pasivos;
    }
}
