/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class CodigoAcceso {
 
    private int idCodigo;
    private int idPersona;
    private int idEstacionamiento;
    private int idEstadoCodigo;
    private String codigo;
    private String fechaInicio;
    private String fechaFin;

    public CodigoAcceso() {}

    public CodigoAcceso(int idCodigo, int idPersona, int idEstacionamiento, int idEstadoCodigo, String codigo, String fechaInicio, String fechaFin) {
        this.idCodigo = idCodigo;
        this.idPersona = idPersona;
        this.idEstacionamiento = idEstacionamiento;
        this.idEstadoCodigo = idEstadoCodigo;
        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdCodigo() { return idCodigo; }
    public void setIdCodigo(int idCodigo) { this.idCodigo = idCodigo; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdEstadoCodigo() { return idEstadoCodigo; }
    public void setIdEstadoCodigo(int idEstadoCodigo) { this.idEstadoCodigo = idEstadoCodigo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    @Override
    public String toString() {
        return "CodigoAcceso{idCodigo=" + idCodigo + ", codigo=" + codigo + "}";
    }
}