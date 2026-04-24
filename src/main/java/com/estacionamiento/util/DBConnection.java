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
            InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("com/estacionamiento/util/db.properties");

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
