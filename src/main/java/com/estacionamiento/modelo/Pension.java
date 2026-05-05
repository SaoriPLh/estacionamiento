package com.estacionamiento.modelo;

import java.util.Date;

public class Pension {

    private int idPension;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Tarifa tarifa;
    private Espacio espacio;
    private EstadoPension estadoPension;
    private CodigoAcceso codigo;   // nullable
    private Date fechaInicio;
    private Date fechaFin;
    private int idEstacionamiento;

    public Pension() {}

    public Pension(int idPension, Cliente cliente, Vehiculo vehiculo, Tarifa tarifa,
                   Espacio espacio, EstadoPension estadoPension, CodigoAcceso codigo,
                   Date fechaInicio, Date fechaFin) {
        this.idPension    = idPension;
        this.cliente      = cliente;
        this.vehiculo     = vehiculo;
        this.tarifa       = tarifa;
        this.espacio      = espacio;
        this.estadoPension = estadoPension;
        this.codigo       = codigo;
        this.fechaInicio  = fechaInicio;
        this.fechaFin     = fechaFin;
    }

    public int getIdPension() { return idPension; }
    public void setIdPension(int idPension) { this.idPension = idPension; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Tarifa getTarifa() { return tarifa; }
    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }

    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }

    public EstadoPension getEstadoPension() { return estadoPension; }
    public void setEstadoPension(EstadoPension estadoPension) { this.estadoPension = estadoPension; }

    public CodigoAcceso getCodigo() { return codigo; }
    public void setCodigo(CodigoAcceso codigo) { this.codigo = codigo; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    @Override
    public String toString() {
        return "Pension{id=" + idPension +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                ", espacio=" + (espacio != null ? espacio.getCodigo() : "null") +
                ", estado=" + (estadoPension != null ? estadoPension.getNombreEstado() : "null") + "}";
    }
}
