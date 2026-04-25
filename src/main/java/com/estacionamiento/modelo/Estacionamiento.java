/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.List;

/**
 * Modelo que representa la tabla 'estacionamiento'.
 * Nota: La dirección es una referencia a otra tabla (FK).
 */
public class Estacionamiento {

    private int idEstacionamiento;
    private Empresa empresa;
    private Direccion direccion;
    private String nombre;
    private List<Permiso> personalAutorizado;

    public Estacionamiento() {}

    public Estacionamiento(int idEstacionamiento, Empresa empresa,
                           Direccion direccion, String nombre) {
        this.idEstacionamiento = idEstacionamiento;
        this.empresa = empresa;
        this.direccion = direccion;
        this.nombre = nombre;
    }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public List<Permiso> getPersonalAutorizado() { return personalAutorizado; }
    public void setPersonalAutorizado(List<Permiso> personalAutorizado) { this.personalAutorizado = personalAutorizado; }
    
    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}