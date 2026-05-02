/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.servicio;
import com.estacionamiento.modelo.*;
import com.estacionamiento.dao.*;
import java.util.List;
/**
 *
 * @author saori
 */
public class EstacionamientoService {
    
  private EspacioDAO espacioDao = new EspacioDAO();
  private EstacionamientoDAO estacionamientoDao = new EstacionamientoDAO();
  
    //insertar un espacio en un estacionamiento cuando listo los estacionamiento activos
  //tengo esa opcion cuando veo los q no estan activos no tengo la opcion esa
  public boolean agregarNuevoCajon(Estacionamiento est, Espacio nuevoEspacio) {
 
    nuevoEspacio.setEstacionamiento(est);
    
  
    Espacio espacioGuardado = espacioDao.insertarEspacio(nuevoEspacio);
    
    if (espacioGuardado.getIdEspacio() > 0) {
       
        est.agregarEspacio(espacioGuardado);
        
        return true;
    }
    
    return false;
}
  
  /*aca en buscarEstacionamientoPorid me va a traer toda su info entonces tendremos que
  juntar los DAO de espacioDAO para traer todos los espacios de un estacionamiento y luego
  con estacionamientoDAO traemos el estacionamiento y le añadimos a su lista de espacios
  
  */
  public Estacionamiento buscarEstacionamientoPorId(int idEstacionamiento) {
   
        Estacionamiento est = estacionamientoDao.buscarPorId(idEstacionamiento);

        if (est != null) {
            
            List<Espacio> listaCajones = espacioDao.espaciosEstacionamiento(idEstacionamiento);

            for (Espacio e : listaCajones) {
                est.agregarEspacio(e);
            }
        }

        return est; 
   }
  
  public boolean eliminarEspacio (Estacionamiento estacionamiento, int idEspacio){
  
      boolean eliminado = espacioDao.eliminar(idEspacio);
      
      if(eliminado){
          estacionamiento.getEspacios().removeIf(espacio -> espacio.getIdEspacio() == idEspacio);
          return true;
      }
      
      return false;
  } 
 /* un estacionamiento pertenece a una empresa
  una empresa puede tener muchos estacionamientos 
  del lado de la empresa -> Lista de sus estacionamientos
  Pero aca debe estar el metodo para añadir un estacionamiento a una empresa ? 
  o en la empresa debe estar añadir estcionamiento, creo que es aca porque tiene su id de empresa aca
  es uno a muchos no tendria caso en empresa 
  
  */
  
  /*Asumimos que el objeto empresa es en donde estoy logeao claramente puedo añadir estacionamiento
  a la empresa donde estoy logeada
  */
public boolean insertarEstacionamiento(Empresa empresa, Estacionamiento estacionamiento) {
    if (empresa == null || estacionamiento == null) {
        return false;
    }
    Estacionamiento guardado = estacionamientoDao.insertarEstacionamiento(estacionamiento);
    
   
    if (guardado != null && guardado.getIdEstacionamiento() > 0) {
        
        
        empresa.agregarEstacionamiento(guardado);
        
        return true;
    }
    
    return false;
}


}
