/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class UnidadDescuento {

    private int idUnidadDescuento;
    private String tipoUnidad;
    private int factorConversionMinutos;

    public UnidadDescuento() {}

    public UnidadDescuento(int idUnidadDescuento, String tipoUnidad, int factorConversionMinutos) {
        this.idUnidadDescuento = idUnidadDescuento;
        this.tipoUnidad = tipoUnidad;
        this.factorConversionMinutos = factorConversionMinutos;
    }

    public int getIdUnidadDescuento() { return idUnidadDescuento; }
    public void setIdUnidadDescuento(int idUnidadDescuento) { this.idUnidadDescuento = idUnidadDescuento; }

    public String getTipoUnidad() { return tipoUnidad; }
    public void setTipoUnidad(String tipoUnidad) { this.tipoUnidad = tipoUnidad; }

    public int getFactorConversionMinutos() { return factorConversionMinutos; }
    public void setFactorConversionMinutos(int factorConversionMinutos) { this.factorConversionMinutos = factorConversionMinutos; }

    @Override
    public String toString() {
        return "UnidadDescuento{idUnidadDescuento=" + idUnidadDescuento + ", tipoUnidad=" + tipoUnidad + "}";
    }
}