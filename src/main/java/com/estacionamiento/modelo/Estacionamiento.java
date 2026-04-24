/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 * Modelo que representa la tabla 'estacionamiento'.
 * Nota: La dirección es una referencia a otra tabla (FK).
 */
public class Estacionamiento {
    private int idEstacionamiento;
    private int idDireccion; 
    private String nombre;

   
    public Estacionamiento() {
    }

    
    public Estacionamiento(int idEstacionamiento, int idDireccion, String nombre) {
        this.idEstacionamiento = idEstacionamiento;
        this.idDireccion = idDireccion;
        this.nombre = nombre;
    }

   
    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdDireccion() { return idDireccion; }
    public void setIdDireccion(int idDireccion) { this.idDireccion = idDireccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre; 
    }
}