/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Registro {

    private int idRegistro;
    private int idVehiculo;
    private int idEspacio;
    private int idPersona;
    private int idEstadoRegistro;
    private int idCodigo;
    private String horaEntrada;
    private String horaSalida;
    private double monto;
    private String fechaRegistro;

    public Registro() {}

    public Registro(int idRegistro, int idVehiculo, int idEspacio, int idPersona, int idEstadoRegistro, int idCodigo, String horaEntrada, String horaSalida, double monto, String fechaRegistro) {
        this.idRegistro = idRegistro;
        this.idVehiculo = idVehiculo;
        this.idEspacio = idEspacio;
        this.idPersona = idPersona;
        this.idEstadoRegistro = idEstadoRegistro;
        this.idCodigo = idCodigo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.monto = monto;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public int getIdEspacio() { return idEspacio; }
    public void setIdEspacio(int idEspacio) { this.idEspacio = idEspacio; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public int getIdEstadoRegistro() { return idEstadoRegistro; }
    public void setIdEstadoRegistro(int idEstadoRegistro) { this.idEstadoRegistro = idEstadoRegistro; }

    public int getIdCodigo() { return idCodigo; }
    public void setIdCodigo(int idCodigo) { this.idCodigo = idCodigo; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "Registro{idRegistro=" + idRegistro + ", horaEntrada=" + horaEntrada + "}";
    }
}