/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estacionamiento.servicio;

import com.estacionamiento.dao.TarifaDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.util.ConfiguracionLocal;
import com.estacionamiento.util.DocumentoUtil;

/**
 *
 * @author saori
 */
public class TicketService {

    
    private static final String LLAVE_IMPRESORA = "ticketera";
    private TarifaDAO tarifaDao =  new TarifaDAO();

    public void imprimirEntrada(Registro reg) {
        //  Creamos el ticket con todos los atributos que necesita el html
        TicketEntrada ticket = new TicketEntrada(
            reg.getEspacio().getEstacionamiento().getNombre(),
            reg.getVehiculo().getPlaca(),
            reg.getFechaRegistro().toString(),   // fecha del registro
            reg.getHoraEntrada().toString(),    // hora del registro
            reg.getPersona().getNombre() 
        );

        // Generamos el html y lo enviamos a la impresora
        String html = convertirEntradaAHtml(ticket);
        String nombreImpresora = ConfiguracionLocal.getImpresora(LLAVE_IMPRESORA);
        DocumentoUtil.imprimirHTML(html, nombreImpresora, LLAVE_IMPRESORA);
    }

    public void imprimirSalida(Registro reg) {
        //  Creamos el ticket con todos los atributos que necesita el html
        TicketSalida ticket = new TicketSalida(
            reg.getEspacio().getEstacionamiento().getNombre(),
            reg.getVehiculo().getPlaca(),
            reg.getHoraEntrada().toString(),    // hora en que entró el vehículo
            reg.getHoraSalida().toString(),     // hora en que sale
            reg.getPersona().getNombre() // quien atendió
        );

        //  Llenamos los montos usando el método ya existente en TicketSalida
        ticket.prepararDatos(reg);
       // Después de ticket.prepararDatos(reg, totalCalculado):

Tarifa tarifaRegistro = tarifaDao.buscarPorId(reg.getTarifa().getIdTarifa());



if (tarifaRegistro.getUnidadDescuento()!= null) {
    
    ticket.setDescuento(tarifaRegistro.getCantidad_descuento()+" "+tarifaRegistro.getUnidadDescuento().getTipoUnidad());
} else if(tarifaRegistro.getValorDescuento()> 0.0){
    ticket.setDescuento(tarifaRegistro.getCantidad_descuento()+" Pesos");
}


        // Generamos el html y lo enviamos a la impresora
        String html = convertirSalidaAHtml(ticket);
        String nombreImpresora = ConfiguracionLocal.getImpresora(LLAVE_IMPRESORA);
        DocumentoUtil.imprimirHTML(html, nombreImpresora, LLAVE_IMPRESORA);
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

    private String convertirSalidaAHtml(TicketSalida t) {
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
        html.append("<b>MONTO BASE:</b> $").append(String.format("%.2f", t.getMontoBase())).append("<br>");
        if (t.getCargoExtra() > 0) {
            html.append("<b>CARGO EXTRA:</b> $").append(String.format("%.2f", t.getCargoExtra())).append("<br>");
        }
      if (!t.getDescuento().trim().isEmpty()) {
            
            String etiquetaDescuento = t.getDescuento().contains("Pesos") ? t.getDescuento() : "$ " + t.getDescuento();
            html.append("<b>DESCUENTO:</b> ").append(etiquetaDescuento).append("<br>");
        }
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