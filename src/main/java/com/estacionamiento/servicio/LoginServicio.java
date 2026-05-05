package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.PasswordHasher;
import com.estacionamiento.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class LoginServicio {
    
    private PersonaDAO personaDAO = new PersonaDAO();
    private PermisosDAO permisosDAO = new PermisosDAO();
    private EstacionamientoDAO estacionamientoDAO = new EstacionamientoDAO();

 public int procesarLogin(String user, String passPlana) {

    Persona p = personaDAO.buscarPorUsername(user); 
    if (p == null) return -1;

    boolean passwordCorrecta;

    if (p.isRequiereCambio()) {
        // Intentar bcrypt primero (empleado con cambio solicitado por admin)
        // Si falla, intentar comparación plana (cuenta creada manualmente en BD)
        try {
            passwordCorrecta = PasswordHasher.verificar(passPlana, p.getPassword());
        } catch (Exception e) {
            passwordCorrecta = false;
        }
        if (!passwordCorrecta) {
            passwordCorrecta = passPlana.equals(p.getPassword());
        }
        if (!passwordCorrecta) return -1;
        SessionManager.getInstance().setUsuario(p);
        return 2; // forzar cambio
    } else {
       
        try {
            passwordCorrecta = PasswordHasher.verificar(passPlana, p.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar contraseña", e);
        }

        if (!passwordCorrecta) return -1;
    }

   
    return inicializarSesion(p);

    // resto igual
  /*   p.setPermisos(permisosDAO.obtenerPermisosPorPersona(p.getIdPersona()));
    if (p.getPermisos().isEmpty()) return -2;

    SessionManager.getInstance().setUsuario(p);
    SessionManager.getInstance().setEmpresa(p.getEmpresa());

    List<Estacionamiento> sedes = obtenerSedesAutorizadas(p);

    if (sedes.size() == 1) {
        SessionManager.getInstance().setEstacionamiento(sedes.get(0));
        return 1;
    }

    return 0;*/
}


        //este metodo lo llamara la interfaz para cuando ya elija el admin o empleadoConPrivilegios que Estacionamiento y ahora si guardamos
        public void asignarEstacionamientoAdmin (Estacionamiento estacionamiento ){

        SessionManager.getInstance().setEstacionamiento(estacionamiento);
        }

    
    //este se volveria a ocupar dentro de la interfaz para poder mostrar las opciones de estacionamientos 
    //a las q puede acceder el admin porque peude haber empleados con acceso a dif zonas pero no las mismas q un admin
    //permitiendonos dibujar en la ui esta info de las zonas etc
        public List<Estacionamiento> obtenerSedesAutorizadas(Persona p) {
            if (p == null) return new ArrayList<>();

        
            if (p.getRol() != null && p.getRol().getIdRol() == MisConstantes.ROL_ADMIN) {
                return estacionamientoDAO.listarPorEmpresa(p.getEmpresa().getIdEmpresa());
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
    
 public boolean cambiarContraseña(int idPersona, String nuevaContraseña) {
   
    if (idPersona <= 0 || nuevaContraseña == null || nuevaContraseña.trim().isEmpty()) {
        return false;
    }

   
    Persona pActualizada = personaDAO.cambiarContrasena(idPersona, nuevaContraseña);
    
    if (pActualizada != null) {
   
        SessionManager.getInstance().setUsuario(pActualizada);
        
  
        if (pActualizada.getEmpresa() != null) {
            SessionManager.getInstance().setEmpresa(pActualizada.getEmpresa());
        }
        
        return true;
    }
    
    return false;
}
 
 public boolean solicitarCambioContraseña(int idPersona) {
     return personaDAO.solicitoCambioContraseña(idPersona);
 }

 public boolean verificarPasswordActual(Persona usuario, String passActual) {
     if (usuario == null || passActual == null) return false;
     Persona pDB = personaDAO.buscarPorUsername(usuario.getUsername());
     if (pDB == null) return false;
     if (pDB.isRequiereCambio()) {
         return passActual.equals(pDB.getPassword());
     }
     try {
         return PasswordHasher.verificar(passActual, pDB.getPassword());
     } catch (Exception e) {
         return false;
     }
 }
    

 private int inicializarSesion(Persona p) {

    p.setPermisos(permisosDAO.obtenerPermisosPorPersona(p.getIdPersona()));
    if (p.getPermisos().isEmpty()) return -2;

    
    SessionManager.getInstance().setUsuario(p);
    SessionManager.getInstance().setEmpresa(p.getEmpresa());

    
    List<Estacionamiento> sedes = obtenerSedesAutorizadas(p);

    if (sedes.size() == 1) {
        SessionManager.getInstance().setEstacionamiento(sedes.get(0));
        return 1;
    }

    return 0; 
}


}