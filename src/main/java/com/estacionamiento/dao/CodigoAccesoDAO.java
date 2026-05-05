package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodigoAccesoDAO {

    // ================= INSERT =================
    public CodigoAcceso insertar(CodigoAcceso codigo) throws SQLException {
        String sql = "INSERT INTO codigo_acceso " +
                "(id_cliente, id_estacionamiento, id_estado_codigo, codigo, fecha_inicio, fecha_fin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, codigo.getCliente().getIdCliente());
            ps.setInt(2, codigo.getEstacionamiento().getIdEstacionamiento());
            ps.setInt(3, codigo.getEstadoCodigo().getIdEstadoCodigo());
            ps.setString(4, codigo.getCodigo());
            ps.setTimestamp(5, new Timestamp(codigo.getFechaInicio().getTime()));

            if (codigo.getFechaFin() != null) {
                ps.setTimestamp(6, new Timestamp(codigo.getFechaFin().getTime()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        codigo.setIdCodigo(rs.getInt(1));
                    }
                }
            }
        }

        return codigo;
    }

    // ================= SELECT BASE =================
    private final String SELECT_BASE =
            "SELECT ca.*, e.nombre_estado, " +
            "c.id_cliente AS c_id, c.nombre AS c_nombre, c.apellido_paterno AS c_ap, " +
            "c.apellido_materno AS c_am, c.correo AS c_correo, c.telefono AS c_tel " +
            "FROM codigo_acceso ca " +
            "INNER JOIN estado_codigo_acceso e ON ca.id_estado_codigo = e.id_estado_codigo " +
            "INNER JOIN cliente c ON ca.id_cliente = c.id_cliente ";

    // ================= BUSCAR POR ID =================
    public CodigoAcceso buscarPorId(int idCodigo) throws SQLException {
        String sql = SELECT_BASE + "WHERE ca.id_codigo = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCodigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCodigo(rs);
            }
        }
        return null;
    }
    

    // ================= BUSCAR POR CODIGO =================
    public CodigoAcceso buscarPorCodigo(String codigoStr) throws SQLException {
        String sql = SELECT_BASE + "WHERE ca.codigo = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoStr);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCodigo(rs);
            }
        }
        return null;
    }

    // ================= BUSCAR ACTIVO =================
    public CodigoAcceso buscarActivoPorIdCliente(int idCliente) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE ca.id_cliente = ? AND ca.id_estado_codigo = ? " +
                "ORDER BY ca.fecha_inicio DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, MisConstantes.CODIGO_ACTIVO);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCodigo(rs);
            }
        }
        return null;
    }

    // ================= LISTAR POR CLIENTE =================
    public List<CodigoAcceso> listarPorCliente(int idCliente) throws SQLException {
        List<CodigoAcceso> lista = new ArrayList<>();

        String sql = SELECT_BASE +
                "WHERE ca.id_cliente = ? ORDER BY ca.fecha_inicio DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCodigo(rs));
            }
        }
        return lista;
    }

    // ================= LISTAR POR ESTACIONAMIENTO =================
    public List<CodigoAcceso> listarPorEstacionamiento(int idEstacionamiento) throws SQLException {
        List<CodigoAcceso> lista = new ArrayList<>();

        String sql = SELECT_BASE +
                "WHERE ca.id_estacionamiento = ? ORDER BY ca.fecha_inicio DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCodigo(rs));
            }
        }
        return lista;
    }

    // ================= UPDATE =================
    public boolean actualizarEstado(int idCodigo, int nuevoEstado) throws SQLException {
        String sql = "UPDATE codigo_acceso SET id_estado_codigo = ? WHERE id_codigo = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idCodigo);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarFechaFin(int idCodigo, java.util.Date nuevaFechaFin) throws SQLException {
        String sql = "UPDATE codigo_acceso SET fecha_fin = ? WHERE id_codigo = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(nuevaFechaFin.getTime()));
            ps.setInt(2, idCodigo);
            return ps.executeUpdate() > 0;
        }
    }

    // ================= DELETE =================
    public boolean eliminar(int idCodigo) throws SQLException {
        String sql = "DELETE FROM codigo_acceso WHERE id_codigo = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCodigo);
            return ps.executeUpdate() > 0;
        }
    }

    // ================= MAPEO =================
    private CodigoAcceso mapearCodigo(ResultSet rs) throws SQLException {
        CodigoAcceso ca = new CodigoAcceso();

        ca.setIdCodigo(rs.getInt("id_codigo"));
        ca.setCodigo(rs.getString("codigo"));

        Timestamp ini = rs.getTimestamp("fecha_inicio");
        if (ini != null) ca.setFechaInicio(new java.util.Date(ini.getTime()));

        Timestamp fin = rs.getTimestamp("fecha_fin");
        if (fin != null) ca.setFechaFin(new java.util.Date(fin.getTime()));

        EstadoCodigoAcceso estado = new EstadoCodigoAcceso(
                rs.getInt("id_estado_codigo"),
                rs.getString("nombre_estado"));
        ca.setEstadoCodigo(estado);

        ca.setCliente(mapearCliente(rs));

        Estacionamiento est = new Estacionamiento();
        est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
        ca.setEstacionamiento(est);

        return ca;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("c_id"));
        c.setNombre(rs.getString("c_nombre"));
        c.setApellidoPaterno(rs.getString("c_ap"));
        c.setApellidoMaterno(rs.getString("c_am"));
        c.setCorreo(rs.getString("c_correo"));
        c.setTelefono(rs.getString("c_tel"));
        return c;
    }
}