package com.estacionamiento.dao;

import com.estacionamiento.modelo.UnidadDescuento;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadDescuentoDAO {

    // Consultas SQL - Asegúrate que coincidan exactamente con tu script de BD
    private static final String SQL_SELECT = "SELECT id_unidad_descuento, tipo_unidad, factor_conversion_minutos FROM unidad_descuento";
    private static final String SQL_SELECT_BY_ID = "SELECT id_unidad_descuento, tipo_unidad, factor_conversion_minutos FROM unidad_descuento WHERE id_unidad_descuento = ?";
    private static final String SQL_INSERT = "INSERT INTO unidad_descuento(tipo_unidad, factor_conversion_minutos) VALUES(?, ?)";
    private static final String SQL_UPDATE = "UPDATE unidad_descuento SET tipo_unidad=?, factor_conversion_minutos=? WHERE id_unidad_descuento=?";
    private static final String SQL_DELETE = "DELETE FROM unidad_descuento WHERE id_unidad_descuento=?";

    public List<UnidadDescuento> listar() {
        List<UnidadDescuento> unidades = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Usamos el constructor de tu modelo UnidadDescuento
                unidades.add(mapearUnidad(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return unidades;
    }

    public UnidadDescuento obtenerPorId(int id) {
        UnidadDescuento unidad = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    unidad = mapearUnidad(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return unidad;
    }

    public int insertar(UnidadDescuento unidad) {
        // Añadimos RETURN_GENERATED_KEYS para recuperar el ID si lo necesitas
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, unidad.getTipoUnidad()); //[cite: 1]
            stmt.setInt(2, unidad.getFactorConversionMinutos()); //[cite: 1]
            int filas = stmt.executeUpdate();
            
            // Opcional: Recuperar el ID autoincrementable generado por MySQL
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    unidad.setIdUnidadDescuento(rs.getInt(1));
                }
            }
            return filas;
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            return 0;
        }
    }

    public int actualizar(UnidadDescuento unidad) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, unidad.getTipoUnidad()); //[cite: 1]
            stmt.setInt(2, unidad.getFactorConversionMinutos()); //[cite: 1]
            stmt.setInt(3, unidad.getIdUnidadDescuento()); //[cite: 1]
            return stmt.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            return 0;
        }
    }

    public int eliminar(int idUnidadDescuento) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setInt(1, idUnidadDescuento);
            return stmt.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            return 0;
        }
    }

    // Método auxiliar para evitar repetir código de mapeo
    private UnidadDescuento mapearUnidad(ResultSet rs) throws SQLException {
        return new UnidadDescuento(
            rs.getInt("id_unidad_descuento"),
            rs.getString("tipo_unidad"), //[cite: 1]
            rs.getInt("factor_conversion_minutos") //[cite: 1]
        );
    }
}