/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */

public class EstadoPermiso {

    private int idEstadoPermiso;
    private String nombreEstado;

    public EstadoPermiso() {}

    public EstadoPermiso(int idEstadoPermiso, String nombreEstado) {
        this.idEstadoPermiso = idEstadoPermiso;
        this.nombreEstado = nombreEstado;
    }

    public int getIdEstadoPermiso() { return idEstadoPermiso; }
    public void setIdEstadoPermiso(int idEstadoPermiso) { this.idEstadoPermiso = idEstadoPermiso; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

    @Override
    public String toString() {
        return "EstadoPermiso{idEstadoPermiso=" + idEstadoPermiso + ", nombreEstado=" + nombreEstado + "}";
    }
}