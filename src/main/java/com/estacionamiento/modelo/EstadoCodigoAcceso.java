/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

public class EstadoCodigoAcceso {

    private int idEstadoCodigo;
    private String nombreEstado;

    public EstadoCodigoAcceso() {}

    public EstadoCodigoAcceso(int idEstadoCodigo, String nombreEstado) {
        this.idEstadoCodigo = idEstadoCodigo;
        this.nombreEstado = nombreEstado;
    }

    public int getIdEstadoCodigo() { return idEstadoCodigo; }
    public void setIdEstadoCodigo(int idEstadoCodigo) { this.idEstadoCodigo = idEstadoCodigo; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
}
