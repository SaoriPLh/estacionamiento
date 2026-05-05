package com.estacionamiento.modelo;

public class EstadoPension {

    private int idEstadoPension;
    private String nombreEstado;

    public EstadoPension() {}

    public EstadoPension(int idEstadoPension, String nombreEstado) {
        this.idEstadoPension = idEstadoPension;
        this.nombreEstado = nombreEstado;
    }

    public int getIdEstadoPension() { return idEstadoPension; }
    public void setIdEstadoPension(int idEstadoPension) { this.idEstadoPension = idEstadoPension; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

    @Override
    public String toString() {
        return "EstadoPension{id=" + idEstadoPension + ", nombre='" + nombreEstado + "'}";
    }
}
