/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.modelo;

/**
 *
 * @author saori
 */
public class Persona {
    
    private int idPersona;
    private int idRol;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String username;
    private String password;
    
    public Persona (){};
    
    public Persona(int idPersona, int idRol, String nombre, String apellidoPaterno, String apellidoMaterno, String username, String password) {
        
        this.idPersona = idPersona;
        this.idRol = idRol;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.username = username;
        this.password = password;
    }
    
    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // 4. Método toString (Para depuración, SIN mostrar el password por seguridad)
    @Override
    public String toString() {
        return "Persona{" +
                "id=" + idPersona +
                ", nombre='" + nombre + '\'' +
                ", rol=" + idRol +
                ", username='" + username + '\'' +
                '}';
    }
}
