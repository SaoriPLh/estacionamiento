/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

import java.util.List;

/**
 *
 * @author saori
 */
public class Persona {

    private int idPersona;
    private Empresa empresa;
    private Rol rol;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String username;
    private String password;
    private List<Permiso> permisos;
    public Persona() {}

    public Persona(int idPersona, Empresa empresa, Rol rol,
                   String nombre, String apellidoPaterno,
                   String apellidoMaterno, String username, String password) {
        this.idPersona = idPersona;
        this.empresa = empresa;
        this.rol = rol;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.username = username;
        this.password = password;
    }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Permiso> getPermisos() { return permisos; }
    public void setPermisos(List<Permiso> permisos) { this.permisos = permisos; }
    
    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}