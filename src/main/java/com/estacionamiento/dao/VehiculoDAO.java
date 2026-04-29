package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    private static final String SQL_VEHICULO =
        "SELECT v.id_vehiculo, v.placa, v.modelo, v.color, " +
        "       m.id_marca, m.nombre AS nombre_marca, " +
        "       c.id_cliente, c.nombre AS nombre_cliente, " +
        "       c.apellido_paterno, c.apellido_materno, " +
        "       c.correo, c.telefono, c.tipo_cliente " +
        "FROM vehiculo v " +
        "JOIN marca m    ON v.id_marca    = m.id_marca " +
        "JOIN cliente c  ON v.id_cliente  = c.id_cliente ";

    //Buscar un vehículo por su placa 
    public Vehiculo buscarPorPlaca(String placa) {
        String sql = SQL_VEHICULO +
            "WHERE UPPER(v.placa) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearVehiculo(rs);
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error en buscarPorPlaca: " + e.getMessage());
        }
        return null;
    }

    //Buscar un vehículo
    public Vehiculo buscarPorId(int idVehiculo) {
        String sql = SQL_VEHICULO +
            "WHERE v.id_vehiculo = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearVehiculo(rs);
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error en buscarPorId: " + e.getMessage());
        }
        return null;
    }

    //Todos los vehículos de un cliente (pensionado con varios autos)
    public List<Vehiculo> listarPorCliente(int idCliente) {
        String sql = SQL_VEHICULO +
            "WHERE v.id_cliente = ? " +
            "ORDER BY v.placa ASC";
        return ejecutarLista(sql, ps -> ps.setInt(1, idCliente));
    }

    // Verificar si una placa ya está registrada en el sistema
    public boolean existePlaca(String placa) {
        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM vehiculo " +
            "WHERE UPPER(placa) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error en existePlaca: " + e.getMessage());
        }
        return false;
    }

    //Insertar un vehículo nuevo y retornar el ID generado por la BD
    public int insertar(Vehiculo vehiculo) {
        String sql =
            "INSERT INTO vehiculo (id_cliente, id_marca, placa, modelo, color) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vehiculo.getCliente().getIdCliente());
            ps.setInt(2, vehiculo.getMarca().getIdMarca());
            ps.setString(3, vehiculo.getPlaca());
            ps.setString(4, vehiculo.getModelo());
            ps.setString(5, vehiculo.getColor());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error en insertar: " + e.getMessage());
        }
        return -1;
    }


    @FunctionalInterface
    private interface Parametrizador {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private List<Vehiculo> ejecutarLista(String sql, Parametrizador p) {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            p.aplicar(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearVehiculo(rs));
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }

    private Vehiculo mapearVehiculo(ResultSet rs) throws SQLException {

        Marca marca = new Marca();
        marca.setIdMarca(rs.getInt("id_marca"));
        marca.setNombre(rs.getString("nombre_marca"));

        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre_cliente"));
        cliente.setApellidoPaterno(rs.getString("apellido_paterno"));
        cliente.setApellidoMaterno(rs.getString("apellido_materno"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setTipoCliente(rs.getString("tipo_cliente"));

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(rs.getInt("id_vehiculo"));
        vehiculo.setPlaca(rs.getString("placa"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setColor(rs.getString("color"));
        vehiculo.setMarca(marca);
        vehiculo.setCliente(cliente);

        return vehiculo;
    }
}
