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
public class TicketPension {
    private String nombreEstacionamiento;
    private String placa;
    private String horaEntrada;
    private String horaSalida;
    private double montoBase;
    private String  descuento;
    private double montoTotal;
    private String mensajeFinal;
    private String tipoCobro;
    private String nombreUsuario;
    private TarifaDAO tarifaDao =  new TarifaDAO();
    
    public void prepararDatos(Registro reg) {
         Tarifa tarifaRegistro = tarifaDao.buscarPorId(reg.getTarifa().getIdTarifa());
        this.montoBase = tarifaRegistro.getPrecio();
        this.montoTotal = reg.getMonto();
        
        
       
    }

    public TicketPension() {
    }

    public TicketPension(String nombreEstacionamiento, String placa, String horaEntrada, String horaSalida,  String nombreUsuario, String tipoCobro) {
        this.nombreEstacionamiento = nombreEstacionamiento;
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
       this.tipoCobro = tipoCobro;
         this.mensajeFinal = "Conserve este comprobante Gracias por su preferencia";
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

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    
}
