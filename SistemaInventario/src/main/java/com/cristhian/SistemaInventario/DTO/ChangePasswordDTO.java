package com.cristhian.SistemaInventario.DTO;

public class ChangePasswordDTO {

       private String passwordActual;
    private String passwordNueva;

    public ChangePasswordDTO() {
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }
}

