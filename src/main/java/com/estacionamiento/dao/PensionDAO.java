package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PensionDAO {

    // ── SELECT BASE CORREGIDO ────────────────────────────────────────────────
    private static final String SELECT_BASE =
            "SELECT p.id_pension, p.id_cliente, p.id_vehiculo, p.id_tarifa, " +
            "       p.id_espacio, p.id_estado_pension, p.id_codigo, " +
            "       p.fecha_inicio, p.fecha_fin, " +
            "       ep.nombre_estado AS estado_nombre, " +
            "       cl.nombre AS cl_nombre, cl.apellido_paterno AS cl_apellido_p, " +
            "       cl.apellido_materno AS cl_apellido_m, cl.correo AS cl_correo, " +
            "       v.placa AS v_placa, " +
            "       t.precio AS t_precio, " +
            "       t.valor_descuento AS t_descuento, " + // ✅ NUEVO
            "       t.id_tipo_tarifa AS t_tipo_tarifa, " +
            "       e.codigo AS e_codigo, e.id_estado_espacio AS e_estado_espacio " +
            "FROM pension p " +
            "INNER JOIN estado_pension ep ON p.id_estado_pension = ep.id_estado_pension " +
            "INNER JOIN cliente cl        ON p.id_cliente        = cl.id_cliente " +
            "INNER JOIN vehiculo v        ON p.id_vehiculo       = v.id_vehiculo " +
            "INNER JOIN tarifa t          ON p.id_tarifa         = t.id_tarifa " +
            "INNER JOIN espacio e         ON p.id_espacio        = e.id_espacio ";

    // =========================================================================
    // ESCRITURA
    // =========================================================================

    public Pension insertar(Pension p, Connection con) throws SQLException {
        String sql = "INSERT INTO pension " +
                "(id_cliente, id_vehiculo, id_tarifa, id_espacio, " +
                " id_estado_pension, id_codigo, fecha_inicio, fecha_fin, id_estacionamiento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCliente().getIdCliente());
            ps.setInt(2, p.getVehiculo().getIdVehiculo());
            ps.setInt(3, p.getTarifa().getIdTarifa());
            ps.setInt(4, p.getEspacio().getIdEspacio());
            ps.setInt(5, p.getEstadoPension().getIdEstadoPension());

            if (p.getCodigo() != null) {
                ps.setInt(6, p.getCodigo().getIdCodigo());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setDate(7, new java.sql.Date(p.getFechaInicio().getTime()));
            ps.setDate(8, new java.sql.Date(p.getFechaFin().getTime()));
            ps.setInt(9, p.getIdEstacionamiento());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setIdPension(rs.getInt(1));
                }
            }
        }
        return p;
    }

    public boolean actualizarTarifa(int idPension, int idTarifa) {
        String sql = "UPDATE pension SET id_tarifa = ? WHERE id_pension = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarifa);
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando tarifa de pensión id=" + idPension, e);
        }
    }

    public boolean actualizarTarifa(int idPension, int idTarifa, Connection con) throws SQLException {
        String sql = "UPDATE pension SET id_tarifa = ? WHERE id_pension = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarifa);
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarFechaFin(int idPension, java.util.Date nuevaFechaFin, Connection con) throws SQLException {
        String sql = "UPDATE pension SET fecha_fin = ? WHERE id_pension = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(nuevaFechaFin.getTime()));
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarFechaFin(int idPension, java.util.Date nuevaFechaFin) {
        String sql = "UPDATE pension SET fecha_fin = ? WHERE id_pension = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(nuevaFechaFin.getTime()));
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando fecha_fin de pensión id=" + idPension, e);
        }
    }

    public boolean actualizarEstado(int idPension, int nuevoEstado) {
        String sql = "UPDATE pension SET id_estado_pension = ? WHERE id_pension = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando estado de pensión id=" + idPension, e);
        }
    }

    public boolean actualizarEstado(int idPension, int nuevoEstado, Connection con) throws SQLException {
        String sql = "UPDATE pension SET id_estado_pension = ? WHERE id_pension = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idPension);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // LECTURA
    // =========================================================================

    public Pension buscarPorId(int idPension) {
        String sql = SELECT_BASE + "WHERE p.id_pension = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPension);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pensión id=" + idPension, e);
        }
        return null;
    }

    public Pension buscarActivaPorEspacio(int idEspacio) {
        String sql = SELECT_BASE +
                "WHERE p.id_espacio = ? AND p.id_estado_pension = ? " +
                "ORDER BY p.fecha_inicio DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);
            ps.setInt(2, MisConstantes.PENSION_ACTIVA);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pensión activa para espacio id=" + idEspacio, e);
        }
        return null;
    }

    public List<Pension> listarPorCliente(int idCliente) {
        return listar(SELECT_BASE + "WHERE p.id_cliente = ? ORDER BY p.fecha_inicio DESC", idCliente);
    }

    public List<Pension> listarPorEspacio(int idEspacio) {
        return listar(SELECT_BASE + "WHERE p.id_espacio = ? ORDER BY p.fecha_inicio DESC", idEspacio);
    }

    public List<Pension> listarActivas(int idEstacionamiento) {
        String sql = SELECT_BASE +
                "WHERE e.id_estacionamiento = ? AND p.id_estado_pension = ? " +
                "ORDER BY p.fecha_fin ASC";

        List<Pension> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.PENSION_ACTIVA);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando pensiones activas", e);
        }

        return lista;
    }

    public List<Pension> listarPorEstacionamiento(int idEstacionamiento) {
        String sql = SELECT_BASE +
                "WHERE e.id_estacionamiento = ? ORDER BY p.fecha_inicio DESC";

        List<Pension> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando pensiones", e);
        }

        return lista;
    }

    public List<Pension> listarVencidas() {
        String sql = SELECT_BASE +
                "WHERE p.id_estado_pension = ? AND p.fecha_fin < CURDATE()";

        List<Pension> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, MisConstantes.PENSION_ACTIVA);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando vencidas", e);
        }

        return lista;
    }

    public List<Pension> listarProximasAVencer(int dias) {
        String sql = SELECT_BASE +
                "WHERE p.id_estado_pension = ? " +
                "AND DATE(p.fecha_fin) = DATE_ADD(CURDATE(), INTERVAL ? DAY)";

        List<Pension> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, MisConstantes.PENSION_ACTIVA);
            ps.setInt(2, dias);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando próximas a vencer", e);
        }

        return lista;
    }

    // =========================================================================
    // MAPEO
    // =========================================================================

    private Pension mapear(ResultSet rs) throws SQLException {
        Pension p = new Pension();
        p.setIdPension(rs.getInt("id_pension"));

        // Fechas
        Date ini = rs.getDate("fecha_inicio");
        if (ini != null) p.setFechaInicio(new java.util.Date(ini.getTime()));

        Date fin = rs.getDate("fecha_fin");
        if (fin != null) p.setFechaFin(new java.util.Date(fin.getTime()));

        // Estado
        p.setEstadoPension(new EstadoPension(
                rs.getInt("id_estado_pension"),
                rs.getString("estado_nombre")));

        // Cliente
        Cliente cl = new Cliente();
        cl.setIdCliente(rs.getInt("id_cliente"));
        cl.setNombre(rs.getString("cl_nombre"));
        cl.setApellidoPaterno(rs.getString("cl_apellido_p"));
        cl.setApellidoMaterno(rs.getString("cl_apellido_m"));
        cl.setCorreo(rs.getString("cl_correo"));
        p.setCliente(cl);

        // Vehículo
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setPlaca(rs.getString("v_placa"));
        p.setVehiculo(v);

        // 🔥 Tarifa corregida
        Tarifa t = new Tarifa();
        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setPrecio(rs.getDouble("t_precio"));

        Double descuento = rs.getDouble("t_descuento");
        if (!rs.wasNull()) {
            t.setValorDescuento(descuento);
        }

        TipoTarifa tipoTarifa = new TipoTarifa();
        tipoTarifa.setIdTipoTarifa(rs.getInt("t_tipo_tarifa"));
        t.setTipoTarifa(tipoTarifa);

        p.setTarifa(t);

        // Espacio
        Espacio e = new Espacio();
        e.setIdEspacio(rs.getInt("id_espacio"));
        e.setCodigo(rs.getString("e_codigo"));

        EstadoEspacio estadoEsp = new EstadoEspacio();
        estadoEsp.setIdEstadoEspacio(rs.getInt("e_estado_espacio"));
        e.setEstadoEspacio(estadoEsp);

        p.setEspacio(e);

        return p;
    }

    private List<Pension> listar(String sql, int parametro) {
        List<Pension> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando pensiones", e);
        }

        return lista;
    }
}