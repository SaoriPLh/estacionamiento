package com.estacionamiento.dao;

import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO {

    public Tarifa insertarTarifa(Tarifa tarifa) {
        String sql = "INSERT INTO tarifa (id_estacionamiento, id_tipo_tarifa, id_unidad_descuento, id_tipo_cobro, precio, valor_descuento, cantidad_descuento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tarifa.getEstacionamiento().getIdEstacionamiento());
            ps.setInt(2, tarifa.getTipoTarifa().getIdTipoTarifa());

        
            if (tarifa.getUnidadDescuento() != null &&
                tarifa.getUnidadDescuento().getIdUnidadDescuento() > 0) {
                ps.setInt(3, tarifa.getUnidadDescuento().getIdUnidadDescuento());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

          
            ps.setInt(4, tarifa.getTipoCobro().getIdTipoCobro());

           
            ps.setDouble(5, tarifa.getPrecio());

            // 6 valor descuento (puedes dejarlo en 0 si no usas nulls)
            if (tarifa.getValorDescuento() != null) {
                ps.setDouble(6, tarifa.getValorDescuento());
            } else {
                ps.setNull(6, Types.DOUBLE);
            }

            // 7 cantidad descuento
            ps.setInt(7, tarifa.getCantidad_descuento());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        tarifa.setIdTarifa(rs.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tarifa;
    }
    // NUEVO MÉTODO: Crucial para tu lógica de convenios dinámicos
    public Tarifa obtenerTarifaNormalPorSede(int idEstacionamiento) {
        Tarifa t = null;
        String sql = "SELECT t.*, tc.nombre_cobro, tt.descripcion, " +
                     "ud.tipo_unidad, ud.factor_conversion_minutos " +
                     "FROM tarifa t " +
                     "INNER JOIN tipo_cobro tc ON t.id_tipo_cobro = tc.id_tipo_cobro " +
                     "INNER JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
                     "LEFT JOIN unidad_descuento ud ON t.id_unidad_descuento = ud.id_unidad_descuento " +
                     "WHERE t.id_estacionamiento = ? AND t.id_tipo_tarifa = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            ps.setInt(2, MisConstantes.TARIFA_NORMAL); // Busca específicamente la normal

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = mapearTarifa(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }
    public boolean actualizar(Tarifa tarifa) {
        String sql = "UPDATE tarifa SET " +
                     "id_estacionamiento = ?, " +
                     "id_tipo_tarifa = ?, " +
                     "id_unidad_descuento = ?, " +
                     "id_tipo_cobro = ?, " +
                     "precio = ?, " +
                     "valor_descuento = ?, " +
                     "cantidad_descuento = ? " +
                     "WHERE id_tarifa = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tarifa.getEstacionamiento().getIdEstacionamiento());
            ps.setInt(2, tarifa.getTipoTarifa().getIdTipoTarifa());

            // 3 unidad descuento
            if (tarifa.getUnidadDescuento() != null &&
                tarifa.getUnidadDescuento().getIdUnidadDescuento() > 0) {
                ps.setInt(3, tarifa.getUnidadDescuento().getIdUnidadDescuento());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            // 4 tipo cobro
            ps.setInt(4, tarifa.getTipoCobro().getIdTipoCobro());

            // 5 precio
            ps.setDouble(5, tarifa.getPrecio());

            // 6 valor descuento
            if (tarifa.getValorDescuento() != null) {
                ps.setDouble(6, tarifa.getValorDescuento());
            } else {
                ps.setNull(6, Types.DOUBLE);
            }

            // 7 cantidad descuento
            ps.setInt(7, tarifa.getCantidad_descuento());

            // 8 id
            ps.setInt(8, tarifa.getIdTarifa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Tarifa> listarPorEstacionamiento(int idEstacionamiento) {
        List<Tarifa> lista = new ArrayList<>();

        String sql = "SELECT t.*, tc.nombre_cobro, tt.descripcion, " +
                     "ud.tipo_unidad, ud.factor_conversion_minutos " +
                     "FROM tarifa t " +
                     "INNER JOIN tipo_cobro tc ON t.id_tipo_cobro = tc.id_tipo_cobro " +
                     "INNER JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
                     "LEFT JOIN unidad_descuento ud ON t.id_unidad_descuento = ud.id_unidad_descuento " +
                     "WHERE t.id_estacionamiento = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstacionamiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTarifa(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean eliminar(int idTarifa) {
        String sql = "DELETE FROM tarifa WHERE id_tarifa = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarifa);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Tarifa buscarPorId(int idTarifa) {
        Tarifa t = null;

        String sql = "SELECT t.*, tc.nombre_cobro, tt.descripcion, " +
                     "ud.tipo_unidad, ud.factor_conversion_minutos " +
                     "FROM tarifa t " +
                     "INNER JOIN tipo_cobro tc ON t.id_tipo_cobro = tc.id_tipo_cobro " +
                     "INNER JOIN tipo_tarifa tt ON t.id_tipo_tarifa = tt.id_tipo_tarifa " +
                     "LEFT JOIN unidad_descuento ud ON t.id_unidad_descuento = ud.id_unidad_descuento " +
                     "WHERE t.id_tarifa = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarifa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                t = new Tarifa();
                t.setIdTarifa(rs.getInt("id_tarifa"));
                t.setPrecio(rs.getDouble("precio"));
                t.setValorDescuento(rs.getDouble("valor_descuento"));
                t.setCantidad_descuento(rs.getInt("cantidad_descuento"));

                TipoCobro tc = new TipoCobro(
                        rs.getInt("id_tipo_cobro"),
                        rs.getString("nombre_cobro"));
                t.setTipoCobro(tc);

                TipoTarifa tt = new TipoTarifa();
                tt.setIdTipoTarifa(rs.getInt("id_tipo_tarifa"));
                tt.setDescripcion(rs.getString("descripcion"));
                t.setTipoTarifa(tt);

                int idUnidad = rs.getInt("id_unidad_descuento");
                if (!rs.wasNull()) {
                    UnidadDescuento ud = new UnidadDescuento(
                            idUnidad,
                            rs.getString("tipo_unidad"),
                            rs.getInt("factor_conversion_minutos"));
                    t.setUnidadDescuento(ud);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return t;
    }
    // Método auxiliar para mantener el estilo limpio
    private Tarifa mapearTarifa(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setPrecio(rs.getDouble("precio"));
        t.setValorDescuento(rs.getDouble("valor_descuento"));
        t.setCantidad_descuento(rs.getInt("cantidad_descuento"));

        TipoCobro tc = new TipoCobro(rs.getInt("id_tipo_cobro"), rs.getString("nombre_cobro"));
        t.setTipoCobro(tc);

        TipoTarifa tt = new TipoTarifa();
        tt.setIdTipoTarifa(rs.getInt("id_tipo_tarifa"));
        tt.setDescripcion(rs.getString("descripcion"));
        t.setTipoTarifa(tt);
        
        int idUnidad = rs.getInt("id_unidad_descuento");
        if (!rs.wasNull()) {
            UnidadDescuento ud = new UnidadDescuento();
            ud.setIdUnidadDescuento(idUnidad);
            ud.setTipoUnidad(rs.getString("tipo_unidad"));
            ud.setFactorConversionMinutos(rs.getInt("factor_conversion_minutos"));
            t.setUnidadDescuento(ud);
        }

        return t;
    }
}