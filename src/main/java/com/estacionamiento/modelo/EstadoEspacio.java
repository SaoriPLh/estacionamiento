/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */

public class EstadoEspacio {

    private int idEstadoEspacio;
    private String descripcion;

    public EstadoEspacio() {}

    public EstadoEspacio(int idEstadoEspacio, String descripcion) {
        this.idEstadoEspacio = idEstadoEspacio;
        this.descripcion = descripcion;
    }

    public int getIdEstadoEspacio() { return idEstadoEspacio; }
    public void setIdEstadoEspacio(int idEstadoEspacio) { this.idEstadoEspacio = idEstadoEspacio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "EstadoEspacio{idEstadoEspacio=" + idEstadoEspacio + ", descripcion=" + descripcion + "}";
    }
}
