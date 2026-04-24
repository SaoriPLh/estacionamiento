/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.dao;

import com.estacionamiento.modelo.Persona;
import com.estacionamiento.modelo.Rol;
import com.estacionamiento.util.DBConnection; 
import java.sql.*;

public class PersonaDAO {

    public Persona autenticar(String user, String pass) {

    Persona personaEncontrada = null;

    String sql = "SELECT p.*, r.id_rol, r.nombre_rol " +
                 "FROM persona p " +
                 "JOIN rol r ON p.id_rol = r.id_rol " +
                 "WHERE p.username = ? AND p.password = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, user);
        ps.setString(2, pass);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                // Crear el ROL primero hijo
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));

                // Crear la PERSONA (padre= con objeto Rol
                personaEncontrada = new Persona();
                personaEncontrada.setIdPersona(rs.getInt("id_persona"));
                personaEncontrada.setRol(rol);
                personaEncontrada.setNombre(rs.getString("nombre"));
                personaEncontrada.setApellidoPaterno(rs.getString("apellido_paterno"));
                personaEncontrada.setApellidoMaterno(rs.getString("apellido_materno"));
                personaEncontrada.setUsername(rs.getString("username"));
                personaEncontrada.setPassword(rs.getString("password"));
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return personaEncontrada;
}
}