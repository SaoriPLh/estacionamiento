package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Espacio;
import com.estacionamiento.modelo.Registro;
import com.estacionamiento.servicio.CalculoServicio;
import com.estacionamiento.servicio.RegistroServicio;
import com.estacionamiento.servicio.TicketService;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegistroSalidaController {

    @FXML private TextField txtBuscar;
    @FXML private VBox panelInfo;
    @FXML private Label lblPlaca;
    @FXML private Label lblModelo;
    @FXML private Label lblEspacio;
    @FXML private Label lblEntrada;
    @FXML private Label lblVigente;
    @FXML private Label lblTarifa;
    @FXML private Label lblMonto;
    @FXML private Label lblCodigo;
    @FXML private Label lblError;
    @FXML private Button btnConfirmar;

    private final RegistroServicio registroServicio = new RegistroServicio();
    private final CalculoServicio calculoServicio = new CalculoServicio();
    private final TicketService ticketService = new TicketService();

    private Registro registroActivo;
    private boolean confirmado = false;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {}

    public void setEspacioPreseleccionado(Espacio espacio) {
        txtBuscar.setText(espacio.getCodigo() != null ? espacio.getCodigo()
                : String.valueOf(espacio.getIdEspacio()));
        buscar();
    }

    @FXML
    private void buscar() {
        ocultarError();
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) { mostrarError("Ingresa una placa o número de espacio."); return; }

        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) { mostrarError("No hay sede activa."); return; }

        List<Registro> activos;
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Buscando registro activo en datos simulados — BD omitida.");
            registroActivo = MockData.getRegistroActivoParaSalida(termino);
            if (registroActivo != null) mostrarInfoRegistro(registroActivo);
            else {
                mostrarError("No se encontró un registro activo con ese dato.");
                panelInfo.setVisible(false);
                panelInfo.setManaged(false);
                btnConfirmar.setDisable(true);
            }
            return;
        }

        // Buscar en registros activos de hoy por placa o espacio
        activos = registroServicio.obtenerRegistrosFiltrados(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), null, sede.getIdEstacionamiento());

        registroActivo = activos.stream()
                .filter(r -> r.getEstadoRegistro() != null
                        && r.getEstadoRegistro().getIdEstadoRegistro() == 2) // ACTIVO
                .filter(r -> {
                    
                    String placa = r.getVehiculo() != null ? r.getVehiculo().getPlaca() : "";
                    String espacioCod = r.getEspacio() != null && r.getEspacio().getCodigo() != null
                            ? r.getEspacio().getCodigo() : "";
                    return placa.equalsIgnoreCase(termino) || espacioCod.equalsIgnoreCase(termino);
                })
                .findFirst().orElse(null);

                System.out.println("========== RESULTADO FINAL ==========");

if (registroActivo != null) {
    System.out.println("REGISTRO ENCONTRADO ID: " + registroActivo.getIdRegistro());

    System.out.println("PLACA: " +
            (registroActivo.getVehiculo() != null
                    ? registroActivo.getVehiculo().getPlaca()
                    : "NULL"));

    System.out.println("MODELO: " +
            (registroActivo.getVehiculo() != null
                    ? registroActivo.getVehiculo().getModelo()
                    : "NULL"));

    System.out.println("ESPACIO: " +
            (registroActivo.getEspacio() != null
                    ? registroActivo.getEspacio().getCodigo()
                    : "NULL"));

} else {
    System.out.println("REGISTRO ACTIVO = NULL");
}

        if (registroActivo == null) {
            mostrarError("No se encontró un registro activo con ese dato.");
            panelInfo.setVisible(false);
            panelInfo.setManaged(false);
            btnConfirmar.setDisable(true);
            return;
        }

        mostrarInfoRegistro(registroActivo);
    }

    private void mostrarInfoRegistro(Registro r) {
        System.out.println("Enrtrnado mostrarInfoRegistro()");
        lblPlaca.setText(r.getVehiculo() != null ? r.getVehiculo().getPlaca() : "—");

        String modelo = r.getVehiculo() != null && r.getVehiculo().getModelo() != null
                && !r.getVehiculo().getModelo().isBlank()
                ? r.getVehiculo().getModelo() : "—";
        lblModelo.setText(modelo);

        lblEspacio.setText(r.getEspacio() != null && r.getEspacio().getCodigo() != null
                ? r.getEspacio().getCodigo() : "—");
        lblEntrada.setText(r.getHoraEntrada() != null ? r.getHoraEntrada().format(FMT) : "—");

        if (r.getFecha_fin_plan() != null) {
            lblVigente.setText(r.getFecha_fin_plan().format(FMT));
        } else {
            lblVigente.setText("—");
        }

        lblTarifa.setText(r.getTarifa() != null
                ? r.getTarifa().getTipoTarifa().getDescripcion() + "  —  $" + r.getTarifa().getPrecio() + "/hr" : "—");

        boolean esCodigo = r.getCodigoAcceso() != null;
        if (esCodigo) {
            lblMonto.setText("$0.00  (acceso con código)");
            lblMonto.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#2563eb;");
            lblCodigo.setText("Este registro usa código de acceso — sin cobro adicional");
            lblCodigo.setVisible(true);
            lblCodigo.setManaged(true);
        } else {
            double monto = calculoServicio.calcularMontoTotal(r);
            lblMonto.setText(String.format("$%.2f", monto));
            lblMonto.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#16a34a;");
            lblCodigo.setVisible(false);
            lblCodigo.setManaged(false);
        }

        panelInfo.setVisible(true);
        panelInfo.setManaged(true);
        btnConfirmar.setDisable(false);
    }

    @FXML
    private void confirmar() {
        if (registroActivo == null) return;
        ocultarError();

        Timestamp ahora = Timestamp.valueOf(LocalDateTime.now());
        boolean ok = registroServicio.registrarSalida(registroActivo.getIdRegistro(), ahora);

        if (ok) {
            // Refrescar registro para ticket
            Registro actualizado = registroServicio.buscarRegistro(registroActivo.getIdRegistro());
            if (actualizado != null) {
                try { ticketService.imprimirSalida(actualizado); }
                catch (Exception e) {
    e.printStackTrace();
}
            }
            confirmado = true;
            cerrarVentana();
        } else {
            mostrarError("No se pudo registrar la salida. Intenta de nuevo.");
        }
    }

    @FXML
    private void cancelar() { cerrarVentana(); }

    private void cerrarVentana() {
        Stage stage = (Stage) txtBuscar.getScene().getWindow();
        stage.close();
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

    public boolean isConfirmado() { return confirmado; }
}
