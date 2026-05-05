/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.util;


public class MisConstantes {

    public static final int ROL_ADMIN = 1;
    public static final int ROL_EMPLEADO = 2;


    public static final int ESPACIO_DISPONIBLE = 1;
    public static final int ESPACIO_OCUPADO = 2;
    public static final int ESPACIO_RESERVADO = 3;
    public static final int ESPACIO_PENSION = 4;

  
    public static final int TIPO_ESPACIO_NORMAL = 1;
    public static final int TIPO_ESPACIO_RESERVA = 2;

    public static final int REGISTRO_RESERVADO = 1;
    public static final int REGISTRO_ACTIVO = 2;
    public static final int REGISTRO_FINALIZADO = 3;
    public static final int REGISTRO_CANCELADO = 4;

   
    public static final int TARIFA_NORMAL = 1;
    public static final int TARIFA_PENSION = 2;

    public static final int TARIFA_ESPECIAL = 4;
    public static final int TARIFA_CONVENIO = 5;


    public static final int UNIDAD_MINUTO = 1; 
    public static final int UNIDAD_HORA = 2;   
    public static final int UNIDAD_DIA = 3;    
    
    public static final int TIPO_COBRO_HORA = 1;    
    public static final int TIPO_COBRO_MENSUAL = 2;    
   
    
    
    

    
    public static final int CODIGO_ACTIVO = 1;
    public static final int CODIGO_USADO = 2;
    public static final int CODIGO_VENCIDO = 3;
    public static final int CODIGO_CANCELADO = 4;

    
    public static final int PERMISO_ACTIVO = 1;
    public static final int PERMISO_SUSPENDIDO = 2;
    public static final int PERMISO_REVOCADO = 3;

    public static final int PENSION_ACTIVA    = 1;
    public static final int PENSION_VENCIDA   = 2;
    public static final int PENSION_SUSPENDIDA = 3;

  
    public static String getNombreEstadoEspacio(int id) {
        switch (id) {
            case ESPACIO_DISPONIBLE: return "Disponible";
            case ESPACIO_OCUPADO:    return "Ocupado";
            case ESPACIO_RESERVADO:  return "Reservado";
            case ESPACIO_PENSION:    return "Pensión";
            default: return "Desconocido";
        }
    }

    public static String getNombreTipoEspacio(int id) {
        switch (id) {
            case TIPO_ESPACIO_NORMAL:  return "Normal";
            case TIPO_ESPACIO_RESERVA: return "Reserva";
            default: return "General";
        }
    }

    public static String getNombreRol(int id) {
        switch (id) {
            case ROL_ADMIN:    return "Administrador";
            case ROL_EMPLEADO: return "Empleado";
            default: return "Sin Rol";
        }
    }

    public static String getNombreEstadoPension(int id) {
        switch (id) {
            case PENSION_ACTIVA:     return "Activa";
            case PENSION_VENCIDA:    return "Vencida";
            case PENSION_SUSPENDIDA: return "Suspendida";
            default: return "Desconocido";
        }
    }
}