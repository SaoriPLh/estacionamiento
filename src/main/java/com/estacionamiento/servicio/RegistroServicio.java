package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.MisConstantes;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroServicio {

    private RegistroDAO registroDao = new RegistroDAO();
    private EspacioService espacioService = new EspacioService();
    private VehiculoDAO vehiculoDao = new VehiculoDAO(); // Para validar la placa al entrar
    private MarcaDAO marcaDao = new MarcaDAO();
    private CalculoServicio calculoService = new CalculoServicio();
    //tarifa service calculamos el monto y lo seteamos a registro
    public Registro registrarEntrada(String placa, int idMarca,int horas, Tarifa tarifa,Cliente cliente,Espacio espacio, String modelo, Persona empleado) {
    
        // si el vechiculo ya esta registrado entonces lo asignamos nada mas al espacio 
        Vehiculo v = vehiculoDao.buscarPorPlaca(placa); // los buscamos  por placa 
        Marca marca = marcaDao.buscarPorID(idMarca);
        if (v != null && v.getCliente() == null && cliente != null) { //aca tiene  q cunplir todas
        v.setCliente(cliente);
        vehiculoDao.actualizarVehiculo(v); 
      }
        
        //si v es dif de nulo pero va de pasada de nuevo simplemente pasamos el objeto obtenido sin crear uno nuevo
        if (v == null) {
            v = new Vehiculo();
            v.setPlaca(placa);
            v.setMarca(marca);
            if (modelo != null && !modelo.trim().isEmpty()) {
                v.setModelo(modelo);
            }
            
            //podemos hacer varios registros del mismo cliente pero tambien registrar a un cliente entonces a la hora de tomar e
           if (cliente != null) {
                v.setCliente(cliente);
            }
            v = vehiculoDao.insertarVehiculo(v);
        }
        
       
        
        if (espacioService.ocuparCajon(espacio.getIdEspacio(),cliente)) { // si esta desucpado el cajon
      // esto si es true se ejecuta ?       
           LocalDateTime ahora = LocalDateTime.now();
            Registro nuevo = new Registro();
            nuevo.setVehiculo(v);
            nuevo.setEspacio(espacio);
            nuevo.setPersona(empleado);
            if (tarifa.getTipoCobro().getIdTipoCobro() == MisConstantes.TIPO_COBRO_HORA) { //puede ser normal por hora  o convenio por hora ambos son por hora 
                 nuevo.setMonto(tarifa.getPrecio()*horas); 

              // 4. Calcular fecha fin sumando horas
              nuevo.setFecha_fin_plan(ahora.plusHours(horas));
          }
            else {
             nuevo.setMonto(tarifa.getPrecio()); //tanto si es mensualidad, quincenal, o especial tienen precio fijo
            }
         //   nuevo.setHoraEntrada(new Timestamp(System.currentTimeMillis()));
            nuevo.setHoraEntrada(ahora);
           
          
            nuevo.setTarifa(tarifa);
// tendria q actualizar el estado del cajon 
             
            
            EstadoRegistro estado = new EstadoRegistro();
            estado.setIdEstadoRegistro(MisConstantes.REGISTRO_ACTIVO);
            nuevo.setEstadoRegistro(estado);

            return registroDao.insertar(nuevo);
        }
        
        return null; 
    }

    public boolean registrarSalida(int idRegistro, Timestamp horaSalida) {//hora salida sacamos la actual en cuando ejecutamos este registrar salida
        if (idRegistro <= 0) return false;
/*
       aca pensaba en añadir al registro o settearle el monto final pero al final vamos a recuperar en la ui los registros cargados
       actualizados entonces este objeto no me serviria x el momento osea el objeto que quedo ya con la hora de salida el monto etc
       ya q despues lo consultare pero en masividad no en el momento me refiero a q hare una actualizacion de toodooos los registros entonces
       ahi aparecera este nuevo*/
        Registro r = registroDao.buscarPorId(idRegistro);
        if (r == null) return false;

     
        boolean registrado = registroDao.registrarSalida(idRegistro, horaSalida, calculoService.calcularMontoTotal(r));

        if (registrado) {
            
            return espacioService.liberarCajon(r.getEspacio().getIdEspacio());
        }
        return false;
    }

   
    public List<Registro> obtenerReporteDiario(String fecha) {
        if (fecha == null || fecha.isEmpty()) return new ArrayList<>();
        return registroDao.listarPorFecha(fecha);
    }

    
    public Registro buscarRegistro(int idRegistro) {
        if (idRegistro <= 0) return null;
        return registroDao.buscarPorId(idRegistro);
    }
public List<Registro> obtenerRegistrosFiltrados(LocalDateTime inicio, LocalDateTime fin, Integer idTipoTarifa) {
    return registroDao.filtrarRegistros(inicio, fin, idTipoTarifa);
}
    public ResumenGananciasDTO obtenerGanancias(List<Registro> registros) {
    ResumenGananciasDTO reporte = new ResumenGananciasDTO();
    
    for (Registro r : registros) {
        
        int tipo = r.getTarifa().getTipoTarifa().getIdTipoTarifa(); 
        
        switch (tipo) {
            case MisConstantes.TARIFA_PENSION:
                reporte.numeroPensiones++;
                break;
            case MisConstantes.TARIFA_NORMAL:
                reporte.numeroTarifaNormal++;
                break;
            case MisConstantes.TARIFA_ESPECIAL:
                reporte.numeroTarifaEspecial++;
                break;
            case MisConstantes.TARIFA_CONVENIO:
                reporte.numeroTarifaConvenios++;
                break;
        }

      
        if (r.getMonto() > 0) {
            reporte.gananciasTotales += r.getMonto();
        }
        
        reporte.totalRegistros++;
    }
    
    return reporte;
}
  
    
}