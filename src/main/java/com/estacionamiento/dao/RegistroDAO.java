package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    public Registro insertar(Registro reg) {
        String sql = "INSERT INTO registro (id_tarifa, id_vehiculo, id_espacio, id_persona, id_estado_registro, id_codigo, hora_entrada, monto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reg.getTarifa().getIdTarifa());
            ps.setInt(2, reg.getVehiculo().getIdVehiculo());
            ps.setInt(3, reg.getEspacio().getIdEspacio());
            ps.setInt(4, reg.getPersona().getIdPersona());
            ps.setInt(5, reg.getEstadoRegistro().getIdEstadoRegistro());

            if (reg.getCodigoAcceso() != null) {
                ps.setInt(6, reg.getCodigoAcceso().getIdCodigo());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setTimestamp(7, Timestamp.valueOf(reg.getHoraEntrada()));
            ps.setDouble(8, reg.getMonto());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) reg.setIdRegistro(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("[RegistroDAO] Error al insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return reg;
    }

    public Registro buscarPorId(int id) {
        String sql = "SELECT r.*, v.placa, v.modelo, e.codigo AS codigo_espacio, " +
                     "p.nombre AS nombre_empleado, er.nombre_estado AS estado_nombre, " +
                     "t.id_tarifa, tt.id_tipo_tarifa, tt.descripcion AS tipo_tarifa_desc " +
                     "FROM registro r " +
                     "JOIN vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
                     "JOIN espacio e ON r.id_espacio = e.id_espacio " +
                     "JOIN persona p ON r.id_persona = p.id_persona " +
                     "JOIN estado_registro er ON r.id_estado_registro = er.id_estado_registro " +
                     "JOIN tarifa t ON r.id_tarifa = t.id_tarifa " +
                     "JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
                     "WHERE r.id_registro = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearRegistroCompleto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registrarSalida(int idRegistro, Timestamp horaSalida, double monto) {
        String sql = "UPDATE registro SET hora_salida = ?, monto = ?, id_estado_registro = ? WHERE id_registro = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, horaSalida);
            ps.setDouble(2, monto);
            ps.setInt(3, MisConstantes.REGISTRO_FINALIZADO);
            ps.setInt(4, idRegistro);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Registro> listarPorFecha(String fecha) {
        List<Registro> lista = new ArrayList<>();

        String sql = "SELECT r.*, v.placa, v.modelo, e.codigo AS codigo_espacio, " +
                     "p.nombre AS nombre_empleado, er.nombre_estado AS estado_nombre, " +
                     "t.id_tarifa, tt.id_tipo_tarifa, tt.descripcion AS tipo_tarifa_desc " +
                     "FROM registro r " +
                     "INNER JOIN vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
                     "INNER JOIN espacio e ON r.id_espacio = e.id_espacio " +
                     "INNER JOIN persona p ON r.id_persona = p.id_persona " +
                     "INNER JOIN estado_registro er ON r.id_estado_registro = er.id_estado_registro " +
                     "INNER JOIN tarifa t ON r.id_tarifa = t.id_tarifa " +
                     "INNER JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
                     "WHERE DATE(r.fecha_registro) = ? " +
                     "ORDER BY r.hora_entrada DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRegistroCompleto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar registros por fecha: " + e.getMessage());
        }
        return lista;
    }

    public List<Registro> filtrarRegistros(LocalDateTime inicio, LocalDateTime fin, Integer idTipoTarifa) {
        List<Registro> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT r.*, v.placa, v.modelo, e.codigo AS codigo_espacio, " +
            "p.nombre AS nombre_empleado, er.nombre_estado AS estado_nombre, " +
            "t.id_tarifa, tt.id_tipo_tarifa, tt.descripcion AS tipo_tarifa_desc " +
            "FROM registro r " +
            "INNER JOIN vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
            "INNER JOIN espacio e ON r.id_espacio = e.id_espacio " +
            "INNER JOIN persona p ON r.id_persona = p.id_persona " +
            "INNER JOIN estado_registro er ON r.id_estado_registro = er.id_estado_registro " +
            "INNER JOIN tarifa t ON r.id_tarifa = t.id_tarifa " +
            "INNER JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (inicio != null && fin == null) {
          
            sql.append("AND r.fecha_registro BETWEEN ? AND ? ");
            params.add(Timestamp.valueOf(inicio.toLocalDate().atStartOfDay()));
            params.add(Timestamp.valueOf(inicio.toLocalDate().atTime(23, 59, 59)));

        } else if (inicio != null) {
            // Rango con inicio y fin
            sql.append("AND r.fecha_registro BETWEEN ? AND ? ");
            params.add(Timestamp.valueOf(inicio));
            params.add(Timestamp.valueOf(fin));

        } else if (fin != null) {
            // Solo fecha fin = todo hasta esa fecha
            sql.append("AND r.fecha_registro <= ? ");
            params.add(Timestamp.valueOf(fin));
        }
        // Si ambos son null = sin filtro de fecha = todos los registros

        if (idTipoTarifa != null && idTipoTarifa > 0) {
            sql.append("AND tt.id_tipo_tarifa = ? ");
            params.add(idTipoTarifa);
        }

        sql.append("ORDER BY r.hora_entrada DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRegistroCompleto(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al filtrar registros: " + e.getMessage());
        }

        return lista;
    }

 
    private Registro mapearRegistroCompleto(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setPlaca(rs.getString("placa"));
        v.setModelo(rs.getString("modelo")); // ← FALTABA esto

        Espacio esp = new Espacio();
        esp.setIdEspacio(rs.getInt("id_espacio"));
        esp.setCodigo(rs.getString("codigo_espacio"));

        Persona emp = new Persona();
        emp.setIdPersona(rs.getInt("id_persona"));
        emp.setNombre(rs.getString("nombre_empleado"));

        EstadoRegistro estado = new EstadoRegistro();
        estado.setIdEstadoRegistro(rs.getInt("id_estado_registro"));
        estado.setNombreEstado(rs.getString("estado_nombre"));

        TipoTarifa tt = new TipoTarifa();
        tt.setIdTipoTarifa(rs.getInt("id_tipo_tarifa"));
        tt.setDescripcion(rs.getString("tipo_tarifa_desc"));

        Tarifa t = new Tarifa();
        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setTipoTarifa(tt);

        Registro reg = new Registro();
        reg.setIdRegistro(rs.getInt("id_registro"));
        reg.setMonto(rs.getDouble("monto"));

       
        reg.setHoraEntrada(rs.getTimestamp("hora_entrada").toLocalDateTime());

        
        Timestamp tsSalida = rs.getTimestamp("hora_salida");
        reg.setHoraSalida(tsSalida != null ? tsSalida.toLocalDateTime() : null);

        
        Timestamp tsFecha = rs.getTimestamp("fecha_registro");
        if (tsFecha != null) {
            reg.setFechaRegistro(tsFecha.toLocalDateTime());
        }

        reg.setVehiculo(v);
        reg.setEspacio(esp);
        reg.setPersona(emp);
        reg.setEstadoRegistro(estado);
        reg.setTarifa(t);

        int idCod = rs.getInt("id_codigo");
        if (!rs.wasNull()) {
            CodigoAcceso c = new CodigoAcceso();
            c.setIdCodigo(idCod);
            reg.setCodigoAcceso(c);
        }

        return reg;
    }
}