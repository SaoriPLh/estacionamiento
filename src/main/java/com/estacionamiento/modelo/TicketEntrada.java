/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class TicketEntrada {
    private String nombreEstacionamiento;
    private String placa;
    private String fechaEntrada;
    private String horaEntrada;  
    private String notaAdvertencia;
    private String nombreUsuario;
    
    
    public TicketEntrada(String nombreEst, String placa) {
        this.nombreEstacionamiento = nombreEst;
        this.placa = placa;
        this.notaAdvertencia = "La empresa no se hace responsable por objetos de valor " +
                               "no declarados o daños causados por terceros.";
    }

    public TicketEntrada(String nombreEstacionamiento, String placa, String fechaEntrada, String horaEntrada, String nombreUsuario) {
        this.nombreEstacionamiento = nombreEstacionamiento;
        this.placa = placa;
        this.fechaEntrada = fechaEntrada;
        this.horaEntrada = horaEntrada;
        this.notaAdvertencia = "La empresa no se hace responsable por objetos de valor " +
                               "no declarados o daños causados por terceros.";
        this.nombreUsuario = nombreUsuario;
    }
    
    public TicketEntrada() {
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

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getNotaAdvertencia() {
        return notaAdvertencia;
    }

    public void setNotaAdvertencia(String notaAdvertencia) {
        this.notaAdvertencia = notaAdvertencia;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    
   
}