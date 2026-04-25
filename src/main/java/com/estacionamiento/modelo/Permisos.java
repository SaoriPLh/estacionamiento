/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Permisos {
    
    private int idPermiso;
    private int idEstacionamiento;
    private int idPersona;
    private int idEstadoPermiso;
    private String fechaAsignacion;

    public Permisos() {}

    public Permisos(int idPermiso, int idEstacionamiento, int idPersona, int idEstadoPermiso, String fechaAsignacion) {
        this.idPermiso = idPermiso;
        this.idEstacionamiento = idEstacionamiento;
        this.idPersona = idPersona;
        this.idEstadoPermiso = idEstadoPermiso;
        this.fechaAsignacion = fechaAsignacion;
    }

    public int getIdPermiso() { return idPermiso; }
    public void setIdPermiso(int idPermiso) { this.idPermiso = idPermiso; }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public int getIdEstadoPermiso() { return idEstadoPermiso; }
    public void setIdEstadoPermiso(int idEstadoPermiso) { this.idEstadoPermiso = idEstadoPermiso; }

    public String getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(String fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    @Override
    public String toString() {
        return "Permisos{idPermiso=" + idPermiso + ", idPersona=" + idPersona + "}";
    }
}