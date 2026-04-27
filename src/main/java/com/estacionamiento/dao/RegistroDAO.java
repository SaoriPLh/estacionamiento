package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

  
    /** Agrupa los datos de un registro junto con sus relaciones (JOIN). */
    public static class RegistroInfo {
        public final Registro registro;
        public final String placa;
        public final String modelo;
        public final String color;
        public final String codigoEspacio;
        public final String nombreZona;
        public final String estadoRegistro;

        public RegistroInfo(Registro registro, String placa, String modelo, String color,
                            String codigoEspacio, String nombreZona, String estadoRegistro) {
            this.registro       = registro;
            this.placa          = placa;
            this.modelo         = modelo;
            this.color          = color;
            this.codigoEspacio  = codigoEspacio;
            this.nombreZona     = nombreZona;
            this.estadoRegistro = estadoRegistro;
        }
    }

    /** Agrupa los datos de un permiso junto con sus relaciones (JOIN). */
    public static class PermisoInfo {
        public final Permisos permiso;
        public final String nombre;
        public final String apellidoPaterno;
        public final String nombreZona;
        public final String estadoPermiso;

        public PermisoInfo(Permisos permiso, String nombre, String apellidoPaterno,
                           String nombreZona, String estadoPermiso) {
            this.permiso         = permiso;
            this.nombre          = nombre;
            this.apellidoPaterno = apellidoPaterno;
            this.nombreZona      = nombreZona;
            this.estadoPermiso   = estadoPermiso;
        }
    }

   
    private static final String SQL_REGISTRO =
        "SELECT r.id_registro, r.hora_entrada, r.hora_salida, r.monto, r.fecha_registro, " +
        "       r.id_vehiculo, r.id_espacio, r.id_persona, r.id_estado_registro, r.id_codigo, " +
        "       v.placa, v.modelo, v.color, " +
        "       e.codigo AS codigo_espacio, " +
        "       est.id_estacionamiento, est.nombre AS nombre_zona, " +
        "       er.nombre_estado AS estado_registro " +
        "FROM registro r " +
        "JOIN vehiculo v          ON r.id_vehiculo         = v.id_vehiculo " +
        "JOIN espacio e           ON r.id_espacio          = e.id_espacio " +
        "JOIN estacionamiento est ON e.id_estacionamiento  = est.id_estacionamiento " +
        "JOIN estado_registro er  ON r.id_estado_registro  = er.id_estado_registro ";

    private static final String SQL_PERMISO =
        "SELECT p.id_permiso, p.fecha_asignacion, " +
        "       p.id_estacionamiento, p.id_persona, p.id_estado_permiso, " +
        "       per.nombre, per.apellido_paterno, " +
        "       est.nombre AS nombre_zona, " +
        "       ep.nombre_estado AS estado_permiso " +
        "FROM permiso p " +
        "JOIN persona per          ON p.id_persona         = per.id_persona " +
        "JOIN estacionamiento est  ON p.id_estacionamiento = est.id_estacionamiento " +
        "JOIN estado_permiso ep    ON p.id_estado_permiso  = ep.id_estado_permiso ";

   
    public List<RegistroInfo> buscarPorPlaca(String placa) {
        String sql = SQL_REGISTRO +
            "WHERE UPPER(v.placa) = UPPER(?) " +
            "ORDER BY r.hora_entrada DESC";
        return ejecutarListaRegistros(sql, ps -> ps.setString(1, placa));
    }

    public List<RegistroInfo> buscarPorFecha(String fecha) {
        String sql = SQL_REGISTRO +
            "WHERE DATE(r.hora_entrada) = ? " +
            "ORDER BY r.hora_entrada ASC";
        return ejecutarListaRegistros(sql, ps -> ps.setString(1, fecha));
    }

    
    public List<RegistroInfo> buscarPorRangoFechas(String fechaInicio, String fechaFin) {
        String sql = SQL_REGISTRO +
            "WHERE DATE(r.hora_entrada) BETWEEN ? AND ? " +
            "ORDER BY r.hora_entrada ASC";
        return ejecutarListaRegistros(sql, ps -> {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
        });
    }

    public List<RegistroInfo> buscarActivos() {
        String sql = SQL_REGISTRO +
            "WHERE r.hora_salida IS NULL " +
            "ORDER BY r.hora_entrada ASC";
        return ejecutarListaRegistros(sql, ps -> {});
    }

    public List<RegistroInfo> buscarPorZona(int idEstacionamiento) {
        String sql = SQL_REGISTRO +
            "WHERE est.id_estacionamiento = ? " +
            "ORDER BY r.hora_entrada DESC";
        return ejecutarListaRegistros(sql, ps -> ps.setInt(1, idEstacionamiento));
    }

  
    // -----------------------------------------------------------------------
    public List<RegistroInfo> buscarPorPlacaYFecha(String placa, String fecha) {
        String sql = SQL_REGISTRO +
            "WHERE UPPER(v.placa) = UPPER(?) AND DATE(r.hora_entrada) = ? " +
            "ORDER BY r.hora_entrada ASC";
        return ejecutarListaRegistros(sql, ps -> {
            ps.setString(1, placa);
            ps.setString(2, fecha);
        });
    }

    // -----------------------------------------------------------------------
    public List<Object[]> calcularIngresosPorZona() {
        List<Object[]> resultados = new ArrayList<>();
        String sql =
            "SELECT est.nombre AS nombre_zona, " +
            "       COALESCE(SUM(r.monto), 0) AS total_ingresos, " +
            "       COUNT(r.id_registro)       AS total_registros " +
            "FROM estacionamiento est " +
            "LEFT JOIN espacio e  ON e.id_estacionamiento = est.id_estacionamiento " +
            "LEFT JOIN registro r ON r.id_espacio         = e.id_espacio " +
            "GROUP BY est.id_estacionamiento, est.nombre " +
            "ORDER BY total_ingresos DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(new Object[]{
                    rs.getString("nombre_zona"),
                    rs.getDouble("total_ingresos"),
                    rs.getInt("total_registros")
                });
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error en calcularIngresosPorZona: " + e.getMessage());
        }
        return resultados;
    }

  
    public List<PermisoInfo> buscarPensionadosActivos() {
        String sql = SQL_PERMISO +
            "WHERE UPPER(ep.nombre_estado) = 'ACTIVO' " +
            "ORDER BY p.fecha_asignacion ASC";
        return ejecutarListaPermisos(sql, ps -> {});
    }

   
    public List<PermisoInfo> buscarPensionadosPorVencer(int dias) {
        String sql = SQL_PERMISO +
            "WHERE UPPER(ep.nombre_estado) = 'ACTIVO' " +
            "  AND DATE_ADD(p.fecha_asignacion, INTERVAL 30 DAY) " +
            "      BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
            "ORDER BY p.fecha_asignacion ASC";
        return ejecutarListaPermisos(sql, ps -> ps.setInt(1, dias));
    }

   
    public List<RegistroInfo> buscarPorEspacio(String codigoEspacio) {
        String sql = SQL_REGISTRO +
            "WHERE UPPER(e.codigo) = UPPER(?) " +
            "ORDER BY r.hora_entrada DESC";
        return ejecutarListaRegistros(sql, ps -> ps.setString(1, codigoEspacio));
    }

    public int contarVisitasPorPlaca(String placa) {
        String sql =
            "SELECT COUNT(r.id_registro) AS total " +
            "FROM registro r " +
            "JOIN vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
            "WHERE UPPER(v.placa) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error en contarVisitasPorPlaca: " + e.getMessage());
        }
        return 0;
    }

    public RegistroInfo buscarUltimaEntrada(String placa) {
        String sql = SQL_REGISTRO +
            "WHERE UPPER(v.placa) = UPPER(?) " +
            "ORDER BY r.hora_entrada DESC " +
            "LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearRegistro(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error en buscarUltimaEntrada: " + e.getMessage());
        }
        return null;
    }

    
    @FunctionalInterface
    private interface Parametrizador {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private List<RegistroInfo> ejecutarListaRegistros(String sql, Parametrizador p) {
        List<RegistroInfo> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            p.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearRegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }

    private List<PermisoInfo> ejecutarListaPermisos(String sql, Parametrizador p) {
        List<PermisoInfo> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            p.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearPermiso(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }

  
    private RegistroInfo mapearRegistro(ResultSet rs) throws SQLException {
        Registro r = new Registro();
        r.setIdRegistro(rs.getInt("id_registro"));
        r.setIdVehiculo(rs.getInt("id_vehiculo"));
        r.setIdEspacio(rs.getInt("id_espacio"));
        r.setIdPersona(rs.getInt("id_persona"));
        r.setIdEstadoRegistro(rs.getInt("id_estado_registro"));
        r.setIdCodigo(rs.getInt("id_codigo"));
        Timestamp tsEntrada = rs.getTimestamp("hora_entrada");
        Timestamp tsSalida  = rs.getTimestamp("hora_salida");
        Timestamp tsFecha   = rs.getTimestamp("fecha_registro");
        r.setHoraEntrada (tsEntrada != null ? tsEntrada.toString() : null);
        r.setHoraSalida  (tsSalida  != null ? tsSalida.toString()  : null);
        r.setFechaRegistro(tsFecha  != null ? tsFecha.toString()   : null);
        r.setMonto(rs.getDouble("monto"));

        return new RegistroInfo(
            r,
            rs.getString("placa"),
            rs.getString("modelo"),
            rs.getString("color"),
            rs.getString("codigo_espacio"),
            rs.getString("nombre_zona"),
            rs.getString("estado_registro")
        );
    }

    private PermisoInfo mapearPermiso(ResultSet rs) throws SQLException {
        Permisos p = new Permisos();
        p.setIdPermiso(rs.getInt("id_permiso"));
        p.setIdPersona(rs.getInt("id_persona"));
        p.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
        p.setIdEstadoPermiso(rs.getInt("id_estado_permiso"));
        Date fecha = rs.getDate("fecha_asignacion");
        p.setFechaAsignacion(fecha != null ? fecha.toString() : null);

        return new PermisoInfo(
            p,
            rs.getString("nombre"),
            rs.getString("apellido_paterno"),
            rs.getString("nombre_zona"),
            rs.getString("estado_permiso")
        );
    }
}
