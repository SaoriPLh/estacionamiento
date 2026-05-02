package com.estacionamiento.servicio;

import com.estacionamiento.modelo.Reporte;
import com.estacionamiento.modelo.Registro;
import com.estacionamiento.util.ConfiguracionLocal;
import com.estacionamiento.util.DocumentoUtil;
import java.io.*;

public class ReporteService {

    public void generarReporteHTML(Reporte reporte) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html><html lang='es'><head>")
          .append("<meta charset='UTF-8'>")
          .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
          .append("<title>").append(reporte.getTitulo()).append("</title>")
          .append("<link href='https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=IBM+Plex+Mono:wght@400;600&family=IBM+Plex+Sans:wght@300;400;500&display=swap' rel='stylesheet'>")
          .append("<style>")

          .append(":root {")
          .append("  --ink:      #0d0d0d;")
          .append("  --paper:    #f5f0e8;")
          .append("  --accent:   #1a3a5c;")
          .append("  --gold:     #c8963e;")
          .append("  --rule:     #c8c0b0;")
          .append("  --row-alt:  #ede8de;")
          .append("  --badge-bg: #1a3a5c;")
          .append("  --badge-fg: #f5f0e8;")
          .append("}")

         
          .append("*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }")

          .append("body {")
          .append("  background: var(--paper);")
          .append("  color: var(--ink);")
          .append("  font-family: 'IBM Plex Sans', sans-serif;")
          .append("  font-weight: 300;")
          .append("  line-height: 1.6;")
          .append("  min-height: 100vh;")
          .append("}")
          .append(".page {")
          .append("  max-width: 900px;")
          .append("  margin: 0 auto;")
          .append("  padding: 48px 40px 64px;")
          .append("}")

          // ── Header ─────────────────────────────────────────────────────────
          .append(".header {")
          .append("  display: grid;")
          .append("  grid-template-columns: 1fr auto;")
          .append("  align-items: end;")
          .append("  gap: 24px;")
          .append("  border-bottom: 3px solid var(--ink);")
          .append("  padding-bottom: 20px;")
          .append("  margin-bottom: 8px;")
          .append("}")
          .append(".header-left {}") 
          .append(".report-label {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 10px;")
          .append("  letter-spacing: 0.25em;")
          .append("  text-transform: uppercase;")
          .append("  color: var(--gold);")
          .append("  margin-bottom: 6px;")
          .append("}")
          .append("h1 {")
          .append("  font-family: 'Playfair Display', serif;")
          .append("  font-size: 2.4rem;")
          .append("  line-height: 1.1;")
          .append("  color: var(--accent);")
          .append("  letter-spacing: -0.02em;")
          .append("}")
          .append(".sede {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 11px;")
          .append("  letter-spacing: 0.15em;")
          .append("  color: #666;")
          .append("  margin-top: 6px;")
          .append("  text-transform: uppercase;")
          .append("}")
          .append(".header-right {")
          .append("  text-align: right;")
          .append("}")
          .append(".period-box {")
          .append("  border: 1px solid var(--rule);")
          .append("  padding: 10px 16px;")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 11px;")
          .append("}")
          .append(".period-box .period-label {")
          .append("  color: #888;")
          .append("  font-size: 9px;")
          .append("  letter-spacing: 0.2em;")
          .append("  text-transform: uppercase;")
          .append("  display: block;")
          .append("  margin-bottom: 2px;")
          .append("}")
          .append(".period-box .period-val { font-weight: 600; }")

          .append(".sub-rule {")
          .append("  height: 1px;")
          .append("  background: var(--rule);")
          .append("  margin-bottom: 32px;")
          .append("}")

         
          .append(".summary {")
          .append("  display: grid;")
          .append("  grid-template-columns: repeat(3, 1fr);")
          .append("  gap: 1px;")
          .append("  background: var(--rule);")
          .append("  border: 1px solid var(--rule);")
          .append("  margin-bottom: 36px;")
          .append("}")
          .append(".summary-card {")
          .append("  background: var(--paper);")
          .append("  padding: 18px 20px;")
          .append("}")
          .append(".summary-card .s-label {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 9px;")
          .append("  letter-spacing: 0.22em;")
          .append("  text-transform: uppercase;")
          .append("  color: #888;")
          .append("  margin-bottom: 6px;")
          .append("}")
          .append(".summary-card .s-value {")
          .append("  font-family: 'Playfair Display', serif;")
          .append("  font-size: 1.8rem;")
          .append("  color: var(--accent);")
          .append("  line-height: 1;")
          .append("}")
          .append(".summary-card.highlight .s-value { color: var(--gold); }")

          
          .append(".section-title {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 10px;")
          .append("  letter-spacing: 0.25em;")
          .append("  text-transform: uppercase;")
          .append("  color: #888;")
          .append("  margin-bottom: 12px;")
          .append("}")
          .append("table {")
          .append("  width: 100%;")
          .append("  border-collapse: collapse;")
          .append("  font-size: 13px;")
          .append("}")
          .append("thead tr {")
          .append("  border-bottom: 2px solid var(--ink);")
          .append("}")
          .append("th {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 9px;")
          .append("  letter-spacing: 0.2em;")
          .append("  text-transform: uppercase;")
          .append("  font-weight: 600;")
          .append("  text-align: left;")
          .append("  padding: 10px 12px;")
          .append("  color: var(--accent);")
          .append("}")
          .append("td {")
          .append("  padding: 10px 12px;")
          .append("  border-bottom: 1px solid var(--rule);")
          .append("  vertical-align: middle;")
          .append("}")
          .append("tbody tr:nth-child(even) { background: var(--row-alt); }")
          .append("tbody tr:last-child td { border-bottom: 2px solid var(--ink); }")
          .append(".placa {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-weight: 600;")
          .append("  font-size: 12px;")
          .append("  background: var(--badge-bg);")
          .append("  color: var(--badge-fg);")
          .append("  padding: 2px 8px;")
          .append("  display: inline-block;")
          .append("  letter-spacing: 0.1em;")
          .append("}")
          .append(".monto {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-weight: 600;")
          .append("  text-align: right;")
          .append("}")
          .append(".hora {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 11px;")
          .append("  color: #555;")
          .append("}")
          .append(".activo {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 10px;")
          .append("  color: #888;")
          .append("  font-style: italic;")
          .append("}")

          // ── Total row ──────────────────────────────────────────────────────
          .append(".total-row td {")
          .append("  background: var(--accent);")
          .append("  color: var(--paper);")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-weight: 600;")
          .append("  font-size: 14px;")
          .append("  border-bottom: none;")
          .append("}")
          .append(".total-row .total-label {")
          .append("  letter-spacing: 0.15em;")
          .append("  text-transform: uppercase;")
          .append("  font-size: 10px;")
          .append("}")

          
          .append(".footer {")
          .append("  margin-top: 48px;")
          .append("  display: flex;")
          .append("  justify-content: space-between;")
          .append("  align-items: flex-end;")
          .append("  border-top: 1px solid var(--rule);")
          .append("  padding-top: 16px;")
          .append("}")
          .append(".footer-meta {")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 10px;")
          .append("  color: #999;")
          .append("  line-height: 1.8;")
          .append("}")
          .append(".footer-meta span { color: #555; font-weight: 600; }")
          .append(".btn-print {")
          .append("  background: var(--accent);")
          .append("  color: var(--paper);")
          .append("  border: none;")
          .append("  padding: 10px 24px;")
          .append("  font-family: 'IBM Plex Mono', monospace;")
          .append("  font-size: 11px;")
          .append("  letter-spacing: 0.15em;")
          .append("  text-transform: uppercase;")
          .append("  cursor: pointer;")
          .append("}")
          .append(".btn-print:hover { background: var(--gold); }")

          // ── Print overrides ────────────────────────────────────────────────
          .append("@media print {")
          .append("  body { background: white; }")
          .append("  .btn-print { display: none; }")
          .append("  .page { padding: 0; }")
          .append("  tbody tr:nth-child(even) { background: #f0ede6; -webkit-print-color-adjust: exact; print-color-adjust: exact; }")
          .append("  .placa { -webkit-print-color-adjust: exact; print-color-adjust: exact; }")
          .append("  .total-row td { -webkit-print-color-adjust: exact; print-color-adjust: exact; }")
          .append("}")

          .append("</style></head><body><div class='page'>");

     
        sb.append("<div class='header'>")
          .append("<div class='header-left'>")
          .append("<div class='report-label'>Reporte de Ingresos</div>")
          .append("<h1>").append(reporte.getTitulo()).append("</h1>")
          .append("<div class='sede'>&#9632;&nbsp;").append(reporte.getNombreEstacionamiento()).append("</div>")
          .append("</div>")
          .append("<div class='header-right'>")
          .append("<div class='period-box'>")
          .append("<span class='period-label'>Período</span>")
          .append("<span class='period-val'>").append(reporte.getRangoFechas()).append("</span>")
          .append("</div>")
          .append("</div>")
          .append("</div>")
          .append("<div class='sub-rule'></div>");

       
        long activos   = reporte.getListaRegistros().stream().filter(r -> r.getHoraSalida() == null).count();
        long cerrados  = reporte.getListaRegistros().stream().filter(r -> r.getHoraSalida() != null).count();

        sb.append("<div class='summary'>")
          .append("<div class='summary-card'>")
          .append("<div class='s-label'>Total registros</div>")
          .append("<div class='s-value'>").append(reporte.getListaRegistros().size()).append("</div>")
          .append("</div>")
          .append("<div class='summary-card'>")
          .append("<div class='s-label'>Finalizados / Activos</div>")
          .append("<div class='s-value'>").append(cerrados).append(" / ").append(activos).append("</div>")
          .append("</div>")
          .append("<div class='summary-card highlight'>")
          .append("<div class='s-label'>Total recaudado</div>")
          .append("<div class='s-value'>$").append(String.format("%.2f", reporte.getTotalIngresos())).append("</div>")
          .append("</div>")
          .append("</div>");

        // ── TABLA ─────────────────────────────────────────────────────────────
        sb.append("<div class='section-title'>Detalle de registros</div>");
        sb.append("<table>")
          .append("<thead><tr>")
          .append("<th>Placa</th>")
          .append("<th>Espacio</th>")
          .append("<th>Entrada</th>")
          .append("<th>Salida</th>")
          .append("<th>Tarifa</th>")
          .append("<th style='text-align:right'>Monto</th>")
          .append("</tr></thead><tbody>");

        for (Registro reg : reporte.getListaRegistros()) {
            String horaSalidaStr = reg.getHoraSalida() != null
                ? reg.getHoraSalida().toString().replace("T", " ")
                : "<span class='activo'>En curso</span>";

            sb.append("<tr>")
              .append("<td><span class='placa'>").append(reg.getVehiculo().getPlaca()).append("</span></td>")
              .append("<td class='hora'>").append(reg.getEspacio().getCodigo()).append("</td>")
              .append("<td class='hora'>").append(reg.getHoraEntrada().toString().replace("T", " ")).append("</td>")
              .append("<td class='hora'>").append(horaSalidaStr).append("</td>")
              .append("<td>").append(reg.getTarifa().getTipoTarifa().getDescripcion()).append("</td>")
              .append("<td class='monto'>$").append(String.format("%.2f", reg.getMonto())).append("</td>")
              .append("</tr>");
        }

        
        sb.append("<tr class='total-row'>")
          .append("<td colspan='5' class='total-label'>Total recaudado</td>")
          .append("<td class='monto'>$").append(String.format("%.2f", reporte.getTotalIngresos())).append("</td>")
          .append("</tr>");

        sb.append("</tbody></table>");

      
        sb.append("<div class='footer'>")
          .append("<div class='footer-meta'>")
          .append("Generado por: <span>").append(reporte.getNombreGenerador()).append("</span><br>")
          .append("Fecha de impresión: <span>").append(reporte.getFechaImpresion()).append("</span>")
          .append("</div>")
          .append("<button class='btn-print' onclick='window.print()'>&#128438;&nbsp; Imprimir</button>")
          .append("</div>")
          .append("</div></body></html>");

       String nombreImpresora = ConfiguracionLocal.getImpresora("reportes");

    // 2. Si no hay una configurada (es null), el método imprimirHTML 
    //    mostrará el diálogo por defecto gracias al "if" que planeamos.
    DocumentoUtil.imprimirHTML(sb.toString(), nombreImpresora,"reportes");
    }

   
}