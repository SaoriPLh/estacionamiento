package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de vehículos en el sistema CyberMining.
 * @author saori
 */
public class VehiculoDAO {
    
    public Vehiculo insertarVehiculo(Vehiculo vehiculo) {
        
        Vehiculo existente = buscarPorPlaca(vehiculo.getPlaca());
        if (existente != null) {
            return existente;
        }

        String sql = "INSERT INTO vehiculo (id_cliente, id_marca, placa, modelo, color) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
         
            if (vehiculo.getCliente() != null && vehiculo.getCliente().getIdCliente() > 0) {
                ps.setInt(1, vehiculo.getCliente().getIdCliente());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            
            ps.setInt(2, vehiculo.getMarca().getIdMarca());
            ps.setString(3, vehiculo.getPlaca().trim().toUpperCase());
            ps.setString(4, vehiculo.getModelo()); 
            ps.setString(5, vehiculo.getColor());   

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehiculo.setIdVehiculo(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("VehiculoDAO Error al insertar: " + e.getMessage());
        }
        return vehiculo;
    }

    public Vehiculo buscarPorPlaca(String placa) {
        
        String sql = "SELECT v.*, m.nombre_marca FROM vehiculo v " +
                     "JOIN marca_vehiculo m ON v.id_marca = m.id_marca " +
                     "WHERE UPPER(v.placa) = UPPER(?)";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, placa.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearVehiculo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[VehiculoDAO] Error al buscar placa: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarVehiculo(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculo SET id_cliente = ?, id_marca = ?, modelo = ?, color = ? WHERE id_vehiculo = ?";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            if (vehiculo.getCliente() != null) {
                ps.setInt(1, vehiculo.getCliente().getIdCliente());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            
            ps.setInt(2, vehiculo.getMarca().getIdMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setString(4, vehiculo.getColor());
            ps.setInt(5, vehiculo.getIdVehiculo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("vehiculoDAO Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarVehiculo(int idVehiculo) {
        String sql = "DELETE FROM vehiculo WHERE id_vehiculo = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("vehiculoDAO Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    
    private Vehiculo mapearVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo(); 
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setPlaca(rs.getString("placa"));
        v.setModelo(rs.getString("modelo"));
        v.setColor(rs.getString("color"));

     
        Marca m = new Marca();
        m.setIdMarca(rs.getInt("id_marca"));
        m.setNombre(rs.getString("nombre_marca"));
        v.setMarca(m);

        
        return v;
    }
}