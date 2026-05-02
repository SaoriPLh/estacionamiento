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
    private int cantidad_descuento;
    private TipoCobro tipoCobro;
    private double precio;
    private Double valorDescuento;
    

    public Tarifa() {}

    public Tarifa(int idTarifa, Estacionamiento estacionamiento, TipoTarifa tipoTarifa, UnidadDescuento unidadDescuento, int cantidad_descuento, TipoCobro tipoCobro, double precio, Double valorDescuento) {
        this.idTarifa = idTarifa;
        this.estacionamiento = estacionamiento;
        this.tipoTarifa = tipoTarifa;
        this.unidadDescuento = unidadDescuento;
        this.cantidad_descuento = cantidad_descuento;
        this.tipoCobro = tipoCobro;
        this.precio = precio;
        this.valorDescuento = valorDescuento;
    }

    public Tarifa(Estacionamiento estacionamiento, TipoTarifa tipoTarifa, TipoCobro tipoCobro, double precio, Double valorDescuento) {
        
        this.estacionamiento = estacionamiento;
        this.tipoTarifa = tipoTarifa;
        this.tipoCobro = tipoCobro;
        this.precio = precio;
        this.valorDescuento = valorDescuento;
    }

    public Tarifa(Estacionamiento estacionamiento, TipoTarifa tipoTarifa, UnidadDescuento unidadDescuento, int cantidad_descuento, TipoCobro tipoCobro, double precio) {
        this.estacionamiento = estacionamiento;
        this.tipoTarifa = tipoTarifa;
        this.unidadDescuento = unidadDescuento;
        this.cantidad_descuento = cantidad_descuento;
        this.tipoCobro = tipoCobro;
        this.precio = precio;
    }
    

    


    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(Estacionamiento estacionamiento) { this.estacionamiento = estacionamiento; }

    public TipoTarifa getTipoTarifa() { return tipoTarifa; }
    public void setTipoTarifa(TipoTarifa tipoTarifa) { this.tipoTarifa = tipoTarifa; }

    public UnidadDescuento getUnidadDescuento() { return unidadDescuento; }
    public void setUnidadDescuento(UnidadDescuento unidadDescuento) { this.unidadDescuento = unidadDescuento; }

    public TipoCobro getTipoCobro() { return tipoCobro; }
    public void setTipoCobro(TipoCobro tipoCobro) { this.tipoCobro = tipoCobro; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public Double getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(Double valorDescuento) { this.valorDescuento = valorDescuento; }

    public int getCantidad_descuento() {
        return cantidad_descuento;
    }

    public void setCantidad_descuento(int cantidad_descuento) {
        this.cantidad_descuento = cantidad_descuento;
    }

    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + idTarifa +
                ", tipoCobro='" + tipoCobro + '\'' +
                ", precio=" + precio +
                '}';
    }
}