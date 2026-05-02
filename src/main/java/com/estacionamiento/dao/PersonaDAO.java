package com.estacionamiento.dao;

import com.estacionamiento.modelo.Empresa;
import com.estacionamiento.modelo.Persona;
import com.estacionamiento.modelo.Rol;
import com.estacionamiento.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public Persona insertar(Persona p) {
        String sql = "INSERT INTO persona (id_empresa, id_rol, nombre, apellido_paterno, apellido_materno, username, password, salario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getEmpresa().getIdEmpresa());
            ps.setInt(2, p.getRol().getIdRol());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getApellidoPaterno());
            ps.setString(5, p.getApellidoMaterno());
            ps.setString(6, p.getUsername());
            ps.setString(7, p.getPassword());
            
            // Manejo de nulo para salario
            if (p.getSalario()!= null) {
                ps.setDouble(8, p.getSalario());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPersona(rs.getInt(1));
                    }
                }
            }
            System.out.println("Persona guardada con éxito con ID: " + p.getIdPersona());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    public boolean actualizar(Persona p) {
        String sql = "UPDATE persona SET id_rol = ?, nombre = ?, apellido_paterno = ?, "
                + "apellido_materno = ?, username = ?, password = ?, salario = ? WHERE id_persona = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getRol().getIdRol());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellidoPaterno());
            ps.setString(4, p.getApellidoMaterno());
            ps.setString(5, p.getUsername());
            ps.setString(6, p.getPassword());
            
            if (p.getSalario()!= null) {
                ps.setDouble(7, p.getSalario());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }
            
            ps.setInt(8, p.getIdPersona());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Persona> listarPorEmpresa(int idEmpresa) {
        List<Persona> lista = new ArrayList<>();
        PermisosDAO permisosDAO = new PermisosDAO();

        String sql = "SELECT p.*, r.nombre_rol FROM persona p "
                + "JOIN rol r ON p.id_rol = r.id_rol WHERE p.id_empresa = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rol rol = new Rol();
                    rol.setIdRol(rs.getInt("id_rol"));
                    rol.setNombreRol(rs.getString("nombre_rol"));

                    Persona p = new Persona();
                    p.setIdPersona(rs.getInt("id_persona"));
                    p.setNombre(rs.getString("nombre"));
                    p.setApellidoPaterno(rs.getString("apellido_paterno"));
                    p.setApellidoMaterno(rs.getString("apellido_materno"));
                    p.setUsername(rs.getString("username"));
                    p.setSalario(rs.getObject("salario") != null ? rs.getDouble("salario") : null);
                    p.setRol(rol);

                    p.setPermisos(permisosDAO.obtenerPermisosPorPersona(p.getIdPersona()));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Persona buscarPorId(int id) {
        Persona p = null;
        PermisosDAO permisosDAO = new PermisosDAO();
        String sql = "SELECT p.*, r.nombre_rol FROM persona p "
                + "JOIN rol r ON p.id_rol = r.id_rol WHERE p.id_persona = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Persona();
                    p.setIdPersona(rs.getInt("id_persona"));
                    p.setNombre(rs.getString("nombre"));
                    p.setApellidoPaterno(rs.getString("apellido_paterno"));
                    p.setApellidoMaterno(rs.getString("apellido_materno"));
                    p.setUsername(rs.getString("username"));
                    p.setSalario(rs.getObject("salario") != null ? rs.getDouble("salario") : null);

                    Rol rol = new Rol();
                    rol.setIdRol(rs.getInt("id_rol"));
                    rol.setNombreRol(rs.getString("nombre_rol"));
                    p.setRol(rol);

                    p.setPermisos(permisosDAO.obtenerPermisosPorPersona(id));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    public Persona autenticar(String user, String pass) {  //me llega la contraseña ya hasheada
        Persona personaEncontrada = null;
        String sql = "SELECT p.*, r.nombre_rol, e.nombre_comercial, e.razon_social, e.rfc, e.telefono "
                + "FROM persona p JOIN rol r ON p.id_rol = r.id_rol "
                + "JOIN empresa e ON p.id_empresa = e.id_empresa "
                + "WHERE p.username = ? AND p.password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol();
                    rol.setIdRol(rs.getInt("id_rol"));
                    rol.setNombreRol(rs.getString("nombre_rol"));

                    Empresa empresa = new Empresa();
                    empresa.setIdEmpresa(rs.getInt("id_empresa"));
                    empresa.setNombreComercial(rs.getString("nombre_comercial"));
                    empresa.setRazonSocial(rs.getString("razon_social"));
                    empresa.setRfc(rs.getString("rfc"));
                    empresa.setTelefono(rs.getString("telefono"));

                    personaEncontrada = new Persona();
                    personaEncontrada.setIdPersona(rs.getInt("id_persona"));
                    personaEncontrada.setRol(rol);
                    personaEncontrada.setEmpresa(empresa);
                    personaEncontrada.setNombre(rs.getString("nombre"));
                    personaEncontrada.setApellidoPaterno(rs.getString("apellido_paterno"));
                    personaEncontrada.setApellidoMaterno(rs.getString("apellido_materno"));
                    personaEncontrada.setUsername(rs.getString("username"));
                    personaEncontrada.setPassword(rs.getString("password"));
                    personaEncontrada.setSalario(rs.getObject("salario") != null ? rs.getDouble("salario") : null);
                    personaEncontrada.setRequiereCambio(rs.getInt("requiere_cambio") == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personaEncontrada;
    }

    public Persona cambiarContrasena(int idPersona, String contrasenaNueva) {
        String sqlUpdate = "UPDATE persona SET password = ?, requiere_cambio = 0 WHERE id_persona = ?";
        String sqlSelect = "SELECT p.*, r.nombre_rol, e.nombre_comercial, e.razon_social "
                + "FROM persona p JOIN rol r ON p.id_rol = r.id_rol "
                + "JOIN empresa e ON p.id_empresa = e.id_empresa WHERE p.id_persona = ?";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, contrasenaNueva);
                psUpdate.setInt(2, idPersona);

                if (psUpdate.executeUpdate() > 0) {
                    try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                        psSelect.setInt(1, idPersona);
                        ResultSet rs = psSelect.executeQuery();

                        if (rs.next()) {
                            Rol rol = new Rol();
                            rol.setIdRol(rs.getInt("id_rol"));
                            rol.setNombreRol(rs.getString("nombre_rol"));

                            Empresa empresa = new Empresa();
                            empresa.setIdEmpresa(rs.getInt("id_empresa"));
                            empresa.setNombreComercial(rs.getString("nombre_comercial"));
                            empresa.setRazonSocial(rs.getString("razon_social"));

                            Persona p = new Persona();
                            p.setIdPersona(rs.getInt("id_persona"));
                            p.setNombre(rs.getString("nombre"));
                            p.setUsername(rs.getString("username"));
                            p.setSalario(rs.getObject("salario") != null ? rs.getDouble("salario") : null);
                            p.setRol(rol);
                            p.setEmpresa(empresa);
                            p.setRequiereCambio(rs.getInt("requiere_cambio") == 1);
                            return p;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean solicitoCambioContraseña(int idPersona) {
        String sql = "UPDATE persona SET requiere_cambio = 1 WHERE id_persona = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Persona buscarPorUsername(String username) {
    Persona p = null;
    PermisosDAO permisosDAO = new PermisosDAO();
    
    // SQL que busca por nombre de usuario sin exponer la contraseña en el WHERE
    String sql = "SELECT p.*, r.nombre_rol, e.nombre_comercial, e.razon_social, e.rfc, e.telefono "
               + "FROM persona p "
               + "JOIN rol r ON p.id_rol = r.id_rol "
               + "JOIN empresa e ON p.id_empresa = e.id_empresa "
               + "WHERE p.username = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                // Construimos el Rol
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));

                // Construimos la Empresa
                Empresa empresa = new Empresa();
                empresa.setIdEmpresa(rs.getInt("id_empresa"));
                empresa.setNombreComercial(rs.getString("nombre_comercial"));
                empresa.setRazonSocial(rs.getString("razon_social"));
                empresa.setRfc(rs.getString("rfc"));
                empresa.setTelefono(rs.getString("telefono"));

                // Construimos la Persona
                p = new Persona();
                p.setIdPersona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidoPaterno(rs.getString("apellido_paterno"));
                p.setApellidoMaterno(rs.getString("apellido_materno"));
                p.setUsername(rs.getString("username"));
                
               
                p.setPassword(rs.getString("password")); 
                
                p.setSalario(rs.getObject("salario") != null ? rs.getDouble("salario") : null);
                p.setRequiereCambio(rs.getInt("requiere_cambio") == 1);
                p.setRol(rol);
                p.setEmpresa(empresa);

                
                p.setPermisos(permisosDAO.obtenerPermisosPorPersona(p.getIdPersona()));
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al buscar usuario por username: " + e.getMessage());
        e.printStackTrace();
    }
    return p;
}
}