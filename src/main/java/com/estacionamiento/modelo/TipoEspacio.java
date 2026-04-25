/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class TipoEspacio {

    private int idTipoEspacio;
    private String descripcion;

    public TipoEspacio() {}

    public TipoEspacio(int idTipoEspacio, String descripcion) {
        this.idTipoEspacio = idTipoEspacio;
        this.descripcion = descripcion;
    }

    public int getIdTipoEspacio() { return idTipoEspacio; }
    public void setIdTipoEspacio(int idTipoEspacio) { this.idTipoEspacio = idTipoEspacio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "TipoEspacio{idTipoEspacio=" + idTipoEspacio + ", descripcion=" + descripcion + "}";
    }
}
