/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

public class EstadoRegistro {

    private int idEstadoRegistro;
    private String nombreEstado;

    public EstadoRegistro() {}

    public EstadoRegistro(int idEstadoRegistro, String nombreEstado) {
        this.idEstadoRegistro = idEstadoRegistro;
        this.nombreEstado = nombreEstado;
    }

    public int getIdEstadoRegistro() { return idEstadoRegistro; }
    public void setIdEstadoRegistro(int idEstadoRegistro) { this.idEstadoRegistro = idEstadoRegistro; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
}