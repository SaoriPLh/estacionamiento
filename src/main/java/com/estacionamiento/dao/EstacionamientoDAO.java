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

public class EstacionamientoDAO {

    /**
     * 
     * @return 
     */
    public List<Estacionamiento> listarTodo() {
        List<Estacionamiento> lista = new ArrayList<>();
        String sql = "SELECT id_estacionamiento, nombre, id_direccion FROM estacionamiento";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estacionamiento est = new Estacionamiento();
                est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                est.setNombre(rs.getString("nombre"));
                est.setIdDireccion(rs.getInt("id_direccion"));
                lista.add(est);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     para cuando necesite los detalles de una sola zona.
     * @param id
     * @return 
     */
    public Estacionamiento buscarPorId(int id) {
        String sql = "SELECT * FROM estacionamiento WHERE id_estacionamiento = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Estacionamiento(
                        rs.getInt("id_estacionamiento"),
                        rs.getInt("id_direccion"),
                        rs.getString("nombre")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}