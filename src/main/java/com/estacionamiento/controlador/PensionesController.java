package com.estacionamiento.controlador;

import com.estacionamiento.modelo.*;
import com.estacionamiento.servicio.PensionService;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.scene.control.TableRow;

public class PensionesController {

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TableView<Pension> tablaPensiones;
    @FXML private TableColumn<Pension, String> colCliente;
    @FXML private TableColumn<Pension, String> colPlaca;
    @FXML private TableColumn<Pension, String> colEspacio;
    @FXML private TableColumn<Pension, String> colTarifa;
    @FXML private TableColumn<Pension, String> colFechaInicio;
    @FXML private TableColumn<Pension, String> colFechaFin;
    @FXML private TableColumn<Pension, String> colEstado;
    @FXML private TableColumn<Pension, Void> colAcciones;
    @FXML private Label lblError;
    @FXML private Label lblAvisoPorVencer;

    private final PensionService pensionService = new PensionService();
    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    private void initialize() {
        cmbFiltroEstado.getItems().addAll("Todas", "Activas", "Suspendidas", "Vencidas");
        cmbFiltroEstado.setValue("Todas");
        cmbFiltroEstado.setOnAction(e -> recargar());
        configurarTabla();
        recargar();
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(d -> {
            Cliente c = d.getValue().getCliente();
            return new SimpleStringProperty(c != null
                    ? c.getNombre() + " " + c.getApellidoPaterno() : "—");
        });
        colPlaca.setCellValueFactory(d -> {
            Vehiculo v = d.getValue().getVehiculo();
            return new SimpleStringProperty(v != null ? v.getPlaca() : "—");
        });
        colEspacio.setCellValueFactory(d -> {
            Espacio e = d.getValue().getEspacio();
            return new SimpleStringProperty(e != null
                    ? (e.getCodigo() != null ? e.getCodigo() : "#" + e.getIdEspacio()) : "—");
        });
       colTarifa.setCellValueFactory(d -> {
    Tarifa t = d.getValue().getTarifa();
    if (t == null) return new SimpleStringProperty("—");

    double precioBase = t.getPrecio();
    // Ojo: asegúrate que getValorDescuento() no sea null si es Double objeto
    double descuento = t.getValorDescuento(); 
    
    // Si el descuento viene como porcentaje (ej: 10 para 10%), divídelo entre 100
    // Si viene como decimal (0.10), déjalo así.
    double precioFinal = precioBase - (precioBase * descuento);
    
    // Validación: Si el precio final es absurdo o menor a 0, mostramos el base
    double montoAMostrar = (precioFinal > 0 && precioFinal < precioBase) ? precioFinal : precioBase;

    return new SimpleStringProperty(String.format("$%.0f", montoAMostrar));
});
        colFechaInicio.setCellValueFactory(d -> {
            Date fi = d.getValue().getFechaInicio();
            return new SimpleStringProperty(fi != null ? FMT.format(fi) : "—");
        });
        colFechaFin.setCellValueFactory(d -> {
            Date ff = d.getValue().getFechaFin();
            return new SimpleStringProperty(ff != null ? FMT.format(ff) : "—");
        });
        colEstado.setCellValueFactory(d -> {
            EstadoPension ep = d.getValue().getEstadoPension();
            return new SimpleStringProperty(ep != null
                    ? MisConstantes.getNombreEstadoPension(ep.getIdEstadoPension()) : "—");
        });

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnRenovar   = new Button("Renovar");
            private final Button btnSuspender = new Button("Suspender");
            private final Button btnCancelar  = new Button("Cancelar");
            private final HBox box = new HBox(4, btnRenovar, btnSuspender, btnCancelar);
            {
                btnRenovar.getStyleClass().add("btn-secondary");
                btnRenovar.setMinWidth(80);
                btnSuspender.setStyle("-fx-background-color:#f59e0b;-fx-text-fill:white;-fx-background-radius:6;-fx-font-size:12px;");
                btnSuspender.setMinWidth(86);
                btnCancelar.getStyleClass().add("btn-danger");
                btnCancelar.setMinWidth(80);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Pension p = getTableView().getItems().get(getIndex());
                int estado = p.getEstadoPension() != null ? p.getEstadoPension().getIdEstadoPension() : 0;
                btnRenovar.setDisable(false);
                btnSuspender.setDisable(estado != MisConstantes.PENSION_ACTIVA);
                btnCancelar.setDisable(estado == MisConstantes.PENSION_VENCIDA);
                btnRenovar.setOnAction(e -> renovarPension(p));
                btnSuspender.setOnAction(e -> suspenderPension(p));
                btnCancelar.setOnAction(e -> cancelarPension(p));
                setGraphic(box);
            }
        });

        tablaPensiones.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Pension item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (esPorVencer(item)) {
                    setStyle("-fx-background-color:#fef3c7;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private boolean esPorVencer(Pension p) {
        if (p.getFechaFin() == null) return false;
        if (p.getEstadoPension() == null ||
            p.getEstadoPension().getIdEstadoPension() != MisConstantes.PENSION_ACTIVA) return false;
        Date hoy = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date limite = cal.getTime();
        return !p.getFechaFin().before(hoy) && !p.getFechaFin().after(limite);
    }

    @FXML
    private void recargar() {
        MainLayoutController.reiniciarTimer();
        ocultarError();
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;

        List<Pension> todas = pensionService.listarPorEstacionamiento(sede.getIdEstacionamiento());
        String filtro = cmbFiltroEstado.getValue();
        List<Pension> filtradas = todas.stream().filter(p -> {
            if ("Todas".equals(filtro)) return true;
            int id = p.getEstadoPension() != null ? p.getEstadoPension().getIdEstadoPension() : 0;
            return switch (filtro) {
                case "Activas"     -> id == MisConstantes.PENSION_ACTIVA;
                case "Suspendidas" -> id == MisConstantes.PENSION_SUSPENDIDA;
                case "Vencidas"    -> id == MisConstantes.PENSION_VENCIDA;
                default -> true;
            };
        }).toList();
        tablaPensiones.getItems().setAll(filtradas);

        long porVencer = todas.stream()
                .filter(this::esPorVencer)
                .count();
        if (porVencer > 0) {
            lblAvisoPorVencer.setText(
                    "Aviso: " + porVencer + " pension(es) vence(n) en los proximos 3 dias. Considera renovarlas.");
            lblAvisoPorVencer.setVisible(true);
            lblAvisoPorVencer.setManaged(true);
        } else {
            lblAvisoPorVencer.setVisible(false);
            lblAvisoPorVencer.setManaged(false);
        }
    }

    private void renovarPension(Pension p) {
        Calendar cal = Calendar.getInstance();
        if (p.getFechaFin() != null && p.getFechaFin().after(new Date())) {
            cal.setTime(p.getFechaFin());
        }
        cal.add(Calendar.MONTH, 1);
        try {
            pensionService.renovarPension(p.getIdPension(), cal.getTime());
            recargar();
        } catch (Exception e) {
            mostrarError("Error al renovar: " + e.getMessage());
        }
    }

    private void suspenderPension(Pension p) {
        String nombre = p.getCliente() != null ? p.getCliente().getNombre() : "este cliente";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Suspender la pensión de " + nombre + "?");
        confirm.setContentText("El espacio se mantendrá reservado.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    pensionService.suspenderPension(p.getIdPension());
                    recargar();
                } catch (Exception e) {
                    mostrarError("Error al suspender: " + e.getMessage());
                }
            }
        });
    }

    private void cancelarPension(Pension p) {
        String nombre = p.getCliente() != null ? p.getCliente().getNombre() : "este cliente";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Se liberará el espacio y la pensión quedará vencida.", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("¿Cancelar la pensión de " + nombre + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    pensionService.cancelarPension(p.getIdPension());
                    recargar();
                } catch (Exception e) {
                    mostrarError("Error al cancelar: " + e.getMessage());
                }
            }
        });
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}
