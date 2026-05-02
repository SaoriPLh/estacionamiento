/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.util;

/**
 *
 * @author saori
 */
import java.io.*;
import java.util.Properties;

public class ConfiguracionLocal {
    private static final String ARCHIVO_CONFIG = "config_impresoras.properties";

    public static void guardarImpresoras(String ticketera, String oficina) {
        Properties props = new Properties();
        props.setProperty("ticketera", ticketera);
        props.setProperty("reportes", oficina);

        try (OutputStream out = new FileOutputStream(ARCHIVO_CONFIG)) {
            props.store(out, "Configuracion de Impresoras CyberMining");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getImpresora(String clave) { 
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(in);
            return props.getProperty(clave);
        } catch (IOException e) {
            return null; // Si no existe, devuelve null para pedir configuracion
        }
    }
    
public static void actualizarSoloUna(String llave, String nuevoNombre) {
    Properties props = new Properties();
    File archivo = new File("config_impresoras.properties");

  
    if (archivo.exists()) {
        try (InputStream in = new FileInputStream(archivo)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Error al leer configuración existente: " + e.getMessage());
        }
    }

    
    props.setProperty(llave, nuevoNombre);

    
    try (OutputStream out = new FileOutputStream(archivo)) {
        props.store(out, "Actualización automática de hardware");
        System.out.println("Configuración actualizada: " + llave + " = " + nuevoNombre);
    } catch (IOException e) {
        System.err.println("Error al persistir la nueva impresora: " + e.getMessage());
    }
}
}