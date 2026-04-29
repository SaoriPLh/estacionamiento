/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.dao;

import com.estacionamiento.modelo.Cliente;
import com.estacionamiento.modelo.Estacionamiento;
import com.estacionamiento.modelo.Tarifa;
import com.estacionamiento.modelo.TipoTarifa;
import com.estacionamiento.modelo.UnidadDescuento;
import com.estacionamiento.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.estacionamiento.modelo.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClienteDAO {

    private static final String SQL_CLIENTE =
        "SELECT c.id_cliente, c.nombre, c.apellido_paterno, c.apellido_materno, " +
        "       c.correo, c.telefono, c.tipo_cliente, " +
        "       t.id_tarifa, t.tipo_cobro, t.precio, t.valor_descuento, " +
        "       tt.id_tipo_tarifa, tt.nombre AS tipo_tarifa, " +
        "       ud.id_unidad_descuento, ud.nombre AS unidad_descuento, " +
        "       est.id_estacionamiento, est.nombre AS nombre_zona " +
        "FROM cliente c " +
        "JOIN tarifa t               ON c.id_tarifa           = t.id_tarifa " +
        "JOIN tipo_tarifa tt         ON t.id_tipo_tarifa      = tt.id_tipo_tarifa " +
        "JOIN unidad_descuento ud    ON t.id_unidad_descuento = ud.id_unidad_descuento " +
        "JOIN estacionamiento est    ON t.id_estacionamiento  = est.id_estacionamiento ";

    public Cliente buscarPorId(int idCliente) {
        String sql = SQL_CLIENTE +
            "WHERE c.id_cliente = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAO] Error en buscarPorId: " + e.getMessage());
        }
        return null;
    }

    // Buscar un cliente por correo electrónico
    public Cliente buscarPorCorreo(String correo) {
        String sql = SQL_CLIENTE +
            "WHERE LOWER(c.correo) = LOWER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAO] Error en buscarPorCorreo: " + e.getMessage());
        }
        return null;
    }

    //Listar solo clientes pensionados
    public List<Cliente> listarPensionados() {
        String sql = SQL_CLIENTE +
            "WHERE LOWER(c.tipo_cliente) = 'pension' " +
            "ORDER BY c.apellido_paterno ASC";
        return ejecutarLista(sql, ps -> {});
    }

    //Listar todos los clientes registrados
    public List<Cliente> listarTodos() {
        String sql = SQL_CLIENTE +
            "ORDER BY c.apellido_paterno ASC";
        return ejecutarLista(sql, ps -> {});
    }

    // Insertar un cliente nuevo y retornar el ID generado
    public int insertar(Cliente cliente) {
        String sql =
            "INSERT INTO cliente " +
            "(id_tarifa, nombre, apellido_paterno, apellido_materno, " +
            " correo, telefono, tipo_cliente) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cliente.getTarifa().getIdTarifa());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidoPaterno());
            ps.setString(4, cliente.getApellidoMaterno());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getTelefono());
            ps.setString(7, cliente.getTipoCliente());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAO] Error en insertar: " + e.getMessage());
        }
        return -1;
    }

    //Actualizar teléfono de un cliente (dato que cambia con frecuencia)
    public boolean actualizarTelefono(int idCliente, String nuevoTelefono) {
        String sql =
            "UPDATE cliente " +
            "SET telefono = ? " +
            "WHERE id_cliente = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoTelefono);
            ps.setInt(2, idCliente);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ClienteDAO] Error en actualizarTelefono: " + e.getMessage());
            return false;
        }
    }

    @FunctionalInterface
    private interface Parametrizador {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private List<Cliente> ejecutarLista(String sql, Parametrizador p) {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            p.aplicar(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {

        Estacionamiento zona = new Estacionamiento();
        zona.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
        zona.setNombre(rs.getString("nombre_zona"));

        TipoTarifa tipoTarifa = new TipoTarifa();
        tipoTarifa.setIdTipoTarifa(rs.getInt("id_tipo_tarifa"));
        tipoTarifa.setDescripcion(rs.getString("tipo_tarifa"));

        UnidadDescuento unidadDescuento = new UnidadDescuento();
        unidadDescuento.setIdUnidadDescuento(rs.getInt("id_unidad_descuento"));
        unidadDescuento.setTipoUnidad(rs.getString("unidad_descuento"));

        Tarifa tarifa = new Tarifa();
        tarifa.setIdTarifa(rs.getInt("id_tarifa"));
        tarifa.setEstacionamiento(zona);
        tarifa.setTipoTarifa(tipoTarifa);
        tarifa.setUnidadDescuento(unidadDescuento);
        tarifa.setTipoCobro(rs.getString("tipo_cobro"));
        tarifa.setPrecio(rs.getDouble("precio"));
        tarifa.setValorDescuento(rs.getDouble("valor_descuento"));

        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setTarifa(tarifa);
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellidoPaterno(rs.getString("apellido_paterno"));
        cliente.setApellidoMaterno(rs.getString("apellido_materno"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setTipoCliente(rs.getString("tipo_cliente"));

        return cliente;
    }
}


