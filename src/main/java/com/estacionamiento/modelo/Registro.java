/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.time.LocalDateTime;
import java.util.Date;

public class Registro {

    private int idRegistro;
    private Tarifa tarifa;

  
    private Vehiculo vehiculo;
    private Espacio espacio;
    private Persona persona;
    private EstadoRegistro estadoRegistro;
    private CodigoAcceso codigoAcceso;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fecha_fin_plan;
    private double monto;
 

    public Registro() {}

    public Registro(int idRegistro, Tarifa tarifa, Vehiculo vehiculo, Espacio espacio, Persona persona, EstadoRegistro estadoRegistro, CodigoAcceso codigoAcceso, LocalDateTime horaEntrada, LocalDateTime horaSalida, LocalDateTime fechaRegistro, LocalDateTime fecha_fin_plan, double monto) {
        this.idRegistro = idRegistro;
        this.tarifa = tarifa;
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.persona = persona;
        this.estadoRegistro = estadoRegistro;
        this.codigoAcceso = codigoAcceso;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.fechaRegistro = fechaRegistro;
        this.fecha_fin_plan = fecha_fin_plan;
        this.monto = monto;
    }

 

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public EstadoRegistro getEstadoRegistro() { return estadoRegistro; }
    public void setEstadoRegistro(EstadoRegistro estadoRegistro) { this.estadoRegistro = estadoRegistro; }

    public CodigoAcceso getCodigoAcceso() { return codigoAcceso; }
    public void setCodigoAcceso(CodigoAcceso codigoAcceso) { this.codigoAcceso = codigoAcceso; }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFecha_fin_plan() {
        return fecha_fin_plan;
    }

    public void setFecha_fin_plan(LocalDateTime fecha_fin_plan) {
        this.fecha_fin_plan = fecha_fin_plan;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Tarifa getTarifa() {
        return tarifa;
    }


    public void setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
    }
    @Override
    public String toString() {
        return "Registro{" +
                "id=" + idRegistro +
                ", monto=" + monto +
                '}';
    }
}