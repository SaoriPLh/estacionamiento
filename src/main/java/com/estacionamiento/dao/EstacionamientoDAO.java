package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionamientoDAO {

    // =====================================================
    // 🔹 MAPPER (solo lectura)
    // =====================================================
    private Estacionamiento map(ResultSet rs) throws SQLException {

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

        Estacionamiento e = new Estacionamiento();
        e.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
        e.setNombre(rs.getString("nombre"));
        e.setActivo(rs.getBoolean("activo"));
        e.setEmpresa(empresa);
        e.setDireccion(direccion);

        return e;
    }

    // =====================================================
    // 🔹 BUSCAR POR ID
    // =====================================================
    public Estacionamiento buscarPorId(int id) {

        String sql = """
            SELECT e.id_estacionamiento, e.nombre, e.activo,
                   e.id_empresa,
                   e.id_direccion,
                   d.calle,
                   c.id_ciudad, c.nombre AS ciudad,
                   es.id_estado, es.nombre AS estado,
                   emp.nombre_comercial
            FROM estacionamiento e
            JOIN empresa emp ON e.id_empresa = emp.id_empresa
            JOIN direccion d ON e.id_direccion = d.id_direccion
            JOIN ciudad c ON d.id_ciudad = c.id_ciudad
            JOIN estado es ON c.id_estado = es.id_estado
            WHERE e.id_estacionamiento = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscarPorId: " + e.getMessage());
        }

        return null;
    }

    // =====================================================
    // 🔹 LISTAR POR EMPRESA (más simple y correcto)
    // =====================================================
    public List<Estacionamiento> listarPorEmpresa(int idEmpresa) {

        List<Estacionamiento> lista = new ArrayList<>();

        String sql = """
            SELECT e.id_estacionamiento, e.nombre, e.activo,
                   e.id_empresa,
                   e.id_direccion,
                   d.calle,
                   c.id_ciudad, c.nombre AS ciudad,
                   es.id_estado, es.nombre AS estado,
                   emp.nombre_comercial
            FROM estacionamiento e
            JOIN empresa emp ON e.id_empresa = emp.id_empresa
            JOIN direccion d ON e.id_direccion = d.id_direccion
            JOIN ciudad c ON d.id_ciudad = c.id_ciudad
            JOIN estado es ON c.id_estado = es.id_estado
            WHERE e.id_empresa = ? AND e.activo = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error listarPorEmpresa: " + e.getMessage());
        }

        return lista;
    }

    // =====================================================
    // 🔹 INSERT (CORRECTO según tu tabla)
    // =====================================================
    public Estacionamiento insertar(Estacionamiento est) {

        String sql = """
            INSERT INTO estacionamiento
            (id_empresa, id_direccion, nombre, activo)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, est.getEmpresa().getIdEmpresa());
            ps.setInt(2, est.getDireccion().getIdDireccion());
            ps.setString(3, est.getNombre());
            ps.setBoolean(4, est.getActivo());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        est.setIdEstacionamiento(rs.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error insertar estacionamiento: " + e.getMessage());
        }

        return est;
    }

    // =====================================================
    // 🔹 DESACTIVAR
    // =====================================================
    public boolean desactivar(int id) {

        String sql = "UPDATE estacionamiento SET activo = 0 WHERE id_estacionamiento = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error desactivar: " + e.getMessage());
            return false;
        }
    }
}