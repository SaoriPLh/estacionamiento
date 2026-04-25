/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */



public class Tarifa {

    private int idTarifa;
    private Estacionamiento estacionamiento;
    private TipoTarifa tipoTarifa;
    private UnidadDescuento unidadDescuento;
    private String tipoCobro;
    private double precio;
    private double valorDescuento;

    public Tarifa() {}

    public Tarifa(int idTarifa, Estacionamiento estacionamiento,
                  TipoTarifa tipoTarifa, UnidadDescuento unidadDescuento,
                  String tipoCobro, double precio, double valorDescuento) {
        this.idTarifa = idTarifa;
        this.estacionamiento = estacionamiento;
        this.tipoTarifa = tipoTarifa;
        this.unidadDescuento = unidadDescuento;
        this.tipoCobro = tipoCobro;
        this.precio = precio;
        this.valorDescuento = valorDescuento;
    }

    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(Estacionamiento estacionamiento) { this.estacionamiento = estacionamiento; }

    public TipoTarifa getTipoTarifa() { return tipoTarifa; }
    public void setTipoTarifa(TipoTarifa tipoTarifa) { this.tipoTarifa = tipoTarifa; }

    public UnidadDescuento getUnidadDescuento() { return unidadDescuento; }
    public void setUnidadDescuento(UnidadDescuento unidadDescuento) { this.unidadDescuento = unidadDescuento; }

    public String getTipoCobro() { return tipoCobro; }
    public void setTipoCobro(String tipoCobro) { this.tipoCobro = tipoCobro; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public double getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(double valorDescuento) { this.valorDescuento = valorDescuento; }

    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + idTarifa +
                ", tipoCobro='" + tipoCobro + '\'' +
                ", precio=" + precio +
                '}';
    }
}