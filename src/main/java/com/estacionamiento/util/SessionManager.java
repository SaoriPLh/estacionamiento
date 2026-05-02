/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.util;

import com.estacionamiento.modelo.*;

public class SessionManager {
    
    
    private static SessionManager instance;
    
    
    private Persona usuarioLogueado;
    private Estacionamiento estacionamiento;
    private Empresa empresa;
    
    private SessionManager() {}

    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setEstacionamiento(Estacionamiento estacionamiento){
        this.estacionamiento = estacionamiento;
    }
    public Estacionamiento getEstacionamiento (){return this.estacionamiento;} 
        public void setEmpresa(Empresa empresa){
        this.empresa = empresa;
    }
    public void setUsuario(Persona persona) {
        this.usuarioLogueado = persona;
    }

    public Persona getUsuario() {
        return usuarioLogueado;
    }
    public Empresa getEmpresa () {
        return empresa;
    }

    
    public void cerrarSesion() {
        this.usuarioLogueado = null;
        this.estacionamiento = null;
    }
 
}