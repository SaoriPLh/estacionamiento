/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class Direccion {

    private int idDireccion;
    private Ciudad ciudad;
    private String calle;
    private String numeroExterior;
    private String codigoPostal;

    public Direccion() {}

    public Direccion(int idDireccion, Ciudad ciudad, String calle,
                     String numeroExterior, String codigoPostal) {
        this.idDireccion = idDireccion;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numeroExterior = numeroExterior;
        this.codigoPostal = codigoPostal;
    }

    public int getIdDireccion() { return idDireccion; }
    public void setIdDireccion(int idDireccion) { this.idDireccion = idDireccion; }

    public Ciudad getCiudad() { return ciudad; }
    public void setCiudad(Ciudad ciudad) { this.ciudad = ciudad; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumeroExterior() { return numeroExterior; }
    public void setNumeroExterior(String numeroExterior) { this.numeroExterior = numeroExterior; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}