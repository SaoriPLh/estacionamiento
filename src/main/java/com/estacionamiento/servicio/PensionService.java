package com.estacionamiento.servicio;

import com.estacionamiento.dao.EspacioDAO;
import com.estacionamiento.dao.PensionDAO;
import com.estacionamiento.dao.RegistroDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.EmailService;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.SessionManager;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PensionService {

    private final PensionDAO pensionDAO = new PensionDAO();
    private final EspacioDAO espacioDAO = new EspacioDAO();
    private final RegistroDAO registroDAO = new RegistroDAO();

    // =========================================================================
    // CREAR
    // =========================================================================

    public Pension crearPension(Cliente cliente, Vehiculo vehiculo, Tarifa tarifa,
                                Espacio espacio, CodigoAcceso codigo, Date fechaFin) {

        if (espacio.getEstadoEspacio().getIdEstadoEspacio() != MisConstantes.ESPACIO_DISPONIBLE) {
            throw new IllegalStateException("El espacio '" + espacio.getCodigo() + "' no está disponible.");
        }

        EstadoPension estadoActiva = new EstadoPension(MisConstantes.PENSION_ACTIVA, "ACTIVO");

        Pension nueva = new Pension();
        nueva.setCliente(cliente);
        nueva.setVehiculo(vehiculo);
        nueva.setTarifa(tarifa);
        nueva.setEspacio(espacio);
        nueva.setEstadoPension(estadoActiva);
        nueva.setCodigo(codigo);
        nueva.setFechaInicio(new Date());
        nueva.setFechaFin(fechaFin);

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            pensionDAO.insertar(nueva, con);
            espacioDAO.actualizarEstado(espacio.getIdEspacio(), MisConstantes.ESPACIO_PENSION, con);

            con.commit();
            return nueva;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error creando pensión: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean actualizarPension(int idPension, Tarifa nuevaTarifa, Date nuevaFechaFin) {
        Pension p = pensionDAO.buscarPorId(idPension);
        if (p == null) throw new IllegalArgumentException("Pensión no encontrada: " + idPension);
        if (p.getEstadoPension().getIdEstadoPension() != MisConstantes.PENSION_ACTIVA) {
            throw new IllegalStateException("Solo se pueden modificar pensiones activas.");
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            pensionDAO.actualizarTarifa(idPension, nuevaTarifa.getIdTarifa(), con);
            if (nuevaFechaFin != null) {
                pensionDAO.actualizarFechaFin(idPension, nuevaFechaFin, con);
            }

            con.commit();
            return true;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error actualizando pensión: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    // =========================================================================
    // RENOVAR
    // =========================================================================

    /**
     * Cierra el registro activo del periodo anterior, extiende la fecha_fin
     * de la pensión y crea un nuevo registro para el periodo renovado.
     */
    public boolean renovarPension(int idPension, Date nuevaFechaFin) {
        Pension p = pensionDAO.buscarPorId(idPension);
        if (p == null) throw new IllegalArgumentException("Pensión no encontrada: " + idPension);
        if (p.getEstadoPension().getIdEstadoPension() != MisConstantes.PENSION_ACTIVA) {
            throw new IllegalStateException("Solo se pueden renovar pensiones activas.");
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Cerrar el registro activo del periodo anterior con hora_salida = ahora
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            registroDAO.cerrarRegistroActivoPorEspacio(p.getEspacio().getIdEspacio(), ahora, con);

            // 2. Actualizar fecha_fin de la pension
            pensionDAO.actualizarFechaFin(idPension, nuevaFechaFin, con);

            // 3. Crear nuevo registro para el periodo renovado
            Registro nuevo = construirRegistroRenovacion(p, nuevaFechaFin);
            registroDAO.insertar(nuevo, con);

            con.commit();
            return true;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error renovando pensión: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    private Registro construirRegistroRenovacion(Pension p, Date nuevaFechaFin) {
        LocalDateTime ahora = LocalDateTime.now();

        Persona empleado = SessionManager.getInstance().getUsuario();
        if (empleado == null) {
            empleado = new Persona();
            empleado.setIdPersona(1);
        }

        EstadoRegistro estadoActivo = new EstadoRegistro();
        estadoActivo.setIdEstadoRegistro(MisConstantes.REGISTRO_ACTIVO);

        Registro r = new Registro();
        r.setVehiculo(p.getVehiculo());
        r.setEspacio(p.getEspacio());
        r.setTarifa(p.getTarifa());
        r.setPersona(empleado);
        r.setFechaRegistro(ahora);
        r.setHoraEntrada(ahora);
        r.setMonto(p.getTarifa().getPrecio());
        r.setFecha_fin_plan(nuevaFechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        r.setEstadoRegistro(estadoActivo);
        return r;
    }

    // =========================================================================
    // SUSPENDER
    // =========================================================================

    public boolean suspenderPension(int idPension) {
        Pension p = pensionDAO.buscarPorId(idPension);
        if (p == null) throw new IllegalArgumentException("Pensión no encontrada: " + idPension);
        if (p.getEstadoPension().getIdEstadoPension() != MisConstantes.PENSION_ACTIVA) {
            throw new IllegalStateException("Solo se pueden suspender pensiones activas.");
        }
        return pensionDAO.actualizarEstado(idPension, MisConstantes.PENSION_SUSPENDIDA);
    }

    // =========================================================================
    // CANCELAR
    // =========================================================================

    public boolean cancelarPension(int idPension) {
        Pension p = pensionDAO.buscarPorId(idPension);
        if (p == null) throw new IllegalArgumentException("Pensión no encontrada: " + idPension);
        if (p.getEstadoPension().getIdEstadoPension() == MisConstantes.PENSION_VENCIDA) {
            throw new IllegalStateException("La pensión ya está vencida.");
        }
        return ejecutarCancelacion(p, new Timestamp(System.currentTimeMillis()));
    }

    // =========================================================================
    // VENCIMIENTOS AUTOMÁTICOS
    // =========================================================================

    public int verificarVencimientos() {
        List<Pension> vencidas = pensionDAO.listarVencidas();
        int procesadas = 0;

        for (Pension p : vencidas) {
            try {
                // hora_salida = fecha_fin del plan, no la hora actual
                Timestamp horaSalida = p.getFechaFin() != null
                        ? new Timestamp(p.getFechaFin().getTime())
                        : new Timestamp(System.currentTimeMillis());
                ejecutarCancelacion(p, horaSalida);
                procesadas++;
                enviarEmailVencimiento(p);
            } catch (Exception e) {
                System.err.println("[PensionService] Error al vencer pensión id="
                        + p.getIdPension() + ": " + e.getMessage());
            }
        }

        if (procesadas > 0) {
            System.out.println("[PensionService] " + procesadas + " pensión(es) vencida(s) procesada(s).");
        }
        return procesadas;
    }

    private boolean ejecutarCancelacion(Pension p, Timestamp horaSalida) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            pensionDAO.actualizarEstado(p.getIdPension(), MisConstantes.PENSION_VENCIDA, con);
            espacioDAO.actualizarEstado(p.getEspacio().getIdEspacio(), MisConstantes.ESPACIO_DISPONIBLE, con);
            registroDAO.cerrarRegistroActivoPorEspacio(p.getEspacio().getIdEspacio(), horaSalida, con);

            con.commit();
            return true;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error cancelando pensión: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    // =========================================================================
    // CONSULTAS
    // =========================================================================

    public void enviarAvisosProximos(int diasAnticipacion) {
        List<Pension> proximas = pensionDAO.listarProximasAVencer(diasAnticipacion);
        for (Pension p : proximas) {
            try {
                enviarEmailVencimiento(p);
            } catch (Exception e) {
                System.err.println("[PensionService] Error enviando aviso id=" + p.getIdPension() + ": " + e.getMessage());
            }
        }
    }

    private void enviarEmailVencimiento(Pension p) {
        String placa = p.getVehiculo() != null ? p.getVehiculo().getPlaca() : "—";
        String fechaFin = p.getFechaFin() != null ? p.getFechaFin().toString() : "—";
        if (p.getCliente() != null) {
            String nombre = p.getCliente().getNombre() + " " + p.getCliente().getApellidoPaterno();
            String correo = p.getCliente().getCorreo();
            if (correo != null && !correo.isBlank()) {
                EmailService.notificarVencimientoCliente(correo, nombre, placa, fechaFin);
            }
        }
    }

    public Pension buscarPorId(int idPension) {
        return pensionDAO.buscarPorId(idPension);
    }

    public Pension buscarActivaPorEspacio(int idEspacio) {
        return pensionDAO.buscarActivaPorEspacio(idEspacio);
    }

    public List<Pension> listarPorCliente(int idCliente) {
        return pensionDAO.listarPorCliente(idCliente);
    }

    public List<Pension> listarActivas(int idEstacionamiento) {
        return pensionDAO.listarActivas(idEstacionamiento);
    }

    public List<Pension> listarPorEstacionamiento(int idEstacionamiento) {
        return pensionDAO.listarPorEstacionamiento(idEstacionamiento);
    }
}
