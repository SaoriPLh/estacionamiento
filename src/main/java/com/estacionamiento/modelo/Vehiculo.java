/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

public class Vehiculo {

    private int idVehiculo;
    private Cliente cliente;
    private String placa;
    private String modelo;
    private String color;

    public Vehiculo() {}

    public Vehiculo(int idVehiculo, Cliente cliente, String placa, String modelo, String color) {
        this.idVehiculo = idVehiculo;
        this.cliente = cliente;
        this.placa = placa;
        this.modelo = modelo;
        this.color = color;
    }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "placa='" + placa + '\'' +
                '}';
    }
}