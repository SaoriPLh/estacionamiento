package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Espacio.
 * Responsabilidad única: ejecutar SQL sobre la tabla espacio
 * y devolver objetos Espacio listos para usar.
 *
 * Consultas disponibles:
 *  1. listarPorEstacionamiento(idEstacionamiento)   → todos los espacios de una zona
 *  2. listarDisponibles(idEstacionamiento)          → solo los libres
 *  3. listarOcupados(idEstacionamiento)             → solo los ocupados
 *  4. buscarPorId(idEspacio)                        → un espacio por PK
 *  5. buscarPorCodigo(codigo)                       → un espacio por código (ej: "A-01")
 *  6. contarDisponibles(idEstacionamiento)          → int: total de espacios libres
 *  7. actualizarEstado(idEspacio, idEstadoEspacio)  → boolean: cambiar estado del espacio
 */
public class EspacioDAO {

    private static final String SQL_ESPACIO =
        "SELECT e.id_espacio, e.codigo, " +
        "       est.id_estacionamiento, est.nombre AS nombre_zona, " +
        "       te.id_tipo_espacio, te.nombre AS tipo_espacio, " +
        "       ee.id_estado_espacio, ee.nombre AS estado_espacio " +
        "FROM espacio e " +
        "JOIN estacionamiento est  ON e.id_estacionamiento = est.id_estacionamiento " +
        "JOIN tipo_espacio te      ON e.id_tipo_espacio    = te.id_tipo_espacio " +
        "JOIN estado_espacio ee    ON e.id_estado_espacio  = ee.id_estado_espacio ";

    //  espacios de una zona
    public List<Espacio> listarPorEstacionamiento(int idEstacionamiento) {
        String sql = SQL_ESPACIO +
            "WHERE e.id_estacionamiento = ? " +
            "ORDER BY e.codigo ASC";
        return ejecutarLista(sql, ps -> ps.setInt(1, idEstacionamiento));
    }

    //Solo espacios disponibles de una zona
    public List<Espacio> listarDisponibles(int idEstacionamiento) {
        String sql = SQL_ESPACIO +
            "WHERE e.id_estacionamiento = ? " +
            "  AND e.id_estado_espacio  = ? " +
            "ORDER BY e.codigo ASC";
        return ejecutarLista(sql, ps -> {
            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.ESPACIO_DISPONIBLE);
        });
    }

    //Solo espacios ocupados de una zona
    public List<Espacio> listarOcupados(int idEstacionamiento) {
        String sql = SQL_ESPACIO +
            "WHERE e.id_estacionamiento = ? " +
            "  AND e.id_estado_espacio  = ? " +
            "ORDER BY e.codigo ASC";
        return ejecutarLista(sql, ps -> {
            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.ESPACIO_OCUPADO);
        });
    }

    // Un espacio por su PK
    public Espacio buscarPorId(int idEspacio) {
        String sql = SQL_ESPACIO +
            "WHERE e.id_espacio = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearEspacio(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EspacioDAO] Error en buscarPorId: " + e.getMessage());
        }
        return null;
    }

    public Espacio buscarPorCodigo(String codigo) {
        String sql = SQL_ESPACIO +
            "WHERE UPPER(e.codigo) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearEspacio(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EspacioDAO] Error en buscarPorCodigo: " + e.getMessage());
        }
        return null;
    }

    // 
    public int contarDisponibles(int idEstacionamiento) {
        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM espacio " +
            "WHERE id_estacionamiento = ? " +
            "  AND id_estado_espacio  = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.ESPACIO_DISPONIBLE);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[EspacioDAO] Error en contarDisponibles: " + e.getMessage());
        }
        return 0;
    }

    // Cambiar el estado de un espacio (DISPONIBLE / OCUPADO / RESERVADO)
    public boolean actualizarEstado(int idEspacio, int idEstadoEspacio) {
        String sql =
            "UPDATE espacio " +
            "SET id_estado_espacio = ? " +
            "WHERE id_espacio = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstadoEspacio);
            ps.setInt(2, idEspacio);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EspacioDAO] Error en actualizarEstado: " + e.getMessage());
            return false;
        }
    }

   
    @FunctionalInterface
    private interface Parametrizador {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private List<Espacio> ejecutarLista(String sql, Parametrizador p) {
        List<Espacio> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            p.aplicar(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearEspacio(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EspacioDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }
    private Espacio mapearEspacio(ResultSet rs) throws SQLException {

        Estacionamiento zona = new Estacionamiento();
        zona.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
        zona.setNombre(rs.getString("nombre_zona"));

        TipoEspacio tipoEspacio = new TipoEspacio();
        tipoEspacio.setIdTipoEspacio(rs.getInt("id_tipo_espacio"));
        tipoEspacio.setDescripcion(rs.getString("tipo_espacio"));

        EstadoEspacio estadoEspacio = new EstadoEspacio();
        estadoEspacio.setIdEstadoEspacio(rs.getInt("id_estado_espacio"));
        estadoEspacio.setDescripcion(rs.getString("estado_espacio"));

        Espacio espacio = new Espacio();
        espacio.setIdEspacio(rs.getInt("id_espacio"));
        espacio.setCodigo(rs.getString("codigo"));
        espacio.setEstacionamiento(zona);
        espacio.setTipoEspacio(tipoEspacio);
        espacio.setEstadoEspacio(estadoEspacio);

        return espacio;
    }
}
