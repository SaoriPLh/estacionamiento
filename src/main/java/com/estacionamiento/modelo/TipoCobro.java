/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class TipoCobro {
    private int idTipoCobro;
    private String nombre;

    public TipoCobro(int id_tipo_cobro, String nombre) {
        this.idTipoCobro = id_tipo_cobro;
        this.nombre = nombre;
    }

    public TipoCobro() {
    }

    public int getIdTipoCobro() {
        return idTipoCobro;
    }

    public void setIdTipoCobro(int idTipoCobro) {
        this.idTipoCobro = idTipoCobro;
    }

    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
}
