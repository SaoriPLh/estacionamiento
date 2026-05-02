package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionamientoDAO {

    public Estacionamiento buscarPorId(int id) {
      
        String sql = "SELECT e.id_estacionamiento, e.nombre, e.activo, " +
                     "emp.id_empresa, emp.nombre_comercial, " +
                     "d.id_direccion, d.calle, " +
                     "c.id_ciudad, c.nombre AS ciudad, " +
                     "es.id_estado, es.nombre AS estado " +
                     "FROM estacionamiento e " +
                     "JOIN empresa emp ON e.id_empresa = emp.id_empresa " +
                     "JOIN direccion d ON e.id_direccion = d.id_direccion " +
                     "JOIN ciudad c ON d.id_ciudad = c.id_ciudad " +
                     "JOIN estado es ON c.id_estado = es.id_estado " +
                     "WHERE e.id_estacionamiento = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    Estacionamiento est = new Estacionamiento();
                    est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                    est.setNombre(rs.getString("nombre"));
                 
                    est.setActivo(rs.getBoolean("activo")); 
                    est.setEmpresa(empresa);
                    est.setDireccion(direccion);

                    return est;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Estacionamiento> listarPorEmpresa(int idEmpresa) {
        List<Estacionamiento> lista = new ArrayList<>();
        
     
        String sql = "SELECT e.id_estacionamiento, e.nombre, e.activo, " +
                     "d.id_direccion, d.calle, c.id_ciudad, c.nombre AS ciudad, " +
                     "es.id_estado, es.nombre AS estado " +
                     "FROM estacionamiento e " +
                     "INNER JOIN direccion d ON e.id_direccion = d.id_direccion " +
                     "INNER JOIN ciudad c ON d.id_ciudad = c.id_ciudad " +
                     "INNER JOIN estado es ON c.id_estado = es.id_estado " +
                     "WHERE e.id_empresa = ? AND e.activo = 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    Estacionamiento est = new Estacionamiento();
                    est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                    est.setNombre(rs.getString("nombre"));
                    est.setActivo(rs.getBoolean("activo"));
                    est.setDireccion(direccion);

                    lista.add(est);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar por empresa: " + e.getMessage());
        }
        return lista;
    }

    public Estacionamiento insertarEstacionamiento(Estacionamiento estacionamiento) {

        String sql = "INSERT INTO estacionamiento (id_empresa, id_direccion, nombre, activo) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
            ps.setInt(1, estacionamiento.getEmpresa().getIdEmpresa());
            ps.setInt(2, estacionamiento.getDireccion().getIdDireccion());
            ps.setString(3, estacionamiento.getNombre());
        
            ps.setBoolean(4, estacionamiento.getActivo());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        estacionamiento.setIdEstacionamiento(rs.getInt(1));
                    }
                }
                System.out.println("Estacionamiento guardado con éxito: " + estacionamiento.getNombre());
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar estacionamiento: " + e.getMessage());
        }
        return estacionamiento; 
    }

    public boolean desactivar(int idEstacionamiento) {
            String sql = "UPDATE estacionamiento SET activo = 0 WHERE id_estacionamiento = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idEstacionamiento);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
}