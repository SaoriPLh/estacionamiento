/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class TipoTarifa {

    private int idTipoTarifa;
    private String descripcion;

    public TipoTarifa() {}

    public TipoTarifa(int idTipoTarifa, String descripcion) {
        this.idTipoTarifa = idTipoTarifa;
        this.descripcion = descripcion;
    }

    public int getIdTipoTarifa() { return idTipoTarifa; }
    public void setIdTipoTarifa(int idTipoTarifa) { this.idTipoTarifa = idTipoTarifa; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "TipoTarifa{idTipoTarifa=" + idTipoTarifa + ", descripcion=" + descripcion + "}";
    }
}