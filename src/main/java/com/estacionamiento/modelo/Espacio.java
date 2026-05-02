/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

public class Espacio {

    private int idEspacio;
    private Estacionamiento estacionamiento;
    private TipoEspacio tipoEspacio;
    private EstadoEspacio estadoEspacio;
    private String codigo;

    public Espacio() {}

    public Espacio(int idEspacio, Estacionamiento estacionamiento,
                   TipoEspacio tipoEspacio, EstadoEspacio estadoEspacio,
                   String codigo) {
        this.idEspacio = idEspacio;
        this.estacionamiento = estacionamiento;
        this.tipoEspacio = tipoEspacio;
        this.estadoEspacio = estadoEspacio;
        this.codigo = codigo;
    }
    
        public Espacio(Estacionamiento estacionamiento,
                   TipoEspacio tipoEspacio, EstadoEspacio estadoEspacio,
                   String codigo) {
        
        this.estacionamiento = estacionamiento;
        this.tipoEspacio = tipoEspacio;
        this.estadoEspacio = estadoEspacio;
        this.codigo = codigo;
    }

    public int getIdEspacio() { return idEspacio; }
    public void setIdEspacio(int idEspacio) { this.idEspacio = idEspacio; }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(Estacionamiento estacionamiento) { this.estacionamiento = estacionamiento; }

    public TipoEspacio getTipoEspacio() { return tipoEspacio; }
    public void setTipoEspacio(TipoEspacio tipoEspacio) { this.tipoEspacio = tipoEspacio; }

    public EstadoEspacio getEstadoEspacio() { return estadoEspacio; }
    public void setEstadoEspacio(EstadoEspacio estadoEspacio) { this.estadoEspacio = estadoEspacio; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @Override
    public String toString() {
        return "Espacio{" +
                "codigo='" + codigo + '\'' +
                '}';
    }
}