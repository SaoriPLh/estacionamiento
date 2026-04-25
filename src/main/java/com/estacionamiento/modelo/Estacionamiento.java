/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Estacionamiento {

    private int idEstacionamiento;
    private int idEmpresa;
    private int idDireccion;
    private String nombre;

    public Estacionamiento() {}

    public Estacionamiento(int idEstacionamiento, int idEmpresa, int idDireccion, String nombre) {
        this.idEstacionamiento = idEstacionamiento;
        this.idEmpresa = idEmpresa;
        this.idDireccion = idDireccion;
        this.nombre = nombre;
    }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public int getIdDireccion() { return idDireccion; }
    public void setIdDireccion(int idDireccion) { this.idDireccion = idDireccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Estacionamiento{idEstacionamiento=" + idEstacionamiento + ", nombre=" + nombre + "}";
    }
}