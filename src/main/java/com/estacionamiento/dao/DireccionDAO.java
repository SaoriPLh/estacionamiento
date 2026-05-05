package com.estacionamiento.dao;

import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DireccionDAO {

    private static final Logger logger = Logger.getLogger(DireccionDAO.class.getName());

    public int insertar(String calle, int idCiudad, String numExterior, String cp) {
        String sql = "INSERT INTO direccion (calle, id_ciudad,numero_exterior, codigo_postal) VALUES (?, ?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, calle);
            ps.setInt(2, idCiudad);
            ps.setString(3, numExterior);
            ps.setString(4,cp);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error insertando dirección", e);
        }
        return 0;
    }
}
