package com.estacionamiento.util;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.text.html.HTMLEditorKit;

public class DocumentoUtil {

    public static void imprimirHTML(String contenidoHtml, String nombreImpresora, String llave) {
        SwingUtilities.invokeLater(() -> {
            JEditorPane pane = new JEditorPane();
            pane.setEditable(false);
            pane.setContentType("text/html");
            pane.setEditorKit(new HTMLEditorKit());
            pane.setText(contenidoHtml);
            pane.setSize(620, 900); // forzar layout antes de imprimir

            try {
                PrintService impresoraDeseada = null;

                if (nombreImpresora != null && !nombreImpresora.isBlank()) {
                    for (PrintService s : PrintServiceLookup.lookupPrintServices(null, null)) {
                        if (s.getName().equalsIgnoreCase(nombreImpresora)) {
                            impresoraDeseada = s;
                            break;
                        }
                    }
                    if (impresoraDeseada == null) {
                        System.err.println("[Impresora] '" + nombreImpresora
                                + "' desconectada o no encontrada. Abriendo selector...");
                    }
                }

                if (impresoraDeseada != null) {
                    // Impresora guardada y disponible → imprime sin dialogo
                    pane.print(null, null, false, impresoraDeseada, null, true);
                    System.out.println("[Impresora] Enviado a: " + nombreImpresora);
                } else {
                    // Sin impresora configurada o desconectada → mostrar dialogo
                    // pane.print con showPrintDialog=true muestra el dialogo nativo
                    // y funciona correctamente desde el EDT de Swing
                    PrinterJob job = PrinterJob.getPrinterJob();
                    if (job.printDialog()) {
                        PrintService elegida = job.getPrintService();
                        // Guardar la seleccion para futuras impresiones
                        ConfiguracionLocal.actualizarSoloUna(llave, elegida.getName());
                        pane.print(null, null, false, elegida, null, true);
                        System.out.println("[Impresora] Guardada: " + elegida.getName());
                    }
                }
            } catch (PrinterException e) {
                System.err.println("[Impresora] Error al imprimir: " + e.getMessage());
            }
        });
    }
}
