package com.estacionamiento.servicio;

import com.estacionamiento.dao.CodigoAccesoDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MisConstantes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CodigoAccesoService {

    private final CodigoAccesoDAO codigoDAO = new CodigoAccesoDAO();

    public CodigoAcceso asignarCodigo(int idCliente, int idEstacionamiento,
                                      Date fechaInicio, Date fechaFin) {
       
        if (fechaFin == null || !fechaFin.after(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            
            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);
            if (activo != null) {
                throw new IllegalStateException(
                        "El cliente ya tiene un código ACTIVO (id: " + activo.getIdCodigo() + "). " +
                        "Cancélelo primero antes de asignar uno nuevo.");
            }

            CodigoAcceso nuevo = construirCodigo(idCliente, idEstacionamiento, fechaInicio, fechaFin);
            codigoDAO.insertar(nuevo, con);

            con.commit();
            return nuevo;

        } catch (IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al asignar código: " + e.getMessage(), e);
        } finally {
            cerrarConexion(con);
        }
    }

    public boolean validarAcceso(String codigoStr) {
        try {
            CodigoAcceso codigo = codigoDAO.buscarPorCodigo(codigoStr);

            if (codigo == null) return false;

            int estado = codigo.getEstadoCodigo().getIdEstadoCodigo();

            
            if (estado == MisConstantes.CODIGO_CANCELADO) return false;

            
            if (estado != MisConstantes.CODIGO_ACTIVO) return false;

            boolean vigente = codigo.getFechaFin() != null
                    && !new Date().after(codigo.getFechaFin());

           
            if (!vigente) {
                codigoDAO.actualizarEstado(codigo.getIdCodigo(), MisConstantes.CODIGO_VENCIDO);
            }

            return vigente;

        } catch (SQLException e) {
            throw new RuntimeException("Error al validar acceso: " + e.getMessage(), e);
        }
    }

    
    public void cancelarCodigo(int idCliente) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);

            if (activo == null) {
                throw new IllegalStateException(
                        "El cliente no tiene ningún código ACTIVO para cancelar.");
            }

            int estado = activo.getEstadoCodigo().getIdEstadoCodigo();

            if (estado == MisConstantes.CODIGO_CANCELADO) {
                throw new IllegalStateException("El código ya se encuentra CANCELADO.");
            }
            if (estado == MisConstantes.CODIGO_VENCIDO) {
                throw new IllegalStateException(
                        "El código ya está VENCIDO. Solo se cancelan códigos activos o suspendidos.");
            }

            codigoDAO.actualizarEstado(activo.getIdCodigo(), MisConstantes.CODIGO_CANCELADO, con);
            con.commit();

        } catch (IllegalStateException e) {
            throw e;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al cancelar código: " + e.getMessage(), e);
        } finally {
            cerrarConexion(con);
        }
    }


    public void cancelarCodigoPorId(int idCodigo) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            CodigoAcceso codigo = codigoDAO.buscarPorId(idCodigo);

            if (codigo == null) {
                throw new IllegalStateException("No existe un código con id: " + idCodigo);
            }

            int estado = codigo.getEstadoCodigo().getIdEstadoCodigo();

            if (estado == MisConstantes.CODIGO_CANCELADO) {
                throw new IllegalStateException("El código ya se encuentra CANCELADO.");
            }
            if (estado == MisConstantes.CODIGO_VENCIDO) {
                throw new IllegalStateException(
                        "El código ya está VENCIDO. Solo se cancelan códigos activos o suspendidos.");
            }

            codigoDAO.actualizarEstado(idCodigo, MisConstantes.CODIGO_CANCELADO, con);
            con.commit();

        } catch (IllegalStateException e) {
            throw e;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al cancelar código: " + e.getMessage(), e);
        } finally {
            cerrarConexion(con);
        }
    }

    public boolean cambiarEstado(int idCodigo, int nuevoEstado) {
        try {
            CodigoAcceso codigo = codigoDAO.buscarPorId(idCodigo);

            if (codigo == null) {
                throw new IllegalStateException("No existe un código con id: " + idCodigo);
            }

            int estadoActual = codigo.getEstadoCodigo().getIdEstadoCodigo();

            if (estadoActual == MisConstantes.CODIGO_CANCELADO
                    && nuevoEstado != MisConstantes.CODIGO_ACTIVO) {
                throw new IllegalStateException(
                        "Un código CANCELADO solo puede reactivarse a ACTIVO por un administrador.");
            }

            return codigoDAO.actualizarEstado(idCodigo, nuevoEstado);

        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }

    
    public CodigoAcceso obtenerCodigoActivo(int idCliente) {
        try {
            return codigoDAO.buscarActivoPorIdCliente(idCliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener código activo: " + e.getMessage(), e);
        }
    }

  
    public List<CodigoAcceso> obtenerHistorial(int idCliente) {
        try {
            return codigoDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener historial: " + e.getMessage(), e);
        }
    }

    
    public boolean esCodigoVigente(int idCliente) {
        try {
            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);

            if (activo == null) return false;

            if (activo.getEstadoCodigo().getIdEstadoCodigo() == MisConstantes.CODIGO_CANCELADO) {
                return false;
            }

            boolean vigente = activo.getFechaFin() != null
                    && !new Date().after(activo.getFechaFin());

            if (!vigente && activo.getEstadoCodigo().getIdEstadoCodigo() == MisConstantes.CODIGO_ACTIVO) {
                codigoDAO.actualizarEstado(activo.getIdCodigo(), MisConstantes.CODIGO_VENCIDO);
            }

            return vigente;

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar vigencia: " + e.getMessage(), e);
        }
    }

 
    private CodigoAcceso construirCodigo(int idCliente, int idEstacionamiento,
                                         Date fechaInicio, Date fechaFin) {
        String codigoStr = "LOGIC-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        CodigoAcceso codigo = new CodigoAcceso();

        Cliente clienteRef = new Cliente();
        clienteRef.setIdCliente(idCliente);
        codigo.setCliente(clienteRef);

        Estacionamiento est = new Estacionamiento();
        est.setIdEstacionamiento(idEstacionamiento);
        codigo.setEstacionamiento(est);

        codigo.setEstadoCodigo(new EstadoCodigoAcceso(MisConstantes.CODIGO_ACTIVO, "Activo"));
        codigo.setCodigo(codigoStr);
        codigo.setFechaInicio(fechaInicio);
        codigo.setFechaFin(fechaFin);

        return codigo;
    }

  
    private void cerrarConexion(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}