/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Tarifa {

    private int idTarifa;
    private int idEstacionamiento;
    private int idTipoTarifa;
    private int idUnidadDescuento;
    private String tipoCobro;
    private double precio;
    private double valorDescuento;

    public Tarifa() {}

    public Tarifa(int idTarifa, int idEstacionamiento, int idTipoTarifa, int idUnidadDescuento, String tipoCobro, double precio, double valorDescuento) {
        this.idTarifa = idTarifa;
        this.idEstacionamiento = idEstacionamiento;
        this.idTipoTarifa = idTipoTarifa;
        this.idUnidadDescuento = idUnidadDescuento;
        this.tipoCobro = tipoCobro;
        this.precio = precio;
        this.valorDescuento = valorDescuento;
    }

    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdTipoTarifa() { return idTipoTarifa; }
    public void setIdTipoTarifa(int idTipoTarifa) { this.idTipoTarifa = idTipoTarifa; }

    public int getIdUnidadDescuento() { return idUnidadDescuento; }
    public void setIdUnidadDescuento(int idUnidadDescuento) { this.idUnidadDescuento = idUnidadDescuento; }

    public String getTipoCobro() { return tipoCobro; }
    public void setTipoCobro(String tipoCobro) { this.tipoCobro = tipoCobro; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public double getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(double valorDescuento) { this.valorDescuento = valorDescuento; }

    @Override
    public String toString() {
        return "Tarifa{idTarifa=" + idTarifa + ", precio=" + precio + "}";
    }
}