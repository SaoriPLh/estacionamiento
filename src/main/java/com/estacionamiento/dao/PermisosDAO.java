    package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisosDAO {

    public List<Permiso> obtenerPermisosPorPersona(int idPersona) {
        List<Permiso> lista = new ArrayList<>();
       
        String sql = "SELECT p.id_permiso, p.fecha_asignacion, " +
                     "e.id_estacionamiento, e.nombre AS nombre_estac, " +
                     "ep.id_estado_permiso, ep.nombre_estado " +
                     "FROM permiso p " +
                     "JOIN estacionamiento e ON p.id_estacionamiento = e.id_estacionamiento " +
                     "JOIN estado_permiso ep ON p.id_estado_permiso = ep.id_estado_permiso " +
                     "WHERE p.id_persona = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Creamos el estacionamiento 
                    Estacionamiento est = new Estacionamiento();
                    est.setIdEstacionamiento(rs.getInt("id_estacionamiento"));
                    est.setNombre(rs.getString("nombre_estac"));

                    // Creamos el estado 
                    EstadoPermiso estado = new EstadoPermiso();
                    estado.setIdEstadoPermiso(rs.getInt("id_estado_permiso"));
                    estado.setNombreEstado(rs.getString("nombre_estado"));

                    // Creamos el permiso
                    Permiso permiso = new Permiso();
                    permiso.setIdPermiso(rs.getInt("id_permiso"));
                    permiso.setFechaAsignacion(rs.getTimestamp("fecha_asignacion"));
                    permiso.setEstacionamiento(est);
                    permiso.setEstadoPermiso(estado);

                    lista.add(permiso);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
public int asignarPermisos(int idPersona, int idEstacionamiento) {
    String sql = "INSERT INTO permiso (id_persona, id_estacionamiento, id_estado_permiso, fecha_asignacion) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
    
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        ps.setInt(1, idPersona);
        ps.setInt(2, idEstacionamiento);
        ps.setInt(3, MisConstantes.PERMISO_ACTIVO);
        
        ps.executeUpdate();

       
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1); 
            }
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return 0; 
}

public boolean revocarTodosLosPermisos(int idPersona) {
    
    String sql = "UPDATE permiso SET id_estado_permiso = ? WHERE id_persona = ?";
    
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, MisConstantes.PERMISO_REVOCADO); 
        ps.setInt(2, idPersona);
        
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public List<Persona> listarPersonalPorEstacionamiento(int idEstacionamiento) {
    List<Persona> lista = new ArrayList<>();
    String sql = "SELECT p.*, r.nombre_rol " +
                 "FROM permiso per " +
                 "JOIN persona p ON per.id_persona = p.id_persona " +
                 "JOIN rol r ON p.id_rol = r.id_rol " +
                 "WHERE per.id_estacionamiento = ? AND per.id_estado_permiso = 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idEstacionamiento);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
           
            Rol rol = new Rol();
            rol.setIdRol(rs.getInt("id_rol"));
            rol.setNombreRol(rs.getString("nombre_rol"));

           
            Persona p = new Persona();
            p.setIdPersona(rs.getInt("id_persona"));
            p.setNombre(rs.getString("nombre"));
            p.setApellidoPaterno(rs.getString("apellido_paterno"));
            p.setUsername(rs.getString("username"));
            p.setRol(rol);
            p.setRequiereCambio(rs.getInt("requiere_cambio") == 1);

            lista.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}


}