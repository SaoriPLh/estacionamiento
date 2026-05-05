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

      // ── CSS COMPACTO ─────────────────────────────
      .append("<style>")
      .append("body{font-family:Arial;padding:10px;color:#111;font-size:12px;}")

      .append(".page{max-width:500px;margin:auto;}")

      .append(".header{border-bottom:1px solid #000;margin-bottom:10px;padding-bottom:5px;}")
      .append(".title{font-size:18px;font-weight:bold;}")
      .append(".sub{font-size:10px;color:#555;}")

      .append(".section{margin-top:12px;}")
      .append(".section-title{font-size:10px;color:#777;text-transform:uppercase;margin-bottom:5px;}")

      .append(".grid{display:grid;grid-template-columns:repeat(3,1fr);gap:5px;}")

      .append(".card{border:1px solid #ccc;padding:6px;text-align:center;}")

      .append(".label{font-size:9px;color:#666;}")
      .append(".value{font-size:14px;font-weight:bold;}")

      .append(".highlight{background:#111;color:#fff;}")

      .append(".footer{margin-top:15px;font-size:10px;color:#555;border-top:1px solid #ccc;padding-top:5px;}")

      .append(".btn{margin-top:8px;padding:5px 10px;font-size:10px;border:none;background:#111;color:white;cursor:pointer;}")

      .append("@media print{.btn{display:none;}}")

      .append("</style></head><body><div class='page'>");

    // ── HEADER ─────────────────────────────
    sb.append("<div class='header'>")
      .append("<div class='title'>").append(reporte.getTitulo()).append("</div>")
      .append("<div class='sub'>").append(reporte.getNombreEstacionamiento()).append("</div>")
      .append("<div class='sub'>Periodo: ").append(reporte.getRangoFechas()).append("</div>")
      .append("</div>");

    // ── CÁLCULOS ─────────────────────────────
    long activos = reporte.getListaRegistros().stream()
            .filter(r -> r.getHoraSalida() == null).count();

    long cerrados = reporte.getListaRegistros().stream()
            .filter(r -> r.getHoraSalida() != null).count();

    int total = reporte.getListaRegistros().size();

    double ocupacion = total == 0 ? 0 : (activos * 100.0 / total);

    double ingresosPension = reporte.getListaRegistros().stream()
            .filter(r -> r.getTarifa().getTipoTarifa().getDescripcion()
            .toLowerCase().contains("pension"))
            .mapToDouble(Registro::getMonto)
            .sum();

    double ingresosHora = reporte.getTotalIngresos() - ingresosPension;

    // ── RESUMEN GENERAL ─────────────────────────────
    sb.append("<div class='section'>")
      .append("<div class='section-title'>Resumen General</div>")
      .append("<div class='grid'>")

      .append("<div class='card'>")
      .append("<div class='label'>Total</div>")
      .append("<div class='value'>").append(total).append("</div>")
      .append("</div>")

      .append("<div class='card'>")
      .append("<div class='label'>Finalizados / Activos</div>")
      .append("<div class='value'>").append(cerrados).append(" / ").append(activos).append("</div>")
      .append("</div>")

      .append("<div class='card'>")
      .append("<div class='label'>Ocupación</div>")
      .append("<div class='value'>")
      .append(String.format("%.1f", ocupacion)).append("%</div>")
      .append("</div>")

      .append("</div></div>");

    // ── RESUMEN FINANCIERO ─────────────────────────────
    sb.append("<div class='section'>")
      .append("<div class='section-title'>Ingresos</div>")
      .append("<div class='grid'>")

      .append("<div class='card highlight'>")
      .append("<div class='label'>Total</div>")
      .append("<div class='value'>$")
      .append(String.format("%.2f", reporte.getTotalIngresos()))
      .append("</div></div>")

      .append("<div class='card'>")
      .append("<div class='label'>Hora</div>")
      .append("<div class='value'>$")
      .append(String.format("%.2f", ingresosHora))
      .append("</div></div>")

      .append("<div class='card'>")
      .append("<div class='label'>Pensión</div>")
      .append("<div class='value'>$")
      .append(String.format("%.2f", ingresosPension))
      .append("</div></div>")

      .append("</div></div>");

    // ── FOOTER ─────────────────────────────
    sb.append("<div class='footer'>")
      .append("Generado por: ").append(reporte.getNombreGenerador()).append("<br>")
      .append("Fecha: ").append(reporte.getFechaImpresion())
      .append("<br><button class='btn' onclick='window.print()'>Imprimir</button>")
      .append("</div>");

    sb.append("</div></body></html>");

    String nombreImpresora = ConfiguracionLocal.getImpresora("reportes");

    DocumentoUtil.imprimirHTML(sb.toString(), nombreImpresora, "reportes");
}

   
}