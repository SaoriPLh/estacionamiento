package com.estacionamiento.servicio;

import com.estacionamiento.dao.RegistroDAO;
import com.estacionamiento.dao.RegistroDAO.RegistroInfo;
import com.estacionamiento.dao.RegistroDAO.PermisoInfo;

import java.util.List;


public class ConsultaServicio {

    private final RegistroDAO registroDAO = new RegistroDAO();

    
    private static final String SEP =
            "+----------+------------+----------+--------+-------+------------------+------------------+----------+";

    
    public void consultarPorPlaca(String placa) {
        List<RegistroInfo> lista = registroDAO.buscarPorPlaca(placa);

        System.out.println("\n=== HISTORIAL DE PLACA: " + placa.toUpperCase() + " ===");
        if (lista.isEmpty()) {
            System.out.println("  No se encontraron registros para esa placa.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);
        System.out.printf("  Total de visitas: %d%n%n", lista.size());
    }

    
    public void consultarPorFecha(String fecha) {
        List<RegistroInfo> lista = registroDAO.buscarPorFecha(fecha);

        System.out.println("\n=== REGISTROS DEL DÍA: " + fecha + " ===");
        if (lista.isEmpty()) {
            System.out.println("  No se encontraron registros para esa fecha.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);
        System.out.printf("  Total de movimientos: %d%n%n", lista.size());
    }

    
    // -----------------------------------------------------------------------
    public void consultarPorRangoFechas(String fechaInicio, String fechaFin) {
        List<RegistroInfo> lista = registroDAO.buscarPorRangoFechas(fechaInicio, fechaFin);

        System.out.println("\n=== REGISTROS DEL " + fechaInicio + " AL " + fechaFin + " ===");
        if (lista.isEmpty()) {
            System.out.println("  No se encontraron registros en ese rango.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);

        double total = lista.stream().mapToDouble(ri -> ri.registro.getMonto()).sum();
        System.out.printf("  Registros: %d  |  Ingreso total del periodo: $%.2f%n%n",
                lista.size(), total);
    }

    
    public void consultarActivos() {
        List<RegistroInfo> lista = registroDAO.buscarActivos();

        System.out.println("\n=== VEHÍCULOS ACTUALMENTE EN EL ESTACIONAMIENTO ===");
        if (lista.isEmpty()) {
            System.out.println("  No hay vehículos estacionados en este momento.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);
        System.out.printf("  Total ocupados: %d%n%n", lista.size());
    }

    public void consultarPorZona(int idZona) {
        List<RegistroInfo> lista = registroDAO.buscarPorZona(idZona);

        System.out.println("\n=== REGISTROS DE LA ZONA ID: " + idZona + " ===");
        if (lista.isEmpty()) {
            System.out.println("  No se encontraron registros para esa zona.");
            return;
        }

        System.out.println("  Zona: " + lista.get(0).nombreZona);
        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);

        double total = lista.stream().mapToDouble(ri -> ri.registro.getMonto()).sum();
        System.out.printf("  Total registros: %d  |  Ingresos zona: $%.2f%n%n",
                lista.size(), total);
    }

    public void consultarPorPlacaYFecha(String placa, String fecha) {
        List<RegistroInfo> lista = registroDAO.buscarPorPlacaYFecha(placa, fecha);

        System.out.println("\n=== PLACA: " + placa.toUpperCase() + "  FECHA: " + fecha + " ===");
        if (lista.isEmpty()) {
            System.out.println("  Sin registros para esa combinación.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);
    }

    
    public void consultarIngresosPorZona() {
        List<Object[]> resultados = registroDAO.calcularIngresosPorZona();

        System.out.println("\n=== INGRESOS TOTALES POR ZONA ===");
        System.out.println("+------------------------+------------------+------------------+");
        System.out.printf( "| %-22s | %-16s | %-16s |%n",
                "Zona", "Ingresos ($)", "Total registros");
        System.out.println("+------------------------+------------------+------------------+");

        double granTotal = 0;
        for (Object[] fila : resultados) {
            String zona     = (String)  fila[0];
            double ingresos = (Double)  fila[1];
            int    cantidad = (Integer) fila[2];
            granTotal += ingresos;
            System.out.printf("| %-22s | %16.2f | %16d |%n", zona, ingresos, cantidad);
        }

        System.out.println("+------------------------+------------------+------------------+");
        System.out.printf("  GRAN TOTAL: $%.2f%n%n", granTotal);
    }

   
    public void consultarPensionadosActivos() {
        List<PermisoInfo> lista = registroDAO.buscarPensionadosActivos();

        System.out.println("\n=== PENSIONADOS ACTIVOS ===");
        if (lista.isEmpty()) {
            System.out.println("  No hay pensionados activos.");
            return;
        }

        String sepP = "+------+------------------------------+------------------------+--------------------+";
        System.out.println(sepP);
        System.out.printf("| %-4s | %-28s | %-22s | %-18s |%n",
                "ID", "Nombre", "Zona", "Desde");
        System.out.println(sepP);

        for (PermisoInfo pi : lista) {
            String nombre = pi.nombre + " " + pi.apellidoPaterno;
            System.out.printf("| %-4d | %-28s | %-22s | %-18s |%n",
                    pi.permiso.getIdPermiso(), nombre, pi.nombreZona,
                    nvl(pi.permiso.getFechaAsignacion()));
        }

        System.out.println(sepP);
        System.out.printf("  Total pensionados activos: %d%n%n", lista.size());
    }

    // -----------------------------------------------------------------------
    public void consultarPensionadosPorVencer(int dias) {
        List<PermisoInfo> lista = registroDAO.buscarPensionadosPorVencer(dias);

        System.out.println("\n=== PENSIONES QUE VENCEN EN LOS PRÓXIMOS " + dias + " DÍA(S) ===");
        if (lista.isEmpty()) {
            System.out.println("  No hay pensiones próximas a vencer.");
            return;
        }

        for (PermisoInfo pi : lista) {
            String nombre = pi.nombre + " " + pi.apellidoPaterno;
            System.out.printf("  [ID %d] %-25s  Zona: %s%n",
                    pi.permiso.getIdPermiso(), nombre, pi.nombreZona);
        }
        System.out.println();
    }

   
    public void consultarPorEspacio(String codigoEspacio) {
        List<RegistroInfo> lista = registroDAO.buscarPorEspacio(codigoEspacio);

        System.out.println("\n=== HISTORIAL DEL ESPACIO: " + codigoEspacio.toUpperCase() + " ===");
        if (lista.isEmpty()) {
            System.out.println("  No se encontraron registros para ese espacio.");
            return;
        }

        imprimirEncabezadoRegistros();
        for (RegistroInfo ri : lista) imprimirFilaRegistro(ri);
        System.out.println(SEP);
        System.out.printf("  Total usos del espacio: %d%n%n", lista.size());
    }

   
    public void consultarVisitasPorPlaca(String placa) {
        int total = registroDAO.contarVisitasPorPlaca(placa);
        System.out.printf("%n=== VISITAS DE LA PLACA %s ===%n  Total de veces registrada: %d%n%n",
                placa.toUpperCase(), total);
    }

    
    // -----------------------------------------------------------------------
    public void consultarUltimaEntrada(String placa) {
        RegistroInfo ri = registroDAO.buscarUltimaEntrada(placa);

        System.out.println("\n=== ÚLTIMA ENTRADA DE LA PLACA: " + placa.toUpperCase() + " ===");
        if (ri == null) {
            System.out.println("  No se encontró ningún registro para esa placa.");
            return;
        }

        System.out.println("  ID Registro : " + ri.registro.getIdRegistro());
        System.out.println("  Zona        : " + ri.nombreZona);
        System.out.println("  Espacio     : " + ri.codigoEspacio);
        System.out.println("  Hora entrada: " + nvl(ri.registro.getHoraEntrada()));
        System.out.println("  Hora salida : " + (ri.registro.getHoraSalida() != null
                            ? ri.registro.getHoraSalida() : "AÚN EN EL ESTACIONAMIENTO"));
        System.out.println("  Monto       : $" + String.format("%.2f", ri.registro.getMonto()));
        System.out.println("  Estado      : " + ri.estadoRegistro);
        System.out.println();
    }

    private void imprimirEncabezadoRegistros() {
        System.out.println(SEP);
        System.out.printf("| %-8s | %-10s | %-8s | %-6s | %-5s | %-16s | %-16s | %-8s |%n",
                "ID", "Placa", "Espacio", "Zona", "Monto", "Entrada", "Salida", "Estado");
        System.out.println(SEP);
    }

    private void imprimirFilaRegistro(RegistroInfo ri) {
        String salida = ri.registro.getHoraSalida() != null
                        ? abrev(ri.registro.getHoraSalida(), 16) : "En curso";
        System.out.printf("| %-8d | %-10s | %-8s | %-6s | %5.2f | %-16s | %-16s | %-8s |%n",
                ri.registro.getIdRegistro(),
                ri.placa,
                ri.codigoEspacio,
                abrev(ri.nombreZona, 6),
                ri.registro.getMonto(),
                abrev(nvl(ri.registro.getHoraEntrada()), 16),
                salida,
                abrev(ri.estadoRegistro, 8));
    }

    private String nvl(String s) {
        return s != null ? s : "—";
    }

    private String abrev(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max);
    }
}
