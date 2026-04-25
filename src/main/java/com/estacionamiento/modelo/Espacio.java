/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author garci
 */
public class Espacio {
    
    private int idEspacio;
    private int idEstacionamiento;
    private int idTipoEspacio;
    private int idEstadoEspacio;
    private String codigo;

    public Espacio() {}

    public Espacio(int idEspacio, int idEstacionamiento, int idTipoEspacio, int idEstadoEspacio, String codigo) {
        this.idEspacio = idEspacio;
        this.idEstacionamiento = idEstacionamiento;
        this.idTipoEspacio = idTipoEspacio;
        this.idEstadoEspacio = idEstadoEspacio;
        this.codigo = codigo;
    }

    public int getIdEspacio() { return idEspacio; }
    public void setIdEspacio(int idEspacio) { this.idEspacio = idEspacio; }

    public int getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(int idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }

    public int getIdTipoEspacio() { return idTipoEspacio; }
    public void setIdTipoEspacio(int idTipoEspacio) { this.idTipoEspacio = idTipoEspacio; }

    public int getIdEstadoEspacio() { return idEstadoEspacio; }
    public void setIdEstadoEspacio(int idEstadoEspacio) { this.idEstadoEspacio = idEstadoEspacio; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @Override
    public String toString() {
        return "Espacio{idEspacio=" + idEspacio + ", codigo=" + codigo + "}";
    }
}