package com.estacionamiento.dao;

import com.estacionamiento.modelo.EstadoPension;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoPensionDAO {

    public List<EstadoPension> listarTodos() {
        List<EstadoPension> lista = new ArrayList<>();
        String sql = "SELECT id_estado_pension, nombre_estado FROM estado_pension ORDER BY id_estado_pension";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error listando estados de pensión", e);
        }
        return lista;
    }

    public EstadoPension buscarPorId(int idEstadoPension) {
        String sql = "SELECT id_estado_pension, nombre_estado FROM estado_pension WHERE id_estado_pension = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstadoPension);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando estado de pensión id=" + idEstadoPension, e);
        }
        return null;
    }

    private EstadoPension mapear(ResultSet rs) throws SQLException {
        return new EstadoPension(
                rs.getInt("id_estado_pension"),
                rs.getString("nombre_estado"));
    }
}
