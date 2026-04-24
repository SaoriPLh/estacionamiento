/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.dao;

import com.estacionamiento.modelo.Estacionamiento;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisosDAO {

    public List<Estacionamiento> obtenerEstacionamientosPorPersona(int idPersona) {
        List<Estacionamiento> lista = new ArrayList<>();
        
        // Usamos 'nombre' e 'id_direccion' como dice en la bd 
        String sql = "SELECT e.id_estacionamiento, e.nombre, e.id_direccion " +
                     "FROM estacionamiento e " +
                     "JOIN permisos p ON e.id_estacionamiento = p.id_estacionamiento " +
                     "WHERE p.id_persona = ? AND p.id_estado_permiso = 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPersona);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estacionamiento est = new Estacionamiento();
                    //  Los nombres de las columnas deben coincidir con el SELECT de arriba
                    est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                    est.setNombre(rs.getString("nombre")); 
                    est.setIdDireccion(rs.getInt("id_direccion"));
                    
                    lista.add(est);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lista;
    }
}