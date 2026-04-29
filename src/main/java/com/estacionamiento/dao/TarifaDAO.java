package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO {

    private static final String SQL_TARIFA =
        "SELECT t.id_tarifa, t.tipo_cobro, t.precio, t.valor_descuento, " +
        "       est.id_estacionamiento, est.nombre AS nombre_zona, " +
        "       tt.id_tipo_tarifa, tt.nombre AS tipo_tarifa, " +
        "       ud.id_unidad_descuento, ud.nombre AS unidad_descuento " +
        "FROM tarifa t " +
        "JOIN estacionamiento est   ON t.id_estacionamiento  = est.id_estacionamiento " +
        "JOIN tipo_tarifa tt        ON t.id_tipo_tarifa      = tt.id_tipo_tarifa " +
        "JOIN unidad_descuento ud   ON t.id_unidad_descuento = ud.id_unidad_descuento ";

    // Todas las tarifas de una zona
    public List<Tarifa> listarPorEstacionamiento(int idEstacionamiento) {
        String sql = SQL_TARIFA +
            "WHERE t.id_estacionamiento = ? " +
            "ORDER BY t.id_tipo_tarifa ASC";
        return ejecutarLista(sql, ps -> ps.setInt(1, idEstacionamiento));
    }

    // Tarifa normal (por hora) de una zona específica
    public Tarifa buscarTarifaNormal(int idEstacionamiento) {
        String sql = SQL_TARIFA +
            "WHERE t.id_estacionamiento = ? " +
            "  AND t.id_tipo_tarifa     = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.TARIFA_NORMAL);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearTarifa(rs);
            }
        } catch (SQLException e) {
            System.err.println("[TarifaDAO] Error en buscarTarifaNormal: " + e.getMessage());
        }
        return null;
    }

    //Tarifa de pensión (mensual o quincenal) de una zona
    public Tarifa buscarTarifaPension(int idEstacionamiento) {
        String sql = SQL_TARIFA +
            "WHERE t.id_estacionamiento = ? " +
            "  AND t.id_tipo_tarifa     = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.TARIFA_PENSION);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearTarifa(rs);
            }
        } catch (SQLException e) {
            System.err.println("[TarifaDAO] Error en buscarTarifaPension: " + e.getMessage());
        }
        return null;
    }

    public Tarifa buscarPorId(int idTarifa) {
        String sql = SQL_TARIFA +
            "WHERE t.id_tarifa = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarifa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearTarifa(rs);
            }
        } catch (SQLException e) {
            System.err.println("[TarifaDAO] Error en buscarPorId: " + e.getMessage());
        }
        return null;
    }


    @FunctionalInterface
    private interface Parametrizador {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private List<Tarifa> ejecutarLista(String sql, Parametrizador p) {
        List<Tarifa> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            p.aplicar(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearTarifa(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TarifaDAO] Error SQL: " + e.getMessage());
        }
        return lista;
    }

    private Tarifa mapearTarifa(ResultSet rs) throws SQLException {

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

        return tarifa;
    }
}
