package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.PasswordHasher;
import com.estacionamiento.util.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 
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

   public boolean añadirAdministradorAnuevaSucursal(Persona p, Estacionamiento e) {

    if (p == null || e == null) return false;

    if (p.getIdPersona() <= 0 || e.getIdEstacionamiento() <= 0) {
        return false;
    }

    boolean yaExiste = permisosDAO.existePermiso(
            p.getIdPersona(),
            e.getIdEstacionamiento()
    );

    if (yaExiste) {
        return true;
    }

    int idPermisoGenerado = permisosDAO.asignarPermisos(
            p.getIdPersona(),
            e.getIdEstacionamiento()
    );

    if (idPermisoGenerado <= 0) return false;

    EstadoPermiso estadoPermiso = new EstadoPermiso(
            MisConstantes.PERMISO_ACTIVO, "activo"
    );

    Permiso nuevoP = new Permiso(
            idPermisoGenerado,
            e,
            p,
            estadoPermiso,
            new Date()
    );

    p.agregarPermiso(nuevoP);

    return true;
}

    
    public boolean añadirEmpleadoYPermisos(Persona p, Estacionamiento estacionamiento) {
        return añadirEmpleadoConPermisos(p, java.util.List.of(estacionamiento));
    }

    public boolean añadirEmpleadoConPermisos(Persona p, List<Estacionamiento> sedes) {
        if (sedes == null || sedes.isEmpty()) return false;
        String passHasheada = PasswordHasher.hash(p.getPassword());
        p.setPassword(passHasheada);
        Persona personaRegistrada = personaDAO.insertar(p);

        if (personaRegistrada == null || personaRegistrada.getIdPersona() <= 0) return false;

        for (Estacionamiento sede : sedes) {
            int idPerm = permisosDAO.asignarPermisos(personaRegistrada.getIdPersona(), sede.getIdEstacionamiento());
            EstadoPermiso ep = new EstadoPermiso(MisConstantes.PERMISO_ACTIVO, "activo");
            personaRegistrada.agregarPermiso(new Permiso(idPerm, sede, personaRegistrada, ep, new Date()));
        }
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

 public List<Estacionamiento> obtenerSedesAutorizadas(Persona p) {
        if (p == null) return new ArrayList<>();

       
        if (p.getRol() != null && p.getRol().getIdRol() == MisConstantes.ROL_ADMIN) {
            return estDAO.listarPorEmpresa(p.getEmpresa().getIdEmpresa());
        } 

        
        List<Estacionamiento> sedes = new ArrayList<>();
        if (p.getPermisos() != null) {
            for (Permiso perm : p.getPermisos()) {
             
                if (perm.getEstadoPermiso().getIdEstadoPermiso() == MisConstantes.PERMISO_ACTIVO) {
                    sedes.add(perm.getEstacionamiento());
                }
            }
        }
        return sedes;
    }

    public boolean actualizarEmpleado(int idPersona, String nombre, String apellidoP,
                                      String apellidoM, Double salario,
                                      List<Estacionamiento> nuevasSedes) {
        boolean ok = personaDAO.actualizarDatos(idPersona, nombre, apellidoP, apellidoM, salario);
        if (ok && nuevasSedes != null && !nuevasSedes.isEmpty()) {
            permisosDAO.revocarTodosLosPermisos(idPersona);
            for (Estacionamiento sede : nuevasSedes) {
                permisosDAO.asignarPermisos(idPersona, sede.getIdEstacionamiento());
            }
        }
        return ok;
    }

    public boolean solicitarCambioContraseña(int idPersona) {
        return personaDAO.solicitoCambioContraseña(idPersona);
    }

public int cambiarContraseña(int idPersona, String contrasenaNueva) {

    Persona p = personaDAO.cambiarContrasena(idPersona, contrasenaNueva);

    if (p == null) return -1;

    // 🔹 Cargar permisos
    p.setPermisos(permisosDAO.obtenerPermisosPorPersona(p.getIdPersona()));
    if (p.getPermisos().isEmpty()) return -2;

    // 🔹 Setear sesión
    SessionManager.getInstance().setUsuario(p);
    SessionManager.getInstance().setEmpresa(p.getEmpresa());

    // 🔹 Obtener sedes (igual que en login)
    List<Estacionamiento> sedes = obtenerSedesAutorizadas(p);

    if (sedes.size() == 1) {
        SessionManager.getInstance().setEstacionamiento(sedes.get(0));
        return 1;
    }

    return 0; // admin
}
    
    
}