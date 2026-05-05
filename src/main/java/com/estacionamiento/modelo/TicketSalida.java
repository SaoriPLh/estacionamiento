/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import com.estacionamiento.dao.TarifaDAO;

/**
 *
 * @author saori
 */
public class TicketSalida {
    private String nombreEstacionamiento;
    private String placa;
    private String horaEntrada;
    private String horaSalida;
    private double montoBase;
    private double cargoExtra; 
    private String  descuento;
    private double montoTotal;
    private String mensajeFinal;
    private String nombreUsuario;
    private String tipoDescuento;
    private TarifaDAO tarifaDao =  new TarifaDAO();
    
    

    public TicketSalida() {
    }

    public TicketSalida(String nombreEstacionamiento, String placa, String horaEntrada, String horaSalida,  String nombreUsuario) {
        this.nombreEstacionamiento = nombreEstacionamiento;
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
       
         this.mensajeFinal = "¡Gracias por su preferencia!";
        this.nombreUsuario = nombreUsuario;
    }

    
    public String getNombreEstacionamiento() {
        return nombreEstacionamiento;
    }

    public void setNombreEstacionamiento(String nombreEstacionamiento) {
        this.nombreEstacionamiento = nombreEstacionamiento;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public double getMontoBase() {
        return montoBase;
    }

    public void setMontoBase(double montoBase) {
        this.montoBase = montoBase;
    }

    public double getCargoExtra() {
        return cargoExtra;
    }

    public void setCargoExtra(double cargoExtra) {
        this.cargoExtra = cargoExtra;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }

    public void setMensajeFinal(String mensajeFinal) {
        this.mensajeFinal = mensajeFinal;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    
}
