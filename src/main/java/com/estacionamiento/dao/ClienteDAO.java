package com.estacionamiento.dao;

import com.estacionamiento.modelo.Cliente;
import com.estacionamiento.modelo.Tarifa;
import com.estacionamiento.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente insertar(Cliente cliente, Connection con) throws SQLException {
        String sql = "INSERT INTO cliente " +
                     "(id_tarifa, nombre, apellido_paterno, apellido_materno, " +
                     " correo, telefono) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (cliente.getTarifa() != null) {
                ps.setInt(1, cliente.getTarifa().getIdTarifa());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidoPaterno());
            ps.setString(4, cliente.getApellidoMaterno());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getTelefono());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setIdCliente(rs.getInt(1));
                    }
                }
            }
        }
        return cliente;
    }

    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT c.*, t.precio " +
                     "FROM cliente c " +
                     "LEFT JOIN tarifa t ON c.id_tarifa = t.id_tarifa " +
                     "WHERE c.id_cliente = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT c.*, t.precio " +
                     "FROM cliente c " +
                     "LEFT JOIN tarifa t ON c.id_tarifa = t.id_tarifa";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET " +
                     "id_tarifa = ?, nombre = ?, apellido_paterno = ?, " +
                     "apellido_materno = ?, correo = ?, telefono = ? " +
                     "WHERE id_cliente = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (cliente.getTarifa() != null) {
                ps.setInt(1, cliente.getTarifa().getIdTarifa());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidoPaterno());
            ps.setString(4, cliente.getApellidoMaterno());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getTelefono());
            ps.setInt(7, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> listarClientesPensionadosPorEstacionamiento(int idEstacionamiento) throws SQLException {
    List<Cliente> lista = new ArrayList<>();

    String sql = "SELECT DISTINCT c.*, t.precio " +
                 "FROM cliente c " +
                 "JOIN pension p ON p.id_cliente = c.id_cliente " +
                 "JOIN espacio e ON p.id_espacio = e.id_espacio " +
                 "LEFT JOIN tarifa t ON c.id_tarifa = t.id_tarifa " +
                 "WHERE p.id_estado_pension = 1 " +
                 "AND e.id_estacionamiento = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idEstacionamiento);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        }
    }

    return lista;
}

    public boolean eliminar(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellidoPaterno(rs.getString("apellido_paterno"));
        c.setApellidoMaterno(rs.getString("apellido_materno"));
        c.setCorreo(rs.getString("correo"));
        c.setTelefono(rs.getString("telefono"));

        int idTarifa = rs.getInt("id_tarifa");
        if (!rs.wasNull()) {
            Tarifa t = new Tarifa();
            t.setIdTarifa(idTarifa);
            t.setPrecio(rs.getDouble("precio"));
            c.setTarifa(t);
        }

        return c;
    }
}