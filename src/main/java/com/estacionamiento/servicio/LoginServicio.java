/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.servicio;

import com.estacionamiento.dao.PersonaDAO;
import com.estacionamiento.dao.PermisosDAO;
import com.estacionamiento.dao.EstacionamientoDAO;
import com.estacionamiento.modelo.Persona;
import com.estacionamiento.modelo.Estacionamiento;
import com.estacionamiento.util.MisConstantes;
import java.util.List;
import java.util.ArrayList;

public class LoginServicio {
  
    private PersonaDAO personaDAO = new PersonaDAO();
    private PermisosDAO permisosDAO = new PermisosDAO();
    private EstacionamientoDAO estacionamientoDAO = new EstacionamientoDAO();

 
    public Persona autenticarUsuario(String user, String pass) {

        return personaDAO.autenticar(user, pass);
    }

    
    public List<Estacionamiento> obtenerSedesAutorizadas(Persona p) {
        if (p == null) {
            return new ArrayList<>();
        }

        
        if (p.getIdRol() == MisConstantes.ROL_ADMIN) {
            System.out.println("Acceso total al Administrador.");
            return estacionamientoDAO.listarTodo(); 
        } 
        

        System.out.println("Buscando sedes asignadas para el empleado: " + p.getNombre());
        return permisosDAO.obtenerEstacionamientosPorPersona(p.getIdPersona());
    }
}