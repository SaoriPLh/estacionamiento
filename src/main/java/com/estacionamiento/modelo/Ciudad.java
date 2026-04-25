/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class Ciudad {

    private int idCiudad;
    private Estado estado;
    private String nombre;

    public Ciudad() {}

    public Ciudad(int idCiudad, Estado estado, String nombre) {
        this.idCiudad = idCiudad;
        this.estado = estado;
        this.nombre = nombre;
    }

    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}