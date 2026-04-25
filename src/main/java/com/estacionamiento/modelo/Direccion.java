/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Direccion {

    private int idDireccion;
    private int idCiudad;
    private String calle;
    private String numeroExterior;
    private String codigoPostal;

    public Direccion() {}

    public Direccion(int idDireccion, int idCiudad, String calle, String numeroExterior, String codigoPostal) {
        this.idDireccion = idDireccion;
        this.idCiudad = idCiudad;
        this.calle = calle;
        this.numeroExterior = numeroExterior;
        this.codigoPostal = codigoPostal;
    }

    public int getIdDireccion() { return idDireccion; }
    public void setIdDireccion(int idDireccion) { this.idDireccion = idDireccion; }

    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumeroExterior() { return numeroExterior; }
    public void setNumeroExterior(String numeroExterior) { this.numeroExterior = numeroExterior; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    @Override
    public String toString() {
        return "Direccion{idDireccion=" + idDireccion + ", calle=" + calle + "}";
    }
}
