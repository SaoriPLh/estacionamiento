package com.estacionamiento.modelo;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reporte {
    private String titulo;
    private String nombreEstacionamiento;
    private String nombreGenerador; 
    private String rangoFechas;
    private List<Registro> listaRegistros;
    private double totalIngresos;
    private String fechaImpresion;

    public Reporte(String titulo, String nombreEstacionamiento, String nombreGenerador, 
                   String rangoFechas, List<Registro> listaRegistros) {
        this.titulo = titulo;
        this.nombreEstacionamiento = nombreEstacionamiento;
        this.nombreGenerador = nombreGenerador;
        this.rangoFechas = rangoFechas;
        this.listaRegistros = listaRegistros;
        this.fechaImpresion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        calcularTotal();
    }

    private void calcularTotal() {
        this.totalIngresos = listaRegistros.stream().mapToDouble(Registro::getMonto).sum();
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getNombreEstacionamiento() { return nombreEstacionamiento; }
    public String getNombreGenerador() { return nombreGenerador; }
    public String getRangoFechas() { return rangoFechas; }
    public List<Registro> getListaRegistros() { return listaRegistros; }
    public double getTotalIngresos() { return totalIngresos; }
    public String getFechaImpresion() { return fechaImpresion; }
}