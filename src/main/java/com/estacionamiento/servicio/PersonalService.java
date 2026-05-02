package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.PasswordHasher;
import com.estacionamiento.util.SessionManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio para gestionar el ciclo de vida del personal
 * @author saori
 */
public class PersonalService {

    private PersonaDAO personaDAO = new PersonaDAO();
    private PermisosDAO permisosDAO = new PermisosDAO();
    private EstacionamientoDAO estDAO = new EstacionamientoDAO();
    
   
    public Persona construirPersonal(Empresa empresa, Rol rol, String nombre,
                                     String apellidoPaterno, String apellidoMaterno, 
                                     String username,String password, boolean requiereCambio,Double salario) {
        
        // Retornamos el objeto para ser usado en el Front o mandado a guardar
        return new Persona(empresa, rol, nombre, apellidoPaterno, apellidoMaterno, username, password,requiereCambio,salario);
    }

    /*
     Verifica si la persona tiene rol de administrador.
     */
    public boolean esAdmin(Persona p) {
       //validadores en front para q efectivamente se cree bieni el objeto Persona
        return p.getRol().getIdRol() == MisConstantes.ROL_ADMIN;
    }

    
    public boolean añadirAdministradorYPermisos(Persona p) {
        String passHasheada = PasswordHasher.hash(p.getPassword());
    p.setPassword(passHasheada);
        Persona personaRegistrada = personaDAO.insertar(p);
        
        if (personaRegistrada == null || personaRegistrada.getIdPersona() <= 0) {
            return false;
        }

        // Asigna todos los permisos
        List<Estacionamiento> todos = estDAO.listarPorEmpresa(p.getEmpresa().getIdEmpresa());
        for (Estacionamiento e : todos) {
            int idPermisoGenerado = permisosDAO.asignarPermisos(personaRegistrada.getIdPersona(), e.getIdEstacionamiento());
            
            EstadoPermiso estadoPermiso = new EstadoPermiso(MisConstantes.PERMISO_ACTIVO, "activo");
            Permiso nuevoP = new Permiso(idPermisoGenerado, e, personaRegistrada, estadoPermiso, new Date());
            
           
            personaRegistrada.agregarPermiso(nuevoP);
        }
        return true;
    }

    
    public boolean añadirEmpleadoYPermisos(Persona p, Estacionamiento estacionamiento) {
        String passHasheada = PasswordHasher.hash(p.getPassword());
    p.setPassword(passHasheada);
        Persona personaRegistrada = personaDAO.insertar(p);
        
        if (personaRegistrada == null || personaRegistrada.getIdPersona() <= 0) {
            return false;
        }

        int idPermisoGenerado = permisosDAO.asignarPermisos(personaRegistrada.getIdPersona(), estacionamiento.getIdEstacionamiento());
        
        EstadoPermiso estadoPermiso = new EstadoPermiso(MisConstantes.PERMISO_ACTIVO, "activo");
        Permiso nuevoP = new Permiso(idPermisoGenerado, estacionamiento, personaRegistrada, estadoPermiso, new Date());

      
        personaRegistrada.agregarPermiso(nuevoP);
        
        return true;
    }
    

    public boolean darDeBajaAccesoPersonal(int idPersona) {
        if (idPersona <= 0) return false;

        // El DAO pone todos los permisos de esta persona en estado REVOCADO
        return permisosDAO.revocarTodosLosPermisos(idPersona);
    }

   
    public boolean cambiarEmpleadoDeSede(int idPersona, int idNuevaSede) {
        if (idPersona <= 0 || idNuevaSede <= 0) return false;

      
        permisosDAO.revocarTodosLosPermisos(idPersona);
        
        int nuevoIdPermiso = permisosDAO.asignarPermisos(idPersona, idNuevaSede);
        
        return nuevoIdPermiso > 0;
    }
    
    
    //listar el personal del estacinamiento actual 
    public List<Persona> obtenerPersonalDeSedeActual() {
    Estacionamiento sedeActual = SessionManager.getInstance().getEstacionamiento();
    if (sedeActual != null) {
        return permisosDAO.listarPersonalPorEstacionamiento(sedeActual.getIdEstacionamiento());
    }
    return new ArrayList<>();
   }
    
    // En PersonalService:
public ResumenNominaDTO calcularResumenNomina(List<Persona> empleados) {
    int contador = 0;
    double sumaSalarios = 0.0;

    for (Persona p : empleados) {
        contador++;
        // Validamos que el salario no sea nulo antes de sumar
        if (p.getSalario() != null) {
            sumaSalarios += p.getSalario();
        }
    }
    return new ResumenNominaDTO(contador, sumaSalarios);
    
}
    
    
}