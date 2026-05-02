/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa la tabla 'estacionamiento'.
 * Nota: La dirección es una referencia a otra tabla (FK).
 */
public class Estacionamiento {

    private int idEstacionamiento;
    private Empresa empresa;
    private Direccion direccion;
    private String nombre;
    private List<Permiso> personalAutorizado;
    private List<Espacio> espaciosEstacionamiento;
    private List<Tarifa> listaTarifas;  
    private boolean activo;
     public Estacionamiento() {
        this.personalAutorizado = new ArrayList<>();
        this.espaciosEstacionamiento = new ArrayList<>();
        this.listaTarifas = new ArrayList<>();
        activo = true;
    }

    public Estacionamiento(int idEstacionamiento, Empresa empresa,
                           Direccion direccion, String nombre) {
        this();
        this.idEstacionamiento = idEstacionamiento;
        this.empresa = empresa;
        this.direccion = direccion;
        this.nombre = nombre;
    }
    


    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public List<Permiso> getPersonalAutorizado() { return personalAutorizado; }
    public void setPersonalAutorizado(List<Permiso> personalAutorizado) { this.personalAutorizado = personalAutorizado; }
    
    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
    public void agregarEspacio(Espacio e) {
        if (e != null) {
            e.setEstacionamiento(this);
            this.espaciosEstacionamiento.add(e);
        }
    }
    public void agregarTarifa(Tarifa tarifa){
    if(tarifa!=null){
    this.listaTarifas.add(tarifa);
    }
    }
    public List<Espacio> getEspacios() { return espaciosEstacionamiento; }
    public void agregarPersonal(Permiso p) {
        if (p != null) {
            p.setEstacionamiento(this);
            this.personalAutorizado.add(p);
        }
    }

    public List<Espacio> getEspaciosEstacionamiento() {
        return espaciosEstacionamiento;
    }

    public void setEspaciosEstacionamiento(List<Espacio> espaciosEstacionamiento) {
        this.espaciosEstacionamiento = espaciosEstacionamiento;
    }

    public List<Tarifa> getListaTarifas() {
        return listaTarifas;
    }

    public void setListaTarifas(List<Tarifa> listaTarifas) {
        this.listaTarifas = listaTarifas;
    }
    
    public boolean getActivo () {
    return activo;
    } 
    
    public void setActivo(boolean activo) {
    
    this.activo = activo;
    }

    public void setEspacios(List<Permiso> permisos) { this.espaciosEstacionamiento = espaciosEstacionamiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}