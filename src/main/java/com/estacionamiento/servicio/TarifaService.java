package com.estacionamiento.servicio;

import com.estacionamiento.modelo.*;
import com.estacionamiento.dao.*;
import java.util.List;

public class TarifaService {

    private TarifaDAO tarifaDao = new TarifaDAO();
    private EstacionamientoService estService = new EstacionamientoService();

    public boolean añadirTarifa(int idEstacionamiento, Tarifa tarifa) { //aca puede ser obj tarifa con desc o no
        if (idEstacionamiento <= 0 || tarifa == null) return false;

      
        Estacionamiento e = estService.buscarEstacionamientoPorId(idEstacionamiento);
        if (e == null) return false;

        tarifa.setEstacionamiento(e);
        Tarifa guardada = tarifaDao.insertarTarifa(tarifa);

        if (guardada.getIdTarifa() > 0) {
            e.agregarTarifa(guardada); 
            return true;
        }
        return false;
    }
    public Tarifa obtenerTarifaNormalPorSede(int idSede) {
        Tarifa t = tarifaDao.obtenerTarifaNormalPorSede(idSede);
        if (t == null) {
            // Log de error o lanzar una excepción personalizada
            System.out.println("Error: No se encontró tarifa normal para la sede " + idSede);
            // Podrías retornar una tarifa por defecto para que el sistema no truene
        }
        return t;
    }
    public boolean actualizarTarifa(Tarifa tarifa) {
    if (tarifa == null || tarifa.getIdTarifa() <= 0) return false;

    // Llamamos al DAO para que haga el UPDATE en la BD
    return tarifaDao.actualizar(tarifa); 
}

    public List<Tarifa> obtenerTarifasPorSede(int idEstacionamiento) {
        return tarifaDao.listarPorEstacionamiento(idEstacionamiento);
    }

    public boolean eliminarTarifa(Estacionamiento est, int idTarifa) {
        if (est == null || idTarifa <= 0) return false;

        if (tarifaDao.eliminar(idTarifa)) {
           
            est.getListaTarifas().removeIf(t -> t.getIdTarifa() == idTarifa);
            return true;
        }
        return false;
    }
    
    public Tarifa buscarPorid (int idTarifa){
    
        if(idTarifa <= 0){return null;}
        
        return tarifaDao.buscarPorId(idTarifa);
       
        
    }
}