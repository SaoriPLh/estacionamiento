package com.estacionamiento.servicio;

import com.estacionamiento.dao.*;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.EmailService;
import com.estacionamiento.util.MisConstantes;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroServicio {

    private RegistroDAO registroDao = new RegistroDAO();
    private EspacioService espacioService = new EspacioService();
    private VehiculoDAO vehiculoDao = new VehiculoDAO();
    private MarcaDAO marcaDao = new MarcaDAO();
    private CalculoServicio calculoService = new CalculoServicio();
    private TarifaService tarifaService = new TarifaService();
    private UnidadDescuentoDAO unidadDescuentoDAO = new UnidadDescuentoDAO();
    private PensionDAO pensionDAO = new PensionDAO();
    private EspacioDAO espacioDAO = new EspacioDAO();
    //tarifa service calculamos el monto y lo seteamos a registro
    public Registro registrarEntrada(Registro registro, long cantidad) {

        Vehiculo v = vehiculoDao.buscarPorPlaca(registro.getVehiculo().getPlaca());

        Marca marca = null;
        if (registro.getVehiculo().getMarca() != null && registro.getVehiculo().getMarca().getIdMarca() > 0) {
            marca = marcaDao.buscarPorID(registro.getVehiculo().getMarca().getIdMarca());
        }

        if (v == null) {
            v = new Vehiculo();
            v.setPlaca(registro.getVehiculo().getPlaca());
            v.setModelo(registro.getVehiculo().getModelo());
            v.setMarca(marca);
            if (registro.getVehiculo().getCliente() != null) {
                v.setCliente(registro.getVehiculo().getCliente());
            }
            v = vehiculoDao.insertarVehiculo(v);
        }

        if (v == null) return null;

        CodigoAcceso c = registro.getCodigoAcceso();
       
        //logica dentro de espacio service devuelve true si hay cliente en dado caso de que se vaya a renovar la pension  y hacer nuevo registro de esto detectamos q el vehiculo esta en mi bd y que tiene cliente entonces claramente puee ocuparlo ?? 
        int idSede = (registro.getEspacio().getEstacionamiento() != null)
                ? registro.getEspacio().getEstacionamiento().getIdEstacionamiento()
                : com.estacionamiento.util.SessionManager.getInstance().getEstacionamiento().getIdEstacionamiento();

        if (espacioService.ocuparCajon(registro.getEspacio().getIdEspacio(), v.getCliente(), c)) {
            LocalDateTime ahora = LocalDateTime.now();
            Registro nuevo = new Registro();
            nuevo.setIdEstacionamiento(idSede);

            nuevo.setVehiculo(v);
            nuevo.setEspacio(registro.getEspacio());
            nuevo.setPersona(registro.getPersona());
            nuevo.setFechaRegistro(ahora);
            if (registro.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_NORMAL) { //puede ser normal por hora  o convenio por hora ambos son por hora 
                 nuevo.setMonto(registro.getTarifa().getPrecio()*cantidad); 
              
              // 4. Calcular fecha fin sumando horas
              nuevo.setFecha_fin_plan(ahora.plusHours(cantidad));
          }// Dentro de registrarEntrada
else if (registro.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_CONVENIO) {
    Tarifa tarifaPrecioHora = tarifaService.obtenerTarifaNormalPorSede(idSede);
    if (tarifaPrecioHora == null) {
        espacioService.liberarCajon(registro.getEspacio().getIdEspacio());
        return null;
    }
    double precioHora = tarifaPrecioHora.getPrecio();

    // 2. Obtener la tarifa de convenio completa para tener el descuento y la unidad
    Tarifa tarifaConvenio = tarifaService.buscarPorid(registro.getTarifa().getIdTarifa());
    UnidadDescuento unidad = unidadDescuentoDAO.obtenerPorId(tarifaConvenio.getUnidadDescuento().getIdUnidadDescuento());

    // 3. Convertir la cantidad (días/horas) a horas totales usando el factor
    // Usamos double para no perder precisión en el cálculo
    double horasTotales = (double) (unidad.getFactorConversionMinutos() * cantidad) / 60.0;
    
    
    
    // 5. Aplicar el descuento
    // Suponiendo que 'cantidad_descuento' es el número de horas que NO se cobran:
    double horasACobrar = horasTotales - tarifaConvenio.getCantidad_descuento();
    if (horasACobrar < 0) horasACobrar = 0; // Por si el descuento es mayor al tiempo
    
    double montoFinal = horasACobrar * precioHora;
    
    nuevo.setMonto(montoFinal);
    // Seteamos la fecha fin basada en las horas del convenio
    nuevo.setFecha_fin_plan(ahora.plusMinutes((long)horasTotales * 60));
}
          else if (registro.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION) {
           

              if (c != null) {
            nuevo.setMonto(0.0);
            nuevo.setCodigoAcceso(c);
          }
            else{
                 double precioPension = registro.getTarifa().getPrecio();
            Double descPension = registro.getTarifa().getValorDescuento();
            if(descPension != null && descPension > 0 && descPension < 1) {
                precioPension = precioPension * (1.0 - descPension);
                
            }
            nuevo.setMonto(precioPension);

            }

          
          
           
            if (registro.getTarifa().getTipoCobro() != null
                    && registro.getTarifa().getTipoCobro().getIdTipoCobro() == MisConstantes.TIPO_COBRO_MENSUAL) {
                nuevo.setFecha_fin_plan(ahora.plusMonths(1));
            } else {
                nuevo.setFecha_fin_plan(ahora.plusDays(15));
            }
          }
          
          else {
            nuevo.setMonto(registro.getTarifa().getPrecio());
          }
         //   nuevo.setHoraEntrada(new Timestamp(System.currentTimeMillis()));
            nuevo.setHoraEntrada(ahora);  // solo hora de entrada no registramos hora de salida en pensiones o asi
           
            EstadoRegistro estado = new EstadoRegistro();
estado.setIdEstadoRegistro(MisConstantes.REGISTRO_ACTIVO); // o el que uses

nuevo.setEstadoRegistro(estado);
            nuevo.setTarifa(registro.getTarifa());
// tendria q actualizar el estado del cajon 
             
            
            
            

            Registro insertado = registroDao.insertar(nuevo);
            if (insertado == null) {
                espacioService.liberarCajon(registro.getEspacio().getIdEspacio());
                return null;
            }

            if (registro.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION) {
                // Actualizar espacio a PENSION siempre, independiente del resto
                espacioDAO.actualizarEstado(registro.getEspacio().getIdEspacio(), MisConstantes.ESPACIO_PENSION);

                // Cliente: prioridad al que vino del formulario; fallback al vínculo en BD
                Cliente clientePension = (registro.getVehiculo().getCliente() != null)
                        ? registro.getVehiculo().getCliente()
                        : v.getCliente();

                // Si el formulario trajo cliente pero el vehículo en BD no lo tenía, vincularlo
                if (clientePension != null && v.getCliente() == null) {
                    v.setCliente(clientePension);
                    vehiculoDao.actualizarClienteVehiculo(v.getIdVehiculo(), clientePension.getIdCliente());
                }

                if (clientePension == null) {
                    System.err.println("[RegistroServicio] Pensión sin cliente — no se insertó en tabla pension.");
                } else {
                    try {
                        Pension pension = new Pension();
                        pension.setCliente(clientePension);
                        pension.setVehiculo(insertado.getVehiculo());
                        pension.setTarifa(insertado.getTarifa());
                        pension.setEspacio(insertado.getEspacio());
                        EstadoPension estadoActiva = new EstadoPension(MisConstantes.PENSION_ACTIVA, "ACTIVO");
                        pension.setEstadoPension(estadoActiva);
                        pension.setCodigo(c);
                        pension.setIdEstacionamiento(idSede);
                        pension.setFechaInicio(new java.util.Date());
                        if (insertado.getFecha_fin_plan() != null) {
                            pension.setFechaFin(java.sql.Timestamp.valueOf(insertado.getFecha_fin_plan()));
                        }
                        try (Connection conP = DBConnection.getConnection()) {
                            pensionDAO.insertar(pension, conP);
                        }
                    } catch (Exception e) {
                        System.err.println("[RegistroServicio] No se pudo crear registro de pensión: " + e.getMessage());
                    }

                    // Email de confirmacion al cliente
                    try {
                        String correo = clientePension.getCorreo();
                        if (correo != null && !correo.isBlank()) {
                            String nombre = clientePension.getNombre() + " " + clientePension.getApellidoPaterno();
                            String placa  = insertado.getVehiculo().getPlaca();
                            String ini    = insertado.getHoraEntrada() != null ? insertado.getHoraEntrada().toLocalDate().toString() : "";
                            String fin    = insertado.getFecha_fin_plan() != null ? insertado.getFecha_fin_plan().toLocalDate().toString() : "—";
                            EmailService.notificarNuevaPension(correo, nombre, placa, ini, fin, insertado.getMonto());
                        }
                    } catch (Exception e) {
                        System.err.println("[RegistroServicio] No se pudo enviar email de confirmacion: " + e.getMessage());
                    }
                }
            }

            return insertado;
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

    public Registro buscarRegistroActivoPorEspacio(int idEspacio) {
        if (idEspacio <= 0) return null;
        return registroDao.buscarActivoPorEspacio(idEspacio);
    }
public List<Registro> obtenerRegistrosFiltrados(LocalDateTime inicio, LocalDateTime fin, Integer idTipoTarifa, int idEstacionamiento) {
    return registroDao.filtrarRegistros(inicio, fin, idTipoTarifa, idEstacionamiento);
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