/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.util;

/**
 * Diccionario oficial del sistema de estacionamiento.
 * Mapea los IDs de los catálogos de la base de datos para evitar "números mágicos".
 * * @author saori
 */
public class MisConstantes {

    // 1. ROL
    public static final int ROL_ADMIN = 1;
    public static final int ROL_EMPLEADO = 2;

    // 2. ESTADO_ESPACIO 
    public static final int ESPACIO_DISPONIBLE = 1;
    public static final int ESPACIO_OCUPADO = 2;
    public static final int ESPACIO_RESERVADO = 3;

    // 3. TIPO_ESPACIO
    public static final int TIPO_ESPACIO_NORMAL = 1;
    public static final int TIPO_ESPACIO_RESERVA = 2;

    // 4. ESTADO_REGISTRO 
    public static final int REGISTRO_RESERVADO = 1;
    public static final int REGISTRO_ACTIVO = 2;
    public static final int REGISTRO_FINALIZADO = 3;
    public static final int REGISTRO_CANCELADO = 4;

    // 5. TIPO_TARIFA 
    public static final int TARIFA_NORMAL = 1;
    public static final int TARIFA_PENSION = 2;
    public static final int TARIFA_EVENTO = 3;
    public static final int TARIFA_ESPECIAL = 4;
    public static final int TARIFA_CONVENIO = 5;

    // 6. UNIDAD_DESCUENTO 
    public static final int UNIDAD_MINUTO = 1; 
    public static final int UNIDAD_HORA = 2;   
    public static final int UNIDAD_DIA = 3;    

    // 7. ESTADO_CODIGO_ACCESO 
    public static final int CODIGO_ACTIVO = 1;
    public static final int CODIGO_USADO = 2;
    public static final int CODIGO_VENCIDO = 3;
    public static final int CODIGO_CANCELADO = 4;

    // 9. ESTADO_PERMISO
    public static final int PERMISO_ACTIVO = 1;
    public static final int PERMISO_SUSPENDIDO = 2;
    public static final int PERMISO_REVOCADO = 3;

    // EXTRAS: Tipos de cobro   
    public static final String COBRO_POR_HORA = "por_hora";
    public static final String COBRO_FIJO = "fijo";
    public static final String COBRO_QUINCENAL = "quincenal";
    public static final String COBRO_MENSUAL = "mensual";
}