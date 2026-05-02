package com.estacionamiento.dao;

import com.estacionamiento.modelo.Marca;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar el catálogo de marcas de vehículos.
 * @author saori
 */
public class MarcaDAO {

    public Marca buscarPorNombre(String nombre) {
        String sql = "SELECT id_marca, nombre_marca FROM marca_vehiculo WHERE UPPER(nombre_marca) = UPPER(?)";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Marca m = new Marca();
                    m.setIdMarca(rs.getInt("id_marca"));
                    m.setNombre(rs.getString("nombre_marca"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("[MarcaDAO] Error al buscar por nombre: " + e.getMessage());
        }
        return null;
    }

    
    public Marca insertar(Marca marca) {
    
        Marca existente = buscarPorNombre(marca.getNombre());
        if (existente != null) return existente;

        String sql = "INSERT INTO marca_vehiculo (nombre_marca) VALUES (?)";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, marca.getNombre().trim().toUpperCase());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    marca.setIdMarca(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MarcaDAO] Error al insertar marca: " + e.getMessage());
        }
        return marca;
    }

    public List<Marca> listarTodas() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT * FROM marca_vehiculo ORDER BY nombre_marca ASC";
        
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Marca m = new Marca();
                m.setIdMarca(rs.getInt("id_marca"));
                m.setNombre(rs.getString("nombre_marca"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("[MarcaDAO] Error al listar marcas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Marca marca) {
        String sql = "UPDATE marca_vehiculo SET nombre_marca = ? WHERE id_marca = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, marca.getNombre().toUpperCase());
            ps.setInt(2, marca.getIdMarca());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MarcaDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }
    public Marca buscarPorID (int id){
    
     String sql = "SELECT id_marca, nombre_marca FROM marca_vehiculo WHERE id_marca = ? ";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
             ps.setInt(1, id);
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Marca m = new Marca();
                    m.setIdMarca(rs.getInt("id_marca"));
                    m.setNombre(rs.getString("nombre_marca"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al buscar por id: " + e.getMessage());
        }
        return null;
    } 
}