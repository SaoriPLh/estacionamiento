package com.estacionamiento;

import com.estacionamiento.servicio.ConsultaServicio;

import java.util.Scanner;

public class MenuConsultas {

    private static final ConsultaServicio servicio = new ConsultaServicio();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean ejecutando = true;

        while (ejecutando) {
            imprimirMenu();
            String opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1"  -> consultarPorPlaca();
                case "2"  -> consultarPorFecha();
                case "3"  -> consultarPorRango();
                case "4"  -> servicio.consultarActivos();
                case "5"  -> consultarPorZona();
                case "6"  -> consultarPorPlacaYFecha();
                case "7"  -> servicio.consultarIngresosPorZona();
                case "8"  -> servicio.consultarPensionadosActivos();
                case "9"  -> consultarPorVencer();
                case "10" -> consultarPorEspacio();
                case "11" -> consultarVisitasPorPlaca();
                case "12" -> consultarUltimaEntrada();
                case "0"  -> { ejecutando = false; System.out.println("\nHasta luego.\n"); }
                default   -> System.out.println("  Opción no válida, intente de nuevo.");
            }
        }

        sc.close();
    }

    private static void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE ESTACIONAMIENTO - CONSULTAS ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1.  Historial por placa                  ║");
        System.out.println("║  2.  Registros de una fecha exacta        ║");
        System.out.println("║  3.  Registros entre dos fechas           ║");
        System.out.println("║  4.  Vehículos actualmente estacionados   ║");
        System.out.println("║  5.  Registros por zona                   ║");
        System.out.println("║  6.  Placa + fecha (cruce)                ║");
        System.out.println("║  7.  Ingresos totales por zona            ║");
        System.out.println("║  8.  Pensionados activos                  ║");
        System.out.println("║  9.  Pensiones próximas a vencer          ║");
        System.out.println("║  10. Historial de un espacio              ║");
        System.out.println("║  11. Cuántas veces ha entrado una placa   ║");
        System.out.println("║  12. Última entrada de una placa          ║");
        System.out.println("║  0.  Salir                                ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("  Seleccione una opción: ");
    }

   

    private static void consultarPorPlaca() {
        System.out.print("  Ingrese la placa: ");
        String placa = sc.nextLine().trim();
        if (!placa.isEmpty()) servicio.consultarPorPlaca(placa);
    }

    private static void consultarPorFecha() {
        System.out.print("  Ingrese la fecha (yyyy-MM-dd): ");
        String fecha = sc.nextLine().trim();
        if (validarFecha(fecha)) servicio.consultarPorFecha(fecha);
    }

    private static void consultarPorRango() {
        System.out.print("  Fecha inicio (yyyy-MM-dd): ");
        String inicio = sc.nextLine().trim();
        System.out.print("  Fecha fin    (yyyy-MM-dd): ");
        String fin = sc.nextLine().trim();
        if (validarFecha(inicio) && validarFecha(fin)) {
            servicio.consultarPorRangoFechas(inicio, fin);
        }
    }

    private static void consultarPorZona() {
        System.out.print("  Ingrese el ID de la zona (estacionamiento): ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            servicio.consultarPorZona(id);
        } catch (NumberFormatException e) {
            System.out.println("  ID inválido.");
        }
    }

    private static void consultarPorPlacaYFecha() {
        System.out.print("  Ingrese la placa: ");
        String placa = sc.nextLine().trim();
        System.out.print("  Ingrese la fecha (yyyy-MM-dd): ");
        String fecha = sc.nextLine().trim();
        if (!placa.isEmpty() && validarFecha(fecha)) {
            servicio.consultarPorPlacaYFecha(placa, fecha);
        }
    }

    private static void consultarPorVencer() {
        System.out.print("  Días de anticipación para alertar (ej: 7): ");
        try {
            int dias = Integer.parseInt(sc.nextLine().trim());
            servicio.consultarPensionadosPorVencer(dias);
        } catch (NumberFormatException e) {
            System.out.println("  Valor inválido.");
        }
    }

    private static void consultarPorEspacio() {
        System.out.print("  Ingrese el código del espacio (ej: A-01): ");
        String codigo = sc.nextLine().trim();
        if (!codigo.isEmpty()) servicio.consultarPorEspacio(codigo);
    }

    private static void consultarVisitasPorPlaca() {
        System.out.print("  Ingrese la placa: ");
        String placa = sc.nextLine().trim();
        if (!placa.isEmpty()) servicio.consultarVisitasPorPlaca(placa);
    }

    private static void consultarUltimaEntrada() {
        System.out.print("  Ingrese la placa: ");
        String placa = sc.nextLine().trim();
        if (!placa.isEmpty()) servicio.consultarUltimaEntrada(placa);
    }

    private static boolean validarFecha(String fecha) {
        if (fecha == null || !fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("  Formato de fecha inválido. Use yyyy-MM-dd (ej: 2026-04-26)");
            return false;
        }
        return true;
    }
}
