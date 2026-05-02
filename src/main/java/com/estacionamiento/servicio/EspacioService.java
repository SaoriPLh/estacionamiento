/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.MisConstantes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saori
 */
public class EspacioService {
    private EspacioDAO espacioDao;
    
    

    public Espacio crearCajon(Estacionamiento estacionamiento, int idTipo, int idEstado, String codigo) {
        
        TipoEspacio tipo = new TipoEspacio();
        tipo.setIdTipoEspacio(idTipo);
        tipo.setDescripcion(MisConstantes.getNombreTipoEspacio(idTipo));
        
        EstadoEspacio estado = new EstadoEspacio();
        estado.setIdEstadoEspacio(idEstado);
        tipo.setDescripcion(MisConstantes.getNombreEstadoEspacio(idTipo));

        Espacio esp = new Espacio();
        esp.setEstacionamiento(estacionamiento);
        esp.setTipoEspacio(tipo);
        esp.setEstadoEspacio(estado);
        esp.setCodigo(codigo);

        return espacioDao.insertarEspacio(esp);
    }
    
    
    public List<Espacio> listarDisponiblesPorEstacionamiento(int idEstacionamiento) {

    if(idEstacionamiento <= 0){
    return new ArrayList<>();
    }
    return espacioDao.listarEspaciosDisponibles(idEstacionamiento);
}
    
    
       public List<Espacio> listarEspaciosOcupadosPorEstacionamiento (int idEstacionamiento) {
    
        if(idEstacionamiento <= 0){
        return new ArrayList<>();
        }
        
        return espacioDao.listarEspaciosOcupados(idEstacionamiento);
    }
       
       
       

         

     public List<Espacio> listarEspacios (int idEstacionamiento) {
    
        if(idEstacionamiento <= 0){
        return new ArrayList<>();
        }
        
        return espacioDao.espaciosEstacionamiento(idEstacionamiento);
    }


    public boolean liberarCajon(int idEspacio) {
        return espacioDao.actualizarEstado(idEspacio, MisConstantes.ESPACIO_DISPONIBLE);
    }
    
   public boolean reservarCajon(int idEspacio) {
        
        Espacio esp = espacioDao.buscarPorId(idEspacio);
        
       
        if (esp != null && esp.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_DISPONIBLE) {
            return espacioDao.actualizarEstado(idEspacio, MisConstantes.ESPACIO_RESERVADO);
        }
        
      
        return false;
    }
    public boolean ocuparCajon(int idEspacio, Cliente cliente) {
    Espacio esp = espacioDao.buscarPorId(idEspacio);


    if (esp != null && (esp.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_DISPONIBLE || esp.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_PENSION)) {
        if(cliente != null){
            return espacioDao.actualizarEstado(idEspacio, MisConstantes.ESPACIO_PENSION);
        }else {
        return espacioDao.actualizarEstado(idEspacio, MisConstantes.ESPACIO_OCUPADO);
        }
    }
    
    return false;
}
    
    public boolean cambiarATipoReserva(int idEspacio) {
        return espacioDao.actualizarTipoEspacio(idEspacio, MisConstantes.TIPO_ESPACIO_RESERVA);
    }

    public boolean cambiarATipoNormal(int idEspacio) {
        return espacioDao.actualizarTipoEspacio(idEspacio, MisConstantes.TIPO_ESPACIO_NORMAL);
    }
}
