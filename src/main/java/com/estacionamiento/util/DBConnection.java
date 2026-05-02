/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.util;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
/**
 *
 * @author saori
 */
public class DBConnection {
    private static Connection connection;
    
    public static Connection getConnection(){
    try {
            Properties props = new Properties();
            InputStream input = DBConnection.class  //<- aca con class sabe donde esta ubicado en q paquete entonces junton con getresource busca el archivo en ese mismo paquete
                .getResourceAsStream("db.properties");
            
            /*
            o pudo haber sido 
            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("com/estacionamiento/util/db.properties");
         aca usamos getclassloader para q busque desde la raiz(resources) no desde el paquete actual aunque en el classpath a la hora de ejecutar todo este junto
            este esta obligado a buscar desde la raiz permitiendonos asi cosas como que si cambio esta clase de paquete aun asi busca desde la raiz la ruta q indiquemos
            cosa q no pasaria con la opcion de arriba ya q esta siempre buscaria en el paquete esperando q este ahi pero entonces getClassLoader() necesita la referencia de 
            .class para funcionar (la que sea), al parecer esto es porque en Java, el ClassLoader es el objeto que 'dio a luz' a las clases.

Como el ClassLoader es un objeto del sistema, no puedes acceder a él de forma 'estática' o global directamente; necesitas preguntarle a cualquier clase que ya esté cargada:
            'Oye, ¿quién es tu jefe (tu cargador)?'. Una vez que obtienes esa referencia, sales del contexto pequeño de tu paquete y obtienes permiso para ver todo el mapa 
            del proyecto desde la raíz (resources).
            */

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    
    }
  
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Conexión exitosa ");
            } else {
                System.out.println("Error en la conexión");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
}
}
