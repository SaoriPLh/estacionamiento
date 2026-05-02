/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Empresa {

    private int idEmpresa;
    private String nombreComercial;
    private String razonSocial;
    private String rfc;
    private String telefono;
    private String correoContacto;
    private Date fechaRegistro;
    private List<Estacionamiento> estacionamientos;
    // Constructor vacío
    public Empresa() {
        this.estacionamientos = new ArrayList<>();
    }

    // Constructor completo
    public Empresa(int idEmpresa, String nombreComercial, String razonSocial,
                   String rfc, String telefono, String correoContacto, Date fechaRegistro) {
        this();
        this.idEmpresa = idEmpresa;
        this.nombreComercial = nombreComercial;
        this.razonSocial = razonSocial;
        this.rfc = rfc;
        this.telefono = telefono;
        this.correoContacto = correoContacto;
        this.fechaRegistro = fechaRegistro;
    }

    // GETTERS Y SETTERS

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoContacto() { return correoContacto; }
    public void setCorreoContacto(String correoContacto) { this.correoContacto = correoContacto; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<Estacionamiento> getEstacionamientos() {
        return estacionamientos;
    }

  
    public void setEstacionamientos(List<Estacionamiento> estacionamientos) {
        this.estacionamientos = estacionamientos;
    }

   
    public void agregarEstacionamiento(Estacionamiento est) {
        if (est != null) {
            
            est.setEmpresa(this);
            this.estacionamientos.add(est);
        }
    }

    public void eliminarEstacionamiento(int idEstacionamiento) {
        this.estacionamientos.removeIf(e -> e.getIdEstacionamiento() == idEstacionamiento);
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "idEmpresa=" + idEmpresa +
                ", nombreComercial='" + nombreComercial + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", rfc='" + rfc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correoContacto='" + correoContacto + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}