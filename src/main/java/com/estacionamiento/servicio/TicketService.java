/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.estacionamiento.dao.TarifaDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.ConfiguracionLocal;
import com.estacionamiento.util.DocumentoUtil;
import com.estacionamiento.util.SessionManager;

/**
 *
 * @author saori
 */
public class TicketService {

    private static final String LLAVE_TICKETS  = "printer.tickets";
    private static final String LLAVE_REPORTES = "printer.reportes";
    private TarifaDAO tarifaDao = new TarifaDAO();

    private String obtenerNombreSede(Registro reg) {
        if (reg.getEspacio() != null && reg.getEspacio().getEstacionamiento() != null
                && reg.getEspacio().getEstacionamiento().getNombre() != null) {
            return reg.getEspacio().getEstacionamiento().getNombre();
        }
        var sede = SessionManager.getInstance().getEstacionamiento();
        return sede != null ? sede.getNombre() : "Estacionamiento";
    }

    public void imprimirEntrada(Registro reg) {
        String nombreSede = obtenerNombreSede(reg);
        String fechaStr = reg.getFechaRegistro() != null
                ? reg.getFechaRegistro().toString()
                : reg.getHoraEntrada() != null ? reg.getHoraEntrada().toString() : "";
        String horaStr = reg.getHoraEntrada() != null ? reg.getHoraEntrada().toString() : "";
        String empleado = (reg.getPersona() != null && reg.getPersona().getNombre() != null)
                ? reg.getPersona().getNombre() : "—";

        TicketEntrada ticket = new TicketEntrada(
            nombreSede,
            reg.getVehiculo().getPlaca(),
            fechaStr,
            horaStr,
            empleado
        );

        String html = convertirEntradaAHtml(ticket);
        String nombreImpresora = ConfiguracionLocal.getImpresora(LLAVE_TICKETS);
        DocumentoUtil.imprimirHTML(html, nombreImpresora, LLAVE_TICKETS);
    }
public void imprimirSalida(Registro reg) {
    // 1. Configuración inicial del ticket
    String nombreSede = obtenerNombreSede(reg);
    String horaEntradaStr = reg.getHoraEntrada() != null ? reg.getHoraEntrada().toString() : "";
    String horaSalidaStr  = reg.getHoraSalida()  != null ? reg.getHoraSalida().toString()  : "";
    String empleado = (reg.getPersona() != null && reg.getPersona().getNombre() != null)
            ? reg.getPersona().getNombre() : "—";

    TicketSalida ticket = new TicketSalida(nombreSede, reg.getVehiculo().getPlaca(),
            horaEntradaStr, horaSalidaStr, empleado);

    // 2. Obtener el total autoritativo (ej. 460)
    double totalFinal = reg.getMonto(); 

    // 3. Recargo por exceso (Espejo de CalculoServicio para obtener los 440 exactos)[cite: 1]
    double cargoExtraReal = 0.0;
    LocalDateTime finPlan = reg.getFecha_fin_plan();
    LocalDateTime ahora = LocalDateTime.now();
    if (finPlan != null && ahora.isAfter(finPlan.plusMinutes(5))) {
        long minutosExcedidos = java.time.Duration.between(finPlan, ahora).toMinutes();
        double precioUnitario = reg.getTarifa() != null ? reg.getTarifa().getPrecio() : 0.0;
        long bloques = minutosExcedidos / 30;
        long resto   = minutosExcedidos % 30;

        for (long i = 0; i < bloques; i++) {
            cargoExtraReal += (i % 2 == 0) ? precioUnitario / 2.0 : precioUnitario;
        }
        if (resto > 0) {
            cargoExtraReal += precioUnitario / 2.0;
        }
    }

    // 4. Monto inicial real (Total - Multa = los 20 con descuento incluido)
    double montoInicialConDescuento = totalFinal - cargoExtraReal;

    // 5. Lógica de Horas y Descuento visual (Como lo hacías antes)
    Tarifa tarifaRegistro = tarifaDao.buscarPorId(reg.getTarifa().getIdTarifa());
    double precioTarifa = tarifaRegistro.getPrecio();
    
    // Horas contratadas originalmente[cite: 2]
    long horasPlan = (reg.getHoraEntrada() != null && reg.getFecha_fin_plan() != null)
            ? Math.max(1, java.time.temporal.ChronoUnit.HOURS.between(reg.getHoraEntrada(), reg.getFecha_fin_plan()))
            : 1;

    boolean usaDias = tarifaRegistro.getUnidadDescuento() != null
            && tarifaRegistro.getUnidadDescuento().getFactorConversionMinutos() == 1440;
    String cantidadStr = usaDias ? (horasPlan / 24) + " Día(s)" : horasPlan + " Hora(s)";

    // Descuento visual: comparamos el (precio * horas) vs el monto inicial con descuento[cite: 2]
    double montoTeoricoSinDesc = precioTarifa * horasPlan;
    
    
    String descuentoStr = "—";
   if (tarifaRegistro.getUnidadDescuento() != null) {
        descuentoStr = tarifaRegistro.getCantidad_descuento() + " " + tarifaRegistro.getUnidadDescuento().getTipoUnidad();
   }

    // 6. Asignación final al objeto Ticket[cite: 2, 3]
    ticket.setMontoBase(montoInicialConDescuento);
    ticket.setCargoExtra(cargoExtraReal);
    ticket.setMontoTotal(totalFinal);
    ticket.setMensajeFinal("¡Gracias por su preferencia!");

    // 7. Impresión[cite: 2]
    String html = convertirSalidaAHtml(ticket, cantidadStr, montoInicialConDescuento, descuentoStr);
    String nombreImpresora = ConfiguracionLocal.getImpresora("printer.tickets");
    DocumentoUtil.imprimirHTML(html, nombreImpresora, "printer.tickets");
}
    public void imprimirPension(Registro reg) {
        String tipoCobro = (reg.getTarifa() != null && reg.getTarifa().getTipoCobro() != null)
                ? reg.getTarifa().getTipoCobro().getNombre() : "Mensual";

        TicketPension ticketPension = new TicketPension(
            obtenerNombreSede(reg),
            reg.getVehiculo() != null ? reg.getVehiculo().getPlaca() : "—",
            reg.getHoraEntrada() != null ? reg.getHoraEntrada().toString() : "",
            reg.getFecha_fin_plan() != null ? reg.getFecha_fin_plan().toString() : "—",
            reg.getPersona() != null ? reg.getPersona().getNombre() + " " + reg.getPersona().getApellidoPaterno() : "—",
            tipoCobro
        );

        ticketPension.prepararDatos(reg);

        Tarifa tarifaRegistro = tarifaDao.buscarPorId(reg.getTarifa().getIdTarifa());

        double montoBase = ticketPension.getMontoBase();
        double descuentoPesos = 0.0;

        if (tarifaRegistro.getUnidadDescuento() != null) {
            ticketPension.setDescuento(tarifaRegistro.getCantidad_descuento() + " " + tarifaRegistro.getUnidadDescuento().getTipoUnidad());
        } else if (tarifaRegistro.getValorDescuento() > 0.0) {
            descuentoPesos = montoBase * tarifaRegistro.getValorDescuento();
            ticketPension.setDescuento(String.format("%.2f", descuentoPesos) + " Pesos");
        }

        double totalFinal = montoBase - descuentoPesos;
        ticketPension.setMontoTotal(totalFinal);

        String html = generarHtmlPension(ticketPension);
        String nombreImpresora = ConfiguracionLocal.getImpresora(LLAVE_TICKETS);
        DocumentoUtil.imprimirHTML(html, nombreImpresora, LLAVE_TICKETS);
    }

    private String generarHtmlPension(TicketPension t) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body{font-family:'Courier New',Courier,monospace;width:280px;text-align:center;margin:0;padding:10px;}");
        html.append(".header{font-weight:bold;font-size:1.2em;margin-bottom:10px;text-transform:uppercase;}");
        html.append(".badge{display:inline-block;background:#1d4ed8;color:white;font-size:0.85em;font-weight:bold;padding:3px 12px;border-radius:4px;margin:6px 0;}");
        html.append(".placa{font-size:1.5em;font-weight:bold;margin:10px 0;border:1px solid #000;display:inline-block;padding:5px 15px;}");
        html.append(".info{text-align:left;margin-bottom:15px;font-size:0.9em;}");
        html.append(".monto{text-align:right;font-size:1.2em;font-weight:bold;border-top:1px dashed #000;padding-top:8px;margin-top:8px;}");
        html.append(".footer{font-size:0.75em;border-top:1px dashed #000;padding-top:10px;margin-top:10px;}");
        html.append("</style></head><body>");
        html.append("<div class='header'>").append(t.getNombreEstacionamiento()).append("</div>");
        html.append("<div class='badge'>PENSION ").append(t.getTipoCobro().toUpperCase()).append("</div>");
        html.append("<div>PLACA:</div><div class='placa'>").append(t.getPlaca()).append("</div>");
        html.append("<div class='info'>");
        html.append("<b>INICIO:</b> ").append(t.getHoraEntrada()).append("<br>");
        html.append("<b>VIGENTE HASTA:</b> ").append(t.getHoraSalida()).append("<br>");
        html.append("<b>ATENDIO:</b> ").append(t.getNombreUsuario()).append("<br>");
        html.append("</div>");
        html.append("<div class='monto'>");
        html.append("<b>MONTO BASE:</b> $").append(String.format("%.2f", t.getMontoBase())).append("<br>");
        if (!t.getDescuento().trim().isEmpty()) {
            String etiquetaDescuento = t.getDescuento().contains("Pesos") ? t.getDescuento() : "$ " + t.getDescuento();
            html.append("<b>DESCUENTO:</b> ").append(etiquetaDescuento).append("<br>");
        }
        html.append("<div class='total'>TOTAL: $").append(String.format("%.2f", t.getMontoTotal())).append("</div>");
        html.append("</div>");
        html.append("<div class='footer'>Conserve este comprobante.<br><b>Gracias por su preferencia</b></div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String convertirEntradaAHtml(TicketEntrada t) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: 'Courier New', Courier, monospace; width: 280px; text-align: center; margin: 0; padding: 10px; }");
        html.append(".header { font-weight: bold; font-size: 1.2em; margin-bottom: 10px; text-transform: uppercase; }");
        html.append(".info { text-align: left; margin-bottom: 15px; font-size: 0.9em; }");
        html.append(".footer { font-size: 0.75em; border-top: 1px dashed #000; padding-top: 10px; margin-top: 10px; }");
        html.append(".placa { font-size: 1.5em; font-weight: bold; margin: 10px 0; border: 1px solid #000; display: inline-block; padding: 5px 15px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='header'>").append(t.getNombreEstacionamiento()).append("</div>");
        html.append("<div class='info'>");
        html.append("<b>FECHA:</b> ").append(t.getFechaEntrada()).append("<br>");
        html.append("<b>HORA:</b> ").append(t.getHoraEntrada()).append("<br>");
        html.append("<b>ATENDIÓ:</b> ").append(t.getNombreUsuario()).append("<br>");
        html.append("</div>");
        html.append("<div>PLACA VEHÍCULO:</div>");
        html.append("<div class='placa'>").append(t.getPlaca()).append("</div>");
        html.append("<div class='footer'>");
        html.append(t.getNotaAdvertencia());
        html.append("<br><br><b>¡CONSERVE ESTE TICKET!</b>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    private String convertirSalidaAHtml(TicketSalida t, String cantidadStr, double montoEntrada, String descuentoStr) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: 'Courier New', Courier, monospace; width: 280px; text-align: center; margin: 0; padding: 10px; }");
        html.append(".header { font-weight: bold; font-size: 1.2em; margin-bottom: 10px; text-transform: uppercase; }");
        html.append(".info { text-align: left; margin-bottom: 15px; font-size: 0.9em; }");
        html.append(".footer { font-size: 0.75em; border-top: 1px dashed #000; padding-top: 10px; margin-top: 10px; text-align: center; }");
        html.append(".placa { font-size: 1.5em; font-weight: bold; margin: 10px 0; border: 1px solid #000; display: inline-block; padding: 5px 15px; }");
        html.append(".monto { text-align: right; font-size: 1.1em; }");
        html.append(".total { font-weight: bold; font-size: 1.3em; border-top: 1px solid #000; padding-top: 5px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='header'>").append(t.getNombreEstacionamiento()).append("</div>");
        html.append("<div>PLACA VEHÍCULO:</div>");
        html.append("<div class='placa'>").append(t.getPlaca()).append("</div>");
        html.append("<div class='info'>");
        html.append("<b>ENTRADA:</b> ").append(t.getHoraEntrada()).append("<br>");
        html.append("<b>SALIDA:</b> ").append(t.getHoraSalida()).append("<br>");
        html.append("<b>ATENDIÓ:</b> ").append(t.getNombreUsuario()).append("<br>");
        html.append("</div>");
        html.append("<div class='monto'>");
        html.append("<b>MONTO BASE:</b> $").append(String.format("%.2f", montoEntrada)).append("<br>");
        html.append("<b>CANTIDAD:</b> ").append(cantidadStr).append("<br>");
        if (t.getCargoExtra() > 0) {
            html.append("<b>CARGO EXTRA:</b> $").append(String.format("%.2f", t.getCargoExtra())).append("<br>");
        }
        html.append("<b>DESCUENTO:</b> ").append(descuentoStr).append("<br>");
        html.append("<div class='total'>TOTAL: $").append(String.format("%.2f", t.getMontoTotal())).append("</div>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append(t.getMensajeFinal());
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
