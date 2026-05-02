/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class ResumenGananciasDTO {
    
    public int numeroPensiones = 0;
    public int numeroTarifaNormal= 0;
    public int numeroTarifaEspecial= 0;
    public int numeroTarifaConvenios= 0;
    public double gananciasTotales= 0;
    public double totalRegistros= 0;

 
    public ResumenGananciasDTO() {
    }
    
    

    public int getNumeroPensiones() {
        return numeroPensiones;
    }

    public void setNumeroPensiones(int numeroPensiones) {
        this.numeroPensiones = numeroPensiones;
    }

    public int getNumeroTarifaNormal() {
        return numeroTarifaNormal;
    }

    public void setNumeroTarifaNormal(int numeroTarifaNormal) {
        this.numeroTarifaNormal = numeroTarifaNormal;
    }

    public int getNumeroTarifaEspecial() {
        return numeroTarifaEspecial;
    }

    public void setNumeroTarifaEspecial(int numeroTarifaEspecial) {
        this.numeroTarifaEspecial = numeroTarifaEspecial;
    }

    public int getNumeroTarifaConvenios() {
        return numeroTarifaConvenios;
    }

    public void setNumeroTarifaConvenios(int numeroTarifaConvenios) {
        this.numeroTarifaConvenios = numeroTarifaConvenios;
    }

    public double getGananciasTotales() {
        return gananciasTotales;
    }

    public void setGananciasTotales(double gananciasTotales) {
        this.gananciasTotales = gananciasTotales;
    }

    
    
    
    
    
}
