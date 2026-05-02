package com.estacionamiento.util;

import javax.swing.JEditorPane;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.text.html.HTMLEditorKit;

public class DocumentoUtil {

    public static void imprimirHTML(String contenidoHtml, String nombreImpresora, String llave) {
        JEditorPane pane = new JEditorPane();
        pane.setEditable(false);
        pane.setContentType("text/html");
        pane.setEditorKit(new HTMLEditorKit());
        pane.setText(contenidoHtml);

        try {
           
            PrintService[] servicios = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService impresoraDeseada = null;

            for (PrintService s : servicios) {
                if (s.getName().equalsIgnoreCase(nombreImpresora)) {
                    impresoraDeseada = s;
                    break;
                }
            }

           
            if (impresoraDeseada != null) {
                pane.print(null, null, false, impresoraDeseada, null, true);
                System.out.println("Impresión enviada a: " + nombreImpresora);
            } else {
                System.err.println("La impresora '" + nombreImpresora + "' no está disponible. Abriendo selector...");

           
                PrinterJob job = PrinterJob.getPrinterJob();
                if (job.printDialog()) {

                    PrintService nuevaImpresora = job.getPrintService();
                    String nuevoNombre = nuevaImpresora.getName();

                    ConfiguracionLocal.actualizarSoloUna(llave, nuevoNombre);
                    
                    pane.print(null, null, false, nuevaImpresora, null, true);
                }
            }

        } catch (PrinterException e) {
            System.err.println("Error al intentar imprimir: " + e.getMessage());
            e.printStackTrace();
        }
    }
}