package com.estacionamiento.servicio;

import com.estacionamiento.dao.ClienteDAO;
import com.estacionamiento.dao.CodigoAccesoDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.MisConstantes;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CodigoAccesoService {

    private final CodigoAccesoDAO codigoDAO = new CodigoAccesoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // ================= ASIGNAR =================
    public CodigoAcceso asignarCodigo(int idCliente, int idEstacionamiento,
                                      Date fechaInicio, Date fechaFin) {

        if (fechaFin == null || !fechaFin.after(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        try {
            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);

            if (activo != null) {
                throw new IllegalStateException(
                        "El cliente ya tiene un código ACTIVO (id: " + activo.getIdCodigo() + ")");
            }

            CodigoAcceso nuevo = construirCodigo(idCliente, idEstacionamiento, fechaInicio, fechaFin);
            codigoDAO.insertar(nuevo);

            return nuevo;

        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar código", e);
        }
    }

    

    // ================= VALIDAR =================
   public boolean validarAcceso(String codigoStr, int idEstacionamiento) {
    try {
        CodigoAcceso codigo = codigoDAO.buscarPorCodigo(codigoStr);

        if (codigo == null) return false;

        // validar estacionamiento
        if (codigo.getEstacionamiento().getIdEstacionamiento() != idEstacionamiento) {
            return false;
        }

        int estado = codigo.getEstadoCodigo().getIdEstadoCodigo();
        if (estado != MisConstantes.CODIGO_ACTIVO) return false;

        boolean vigente = codigo.getFechaFin() != null
                && !new Date().after(codigo.getFechaFin());

        if (!vigente) {
            codigoDAO.actualizarEstado(
                    codigo.getIdCodigo(),
                    MisConstantes.CODIGO_VENCIDO
            );
        }

        return vigente;

    } catch (Exception e) {
        throw new RuntimeException("Error al validar acceso", e);
    }
}
    // ================= CANCELAR POR CLIENTE =================
    public boolean cancelarCodigo(int idCliente) {
        try {
            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);

            if (activo == null) {
                throw new IllegalStateException("No hay código activo");
            }

            validarCancelacion(activo);

            return codigoDAO.actualizarEstado(
                    activo.getIdCodigo(),
                    MisConstantes.CODIGO_CANCELADO
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar código", e);
        }
    }

    // ================= CANCELAR POR ID =================
    public boolean cancelarCodigoPorId(int idCodigo) {
        try {
            CodigoAcceso codigo = codigoDAO.buscarPorId(idCodigo);

            if (codigo == null) {
                throw new IllegalStateException("Código no encontrado");
            }

            validarCancelacion(codigo);

            return codigoDAO.actualizarEstado(
                    idCodigo,
                    MisConstantes.CODIGO_CANCELADO
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar código por ID", e);
        }
    }

    // ================= CAMBIAR ESTADO =================
    public boolean cambiarEstado(int idCodigo, int nuevoEstado) {
        try {
            CodigoAcceso codigo = codigoDAO.buscarPorId(idCodigo);

            if (codigo == null) {
                throw new IllegalStateException("Código no encontrado");
            }

            int estadoActual = codigo.getEstadoCodigo().getIdEstadoCodigo();

            if (estadoActual == MisConstantes.CODIGO_CANCELADO
                    && nuevoEstado != MisConstantes.CODIGO_ACTIVO) {
                throw new IllegalStateException("Código cancelado solo puede activarse");
            }

            return codigoDAO.actualizarEstado(idCodigo, nuevoEstado);

        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado", e);
        }
    }

    // ================= CONSULTAS =================
    public CodigoAcceso obtenerCodigoActivo(int idCliente) {
        try {
            return codigoDAO.buscarActivoPorIdCliente(idCliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener código activo", e);
        }
    }

    public List<CodigoAcceso> obtenerHistorial(int idCliente) {
        try {
            return codigoDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener historial", e);
        }
    }

    public boolean esCodigoVigente(int idCliente) {
        try {
            CodigoAcceso activo = codigoDAO.buscarActivoPorIdCliente(idCliente);

            if (activo == null) return false;

            boolean vigente = activo.getFechaFin() != null
                    && !new Date().after(activo.getFechaFin());

            if (!vigente) {
                codigoDAO.actualizarEstado(
                        activo.getIdCodigo(),
                        MisConstantes.CODIGO_VENCIDO
                );
            }

            return vigente;

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar vigencia", e);
        }
    }

    // ================= CONSTRUIR =================
    private CodigoAcceso construirCodigo(int idCliente, int idEstacionamiento,
                                         Date fechaInicio, Date fechaFin) {

        String codigoStr = "LOGIC-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        CodigoAcceso codigo = new CodigoAcceso();

        try {
            Cliente cliente = clienteDAO.buscarPorId(idCliente);

            if (cliente == null) {
                throw new IllegalStateException("Cliente no existe");
            }

            codigo.setCliente(cliente);

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo cliente", e);
        }

        Estacionamiento est = new Estacionamiento();
        est.setIdEstacionamiento(idEstacionamiento);

        codigo.setEstacionamiento(est);
        codigo.setEstadoCodigo(new EstadoCodigoAcceso(MisConstantes.CODIGO_ACTIVO, "Activo"));
        codigo.setCodigo(codigoStr);
        codigo.setFechaInicio(fechaInicio);
        codigo.setFechaFin(fechaFin);

        return codigo;
    }

    // ================= VALIDACION =================
    private void validarCancelacion(CodigoAcceso codigo) {
        int estado = codigo.getEstadoCodigo().getIdEstadoCodigo();

        if (estado == MisConstantes.CODIGO_CANCELADO) {
            throw new IllegalStateException("Ya está cancelado");
        }

        if (estado == MisConstantes.CODIGO_VENCIDO) {
            throw new IllegalStateException("Ya está vencido");
        }
    }
}