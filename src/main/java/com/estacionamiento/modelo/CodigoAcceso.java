/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.Date;

public class CodigoAcceso {

    private int idCodigo;
    private Persona persona;
    private Estacionamiento estacionamiento;
    private EstadoCodigoAcceso estadoCodigo;
    private String codigo;
    private Date fechaInicio;
    private Date fechaFin;

    public CodigoAcceso() {}

    public CodigoAcceso(int idCodigo, Persona persona,
                        Estacionamiento estacionamiento,
                        EstadoCodigoAcceso estadoCodigo,
                        String codigo, Date fechaInicio, Date fechaFin) {
        this.idCodigo = idCodigo;
        this.persona = persona;
        this.estacionamiento = estacionamiento;
        this.estadoCodigo = estadoCodigo;
        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdCodigo() { return idCodigo; }
    public void setIdCodigo(int idCodigo) { this.idCodigo = idCodigo; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(Estacionamiento estacionamiento) { this.estacionamiento = estacionamiento; }

    public EstadoCodigoAcceso getEstadoCodigo() { return estadoCodigo; }
    public void setEstadoCodigo(EstadoCodigoAcceso estadoCodigo) { this.estadoCodigo = estadoCodigo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    @Override
    public String toString() {
        return "CodigoAcceso{" +
                "codigo='" + codigo + '\'' +
                '}';
    }
}