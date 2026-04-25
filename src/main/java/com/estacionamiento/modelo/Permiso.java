/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.Date;

public class Permiso {

    private int idPermiso;
    private Estacionamiento estacionamiento;
    private Persona persona;
    private EstadoPermiso estadoPermiso;
    private Date fechaAsignacion;

    public Permiso() {}

    public Permiso(int idPermiso, Estacionamiento estacionamiento,
                   Persona persona, EstadoPermiso estadoPermiso,
                   Date fechaAsignacion) {
        this.idPermiso = idPermiso;
        this.estacionamiento = estacionamiento;
        this.persona = persona;
        this.estadoPermiso = estadoPermiso;
        this.fechaAsignacion = fechaAsignacion;
    }

    public int getIdPermiso() { return idPermiso; }
    public void setIdPermiso(int idPermiso) { this.idPermiso = idPermiso; }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(Estacionamiento estacionamiento) { this.estacionamiento = estacionamiento; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public EstadoPermiso getEstadoPermiso() { return estadoPermiso; }
    public void setEstadoPermiso(EstadoPermiso estadoPermiso) { this.estadoPermiso = estadoPermiso; }

    public Date getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Date fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    @Override
    public String toString() {
        return "Permiso{" +
                "id=" + idPermiso +
                '}';
    }
}