/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisosDAO {

    public List<Estacionamiento> obtenerEstacionamientosPorPersona(int idPersona) {

        List<Estacionamiento> lista = new ArrayList<>();

        String sql = "SELECT e.id_estacionamiento, e.nombre, " +
                     "emp.id_empresa, emp.nombre_comercial, " +
                     "d.id_direccion, d.calle, " +
                     "c.id_ciudad, c.nombre AS ciudad, " +
                     "es.id_estado, es.nombre AS estado " +
                     "FROM estacionamiento e " +
                     "JOIN permisos p ON e.id_estacionamiento = p.id_estacionamiento " +
                     "JOIN empresa emp ON e.id_empresa = emp.id_empresa " +
                     "JOIN direccion d ON e.id_direccion = d.id_direccion " +
                     "JOIN ciudad c ON d.id_ciudad = c.id_ciudad " +
                     "JOIN estado es ON c.id_estado = es.id_estado " +
                     "WHERE p.id_persona = ? AND p.id_estado_permiso = 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                   
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("id_estado"));
                    estado.setNombre(rs.getString("estado"));

                    
                    Ciudad ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("id_ciudad"));
                    ciudad.setNombre(rs.getString("ciudad"));
                    ciudad.setEstado(estado);

                    
                    Direccion direccion = new Direccion();
                    direccion.setIdDireccion(rs.getInt("id_direccion"));
                    direccion.setCalle(rs.getString("calle"));
                    direccion.setCiudad(ciudad);

                   
                    Empresa empresa = new Empresa();
                    empresa.setIdEmpresa(rs.getInt("id_empresa"));
                    empresa.setNombreComercial(rs.getString("nombre_comercial"));

                    Estacionamiento est = new Estacionamiento();
                    est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                    est.setNombre(rs.getString("nombre"));
                    est.setEmpresa(empresa);
                    est.setDireccion(direccion);

                    lista.add(est);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}