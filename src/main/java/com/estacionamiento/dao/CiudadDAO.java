package com.estacionamiento.dao;

import com.estacionamiento.modelo.Ciudad;
import com.estacionamiento.modelo.Estado;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CiudadDAO {

    public List<Ciudad> listarTodas() {
        List<Ciudad> lista = new ArrayList<>();
        String sql = "SELECT c.id_ciudad, c.nombre, e.id_estado, e.nombre AS nombre_estado " +
                     "FROM ciudad c JOIN estado e ON c.id_estado = e.id_estado " +
                     "ORDER BY e.nombre, c.nombre";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Estado estado = new Estado();
                estado.setIdEstado(rs.getInt("id_estado"));
                estado.setNombre(rs.getString("nombre_estado"));

                Ciudad c = new Ciudad();
                c.setIdCiudad(rs.getInt("id_ciudad"));
                c.setNombre(rs.getString("nombre"));
                c.setEstado(estado);
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
