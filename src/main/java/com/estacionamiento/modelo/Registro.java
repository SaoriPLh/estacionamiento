/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.Date;

public class Registro {

    private int idRegistro;
    private Vehiculo vehiculo;
    private Espacio espacio;
    private Persona persona;
    private EstadoRegistro estadoRegistro;
    private CodigoAcceso codigoAcceso;
    private Date horaEntrada;
    private Date horaSalida;
    private double monto;
    private Date fechaRegistro;

    public Registro() {}

    public Registro(int idRegistro, Vehiculo vehiculo, Espacio espacio,
                    Persona persona, EstadoRegistro estadoRegistro,
                    CodigoAcceso codigoAcceso, Date horaEntrada,
                    Date horaSalida, double monto, Date fechaRegistro) {
        this.idRegistro = idRegistro;
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.persona = persona;
        this.estadoRegistro = estadoRegistro;
        this.codigoAcceso = codigoAcceso;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.monto = monto;
        this.fechaRegistro = fechaRegistro;
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

    public Date getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(Date horaEntrada) { this.horaEntrada = horaEntrada; }

    public Date getHoraSalida() { return horaSalida; }
    public void setHoraSalida(Date horaSalida) { this.horaSalida = horaSalida; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "Registro{" +
                "id=" + idRegistro +
                ", monto=" + monto +
                '}';
    }
}