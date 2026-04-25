/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Ciudad {
    
    private int idCiudad;
    private int idEstado;
    private String nombre;

    public Ciudad() {}

    public Ciudad(int idCiudad, int idEstado, String nombre) {
        this.idCiudad = idCiudad;
        this.idEstado = idEstado;
        this.nombre = nombre;
    }

    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Ciudad{idCiudad=" + idCiudad + ", nombre=" + nombre + "}";
    }
}