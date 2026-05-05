package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Registro;
import com.estacionamiento.modelo.Reporte;
import com.estacionamiento.modelo.Tarifa;
import com.estacionamiento.servicio.ReporteService;
import com.estacionamiento.servicio.RegistroServicio;
import com.estacionamiento.servicio.TarifaService;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Tarifa> cmbFiltroTarifa;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private VBox vboxRegistros;
    @FXML private Label lblSinResultados;
    @FXML private Button btnImprimir;

    private final RegistroServicio registroServicio = new RegistroServicio();
    private final TarifaService tarifaService = new TarifaService();
    private final ReporteService reporteService = new ReporteService();

    private List<Registro> registrosMostrados;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss");

    @FXML
    private void initialize() {
        cargarTarifasFiltro();
        dpInicio.setValue(LocalDate.now().minusDays(7));
        dpFin.setValue(LocalDate.now());
        filtrar();

        txtBuscar.textProperty().addListener((obs, old, val) -> filtrarLocalmente(val));
    }

    private void cargarTarifasFiltro() {
        List<Tarifa> tarifas = SessionManager.MODO_DEMO
                ? MockData.getTarifas()
                : tarifaService.obtenerTarifasPorSede(
                        SessionManager.getInstance().getEstacionamiento().getIdEstacionamiento());

        Tarifa todos = new Tarifa();
        todos.setIdTarifa(0);
        cmbFiltroTarifa.getItems().add(todos);
        cmbFiltroTarifa.getItems().addAll(tarifas);

        cmbFiltroTarifa.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Tarifa t) {
                if (t == null) return "";
                return t.getIdTarifa() == 0 ? "Todos" : t.getTipoTarifa().getDescripcion();
            }
            public Tarifa fromString(String s) { return null; }
        });
        cmbFiltroTarifa.getSelectionModel().selectFirst();
    }

    @FXML
    private void filtrar() {
        MainLayoutController.reiniciarTimer();

        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        if (inicio == null) inicio = LocalDate.now().minusDays(7);
        if (fin == null) fin = LocalDate.now();

        Tarifa tarifaSel = cmbFiltroTarifa.getValue();
        Integer idTarifa = (tarifaSel != null && tarifaSel.getIdTarifa() != 0)
                ? tarifaSel.getTipoTarifa().getIdTipoTarifa() : null;

        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Historial: 15 registros simulados — BD omitida.");
            registrosMostrados = MockData.getHistorial();
        } else {
            int idSede = SessionManager.getInstance().getEstacionamiento().getIdEstacionamiento();
            registrosMostrados = registroServicio.obtenerRegistrosFiltrados(
                    inicio.atStartOfDay(), fin.atTime(LocalTime.MAX), idTarifa, idSede);
        }

        mostrarRegistros(registrosMostrados);

        boolean hayResultados = !registrosMostrados.isEmpty();
        btnImprimir.setVisible(hayResultados);
        btnImprimir.setManaged(hayResultados);
    }

    private void filtrarLocalmente(String termino) {
        if (registrosMostrados == null) return;
        if (termino == null || termino.isBlank()) {
            mostrarRegistros(registrosMostrados);
            return;
        }
        String t = termino.toLowerCase();
        List<Registro> filtrados = registrosMostrados.stream()
                .filter(r -> {
                    String placa = r.getVehiculo() != null ? r.getVehiculo().getPlaca().toLowerCase() : "";
                    String esp = r.getEspacio() != null && r.getEspacio().getCodigo() != null
                            ? r.getEspacio().getCodigo().toLowerCase() : "";
                    return placa.contains(t) || esp.contains(t);
                }).toList();
        mostrarRegistros(filtrados);
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        dpInicio.setValue(LocalDate.now().minusDays(7));
        dpFin.setValue(LocalDate.now());
        cmbFiltroTarifa.getSelectionModel().selectFirst();
        filtrar();
    }

    @FXML
    private void recargar() {
        MainLayoutController.reiniciarTimer();
        filtrar();
    }

    @FXML
    private void imprimirReporte() {
        if (registrosMostrados == null || registrosMostrados.isEmpty()) return;

        var usuario = SessionManager.getInstance().getUsuario();
        var sede = SessionManager.getInstance().getEstacionamiento();

        String nombreGenerador = usuario != null
                ? usuario.getNombre() + " " + usuario.getApellidoPaterno() : "Sistema";
        String nombreSede = sede != null ? sede.getNombre() : "—";
        String rango = dpInicio.getValue() + " → " + dpFin.getValue();

        Reporte reporte = new Reporte("Reporte de Movimientos", nombreSede,
                nombreGenerador, rango, registrosMostrados);

        try {
            reporteService.generarReporteHTML(reporte);
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Error al generar reporte: " + e.getMessage());
            a.showAndWait();
        }
    }

    private void mostrarRegistros(List<Registro> registros) {
        vboxRegistros.getChildren().clear();

        if (registros.isEmpty()) {
            lblSinResultados.setVisible(true);
            lblSinResultados.setManaged(true);
            return;
        }

        lblSinResultados.setVisible(false);
        lblSinResultados.setManaged(false);

        for (Registro r : registros) {
            vboxRegistros.getChildren().add(crearItemRegistro(r));
        }
    }

    private HBox crearItemRegistro(Registro r) {
        HBox item = new HBox(16);
        item.getStyleClass().add("historial-item");
        item.setAlignment(Pos.CENTER_LEFT);

        boolean esCodigo = r.getCodigoAcceso() != null;
        boolean esPension = r.getTarifa() != null
                && r.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION;
        boolean activo = r.getEstadoRegistro() != null
                && r.getEstadoRegistro().getIdEstadoRegistro() == MisConstantes.REGISTRO_ACTIVO;

        // Ícono estado
        String iconoEstado = activo ? "🚗" : "✓";
        Label icono = new Label(iconoEstado);
        icono.setStyle("-fx-font-size:22px;");
        VBox iconoBox = new VBox(icono);
        iconoBox.setAlignment(Pos.CENTER);
        iconoBox.setPrefSize(50, 50);
        iconoBox.setStyle(esCodigo
                ? "-fx-background-color:#eff6ff;-fx-background-radius:10;-fx-border-color:#2563eb;-fx-border-radius:10;-fx-border-width:1;"
                : "-fx-background-color:#eff6ff;-fx-background-radius:10;");

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Encabezado: placa + badges
        HBox encabezado = new HBox(8);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        Label placa = new Label(r.getVehiculo() != null ? r.getVehiculo().getPlaca() : "—");
        placa.getStyleClass().add("historial-placa");
        encabezado.getChildren().add(placa);

        if (esPension) {
            Label vip = new Label("VIP");
            vip.getStyleClass().add("badge-vip");
            encabezado.getChildren().add(vip);
        }
        if (esCodigo) {
            Label badgeCod = new Label("CON CÓDIGO");
            badgeCod.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;"
                    + "-fx-font-size:10px;-fx-font-weight:bold;"
                    + "-fx-padding:2 6 2 6;-fx-background-radius:4;");
            encabezado.getChildren().add(badgeCod);
        }

        // Espacio
        String espacioText = r.getEspacio() != null
                ? "Espacio " + (r.getEspacio().getCodigo() != null
                        ? r.getEspacio().getCodigo() : "#" + r.getEspacio().getIdEspacio())
                : "—";
        Label espacio = new Label(espacioText);
        espacio.getStyleClass().add("historial-espacio");

        // Tiempo de entrada + duración
        String entradaText = r.getHoraEntrada() != null
                ? "⌚ " + r.getHoraEntrada().format(FMT) : "";
        if (r.getHoraSalida() != null && r.getHoraEntrada() != null) {
            long minutos = Duration.between(r.getHoraEntrada(), r.getHoraSalida()).toMinutes();
            long horas = minutos / 60;
            long mins = minutos % 60;
            String dur = horas > 0 ? horas + "h " + mins + "min" : mins + " min";
            entradaText += "  →  " + r.getHoraSalida().format(FMT) + "  (" + dur + ")";
        } else if (activo) {
            entradaText += "  (en curso)";
        }
        Label tiempo = new Label(entradaText);
        tiempo.getStyleClass().add("historial-tiempo");

        // Tipo tarifa
        String tipoTarifaText = r.getTarifa() != null
                ? r.getTarifa().getTipoTarifa().getDescripcion() : "";
        Label tipoTarifa = new Label(tipoTarifaText);
        tipoTarifa.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");

        info.getChildren().addAll(encabezado, espacio, tiempo, tipoTarifa);

        // Monto / indicador código
        VBox montoBox = new VBox(4);
        montoBox.setAlignment(Pos.CENTER_RIGHT);
        if (esCodigo && r.getMonto() == 0) {
            Label lblCod = new Label("Acceso\nincluido");
            lblCod.setStyle("-fx-font-weight:bold;-fx-font-size:12px;-fx-text-fill:#2563eb;-fx-text-alignment:right;");
            montoBox.getChildren().add(lblCod);
        } else if (r.getMonto() > 0) {
            Label monto = new Label(String.format("$%.2f", r.getMonto()));
            monto.setStyle("-fx-font-weight:bold;-fx-font-size:14px;-fx-text-fill:#16a34a;");
            montoBox.getChildren().add(monto);
        }

        // Badge estado
        Label badgeEstado;
        if (activo) {
            badgeEstado = new Label("⊙  En Estacionamiento");
            badgeEstado.getStyleClass().add("status-activo");
        } else {
            badgeEstado = new Label("✓  Finalizado");
            badgeEstado.getStyleClass().add("status-finalizado");
        }

        item.getChildren().addAll(iconoBox, info, montoBox, badgeEstado);
        return item;
    }
}
