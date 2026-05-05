package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspacioDAO {

    public Espacio insertarEspacio(Espacio espacio) {

        String sql = "INSERT INTO espacio (id_estacionamiento, id_tipo_espacio, id_estado_espacio, codigo) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, espacio.getEstacionamiento().getIdEstacionamiento());
            ps.setInt(2, espacio.getTipoEspacio().getIdTipoEspacio());
            ps.setInt(3, espacio.getEstadoEspacio().getIdEstadoEspacio());
            ps.setString(4, espacio.getCodigo());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        espacio.setIdEspacio(idGenerado);
                    }
                }
                System.out.println("Espacio guardado con éxito con ID: " + espacio.getIdEspacio());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return espacio;
    }


    public List<Espacio> espaciosEstacionamiento(int idEstacionamiento) {
        List<Espacio> lista = new ArrayList<>();

        String sql = "SELECT e.id_espacio, e.codigo, " +
                     "t.id_tipo_espacio, t.descripcion AS tipo_desc, " +
                     "s.id_estado_espacio, s.descripcion AS estado_desc " +
                     "FROM espacio e " +
                     "INNER JOIN tipo_espacio t ON e.id_tipo_espacio = t.id_tipo_espacio " +
                     "INNER JOIN estado_espacio s ON e.id_estado_espacio = s.id_estado_espacio " +
                     "WHERE e.id_estacionamiento = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    TipoEspacio tipo = new TipoEspacio(
                            rs.getInt("id_tipo_espacio"),
                            rs.getString("tipo_desc")
                    );

                    EstadoEspacio estado = new EstadoEspacio(
                            rs.getInt("id_estado_espacio"),
                            rs.getString("estado_desc")
                    );
                                        
                    Estacionamiento sede = new Estacionamiento();
                    sede.setIdEstacionamiento(idEstacionamiento);   


                    Espacio espacio = new Espacio();
                    espacio.setIdEspacio(rs.getInt("id_espacio"));
                    espacio.setCodigo(rs.getString("codigo"));
                    espacio.setTipoEspacio(tipo);
                    espacio.setEstadoEspacio(estado);
                    espacio.setEstacionamiento(sede);
                    lista.add(espacio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public boolean eliminar(int idEspacio) {
        String sql = "DELETE FROM espacio WHERE id_espacio = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean actualizarEstado(int idEspacio, int idEstado) {
        String sql = "UPDATE espacio SET id_estado_espacio = ? WHERE id_espacio = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setInt(2, idEspacio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean actualizarEstado(int idEspacio, int idEstado, Connection con) throws SQLException {
        String sql = "UPDATE espacio SET id_estado_espacio = ? WHERE id_espacio = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setInt(2, idEspacio);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean actualizarTipoEspacio(int idEspacio, int idTipoEspacio) {

        String sql = "UPDATE espacio SET id_tipo_espacio = ? WHERE id_espacio = ? ";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipoEspacio); // 🔥 corregido (tenías invertido)
            ps.setInt(2, idEspacio);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Espacio> listarEspaciosDisponibles(int idEstacionamiento) {
        List<Espacio> lista = new ArrayList<>();

        String sql = "SELECT e.id_espacio, e.codigo, " +
                     "t.id_tipo_espacio, t.descripcion AS tipo_desc, " +
                     "s.id_estado_espacio, s.descripcion AS estado_desc " +
                     "FROM espacio e " +
                     "INNER JOIN tipo_espacio t ON e.id_tipo_espacio = t.id_tipo_espacio " +
                     "INNER JOIN estado_espacio s ON e.id_estado_espacio = s.id_estado_espacio " +
                     "WHERE e.id_estacionamiento = ? AND e.id_estado_espacio = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.ESPACIO_DISPONIBLE);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    TipoEspacio tipo = new TipoEspacio(
                            rs.getInt("id_tipo_espacio"),
                            rs.getString("tipo_desc")
                    );

                    EstadoEspacio estado = new EstadoEspacio(
                            rs.getInt("id_estado_espacio"),
                            rs.getString("estado_desc")
                    );

                    Espacio espacio = new Espacio();
                    espacio.setIdEspacio(rs.getInt("id_espacio"));
                    espacio.setCodigo(rs.getString("codigo"));
                    espacio.setTipoEspacio(tipo);
                    espacio.setEstadoEspacio(estado);

                    lista.add(espacio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public List<Espacio> listarEspaciosOcupados(int idEstacionamiento) {
        List<Espacio> lista = new ArrayList<>();

        String sql = "SELECT e.id_espacio, e.codigo, " +
                     "t.id_tipo_espacio, t.descripcion AS tipo_desc, " +
                     "s.id_estado_espacio, s.descripcion AS estado_desc " +
                     "FROM espacio e " +
                     "INNER JOIN tipo_espacio t ON e.id_tipo_espacio = t.id_tipo_espacio " +
                     "INNER JOIN estado_espacio s ON e.id_estado_espacio = s.id_estado_espacio " +
                     "WHERE e.id_estacionamiento = ? AND (e.id_estado_espacio = ? OR e.id_estado_espacio = ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.ESPACIO_OCUPADO);
            ps.setInt(3, MisConstantes.ESPACIO_RESERVADO);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    TipoEspacio tipo = new TipoEspacio(
                            rs.getInt("id_tipo_espacio"),
                            rs.getString("tipo_desc")
                    );

                    EstadoEspacio estado = new EstadoEspacio(
                            rs.getInt("id_estado_espacio"),
                            rs.getString("estado_desc")
                    );

                    Espacio espacio = new Espacio();
                    espacio.setIdEspacio(rs.getInt("id_espacio"));
                    espacio.setCodigo(rs.getString("codigo"));
                    espacio.setTipoEspacio(tipo);
                    espacio.setEstadoEspacio(estado);

                    lista.add(espacio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public Espacio buscarPorId(int idEspacio) {

        String sql = "SELECT e.id_espacio, e.codigo, " +
                     "s.id_estado_espacio, s.descripcion AS estado_desc, " +
                     "t.id_tipo_espacio, t.descripcion AS tipo_desc " +
                     "FROM espacio e " +
                     "INNER JOIN estado_espacio s ON e.id_estado_espacio = s.id_estado_espacio " +
                     "INNER JOIN tipo_espacio t ON e.id_tipo_espacio = t.id_tipo_espacio " +
                     "WHERE e.id_espacio = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    EstadoEspacio estado = new EstadoEspacio(
                            rs.getInt("id_estado_espacio"),
                            rs.getString("estado_desc")
                    );

                    TipoEspacio tipo = new TipoEspacio(
                            rs.getInt("id_tipo_espacio"),
                            rs.getString("tipo_desc")
                    );

                    Espacio espacio = new Espacio();
                    espacio.setIdEspacio(rs.getInt("id_espacio"));
                    espacio.setCodigo(rs.getString("codigo"));
                    espacio.setEstadoEspacio(estado);
                    espacio.setTipoEspacio(tipo);

                    return espacio;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}