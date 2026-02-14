package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;
import java.util.List;

public class MayorGeneralPageDTO {

    private BigDecimal saldoInicial;
    private List<MayorGeneralDTO> movimientos;
    private long totalRegistros;

    private int totalPages;
    private int page;

    public MayorGeneralPageDTO() {
    }

    public MayorGeneralPageDTO(BigDecimal saldoInicial, List<MayorGeneralDTO> movimientos, long totalRegistros,
                               int totalPages, int page) {
        this.saldoInicial = saldoInicial;
        this.movimientos = movimientos;
        this.totalRegistros = totalRegistros;
        this.totalPages = totalPages;
        this.page = page;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public List<MayorGeneralDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MayorGeneralDTO> movimientos) {
        this.movimientos = movimientos;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
