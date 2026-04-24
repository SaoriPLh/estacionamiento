/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.dao;

import com.estacionamiento.modelo.Persona;
import com.estacionamiento.util.DBConnection; 
import java.sql.*;

public class PersonaDAO {

    public Persona autenticar(String user, String pass) {
        Persona personaEncontrada = null;
        String sql = "SELECT * FROM persona WHERE username = ? AND password = ?";
        
        // Usamos  metodo DBConnection.getConnection()
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user);
            ps.setString(2, pass);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    personaEncontrada = new Persona(
                        rs.getInt("id_persona"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre"),
                        rs.getString("apellido_paterno"),
                        rs.getString("apellido_materno"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
         
            e.printStackTrace(); 
        }
        
        return personaEncontrada;
    }
}