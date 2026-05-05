package com.estacionamiento.controlador;

import com.estacionamiento.dao.CodigoAccesoDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.servicio.*;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class RegistroEntradaController {

    @FXML private TextField txtPlaca;
    @FXML private ComboBox<Marca> cmbMarca;
    @FXML private TextField txtModelo;
    @FXML private ComboBox<Espacio> cmbEspacio;
    @FXML private ComboBox<Tarifa> cmbTarifa;
    @FXML private Spinner<Integer> spnHoras;
    @FXML private VBox panelCodigoAcceso;
    @FXML private TextField txtCodigoAcceso;
    @FXML private Label lblCodigoStatus;
    @FXML private VBox panelCliente;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblError;

    private final RegistroServicio registroServicio = new RegistroServicio();
    private final EspacioService espacioService = new EspacioService();
    private final TarifaService tarifaService = new TarifaService();
    private final CodigoAccesoService codigoAccesoService = new CodigoAccesoService();
    private final ClienteService clienteService = new ClienteService();
    private final TicketService ticketService = new TicketService();

    private boolean confirmado = false;
    private boolean espacioPension = false;

    @FXML
    private void initialize() {
        cargarMarcas();
        cargarEspaciosDisponibles();
        cargarTarifas();
        cargarClientes();
        configurarListeners();
    }

    // ── Preselección desde el Dashboard ──────────────────────────────────────
    public void setEspacioPreseleccionado(Espacio espacio) {
        for (Espacio e : cmbEspacio.getItems()) {
            if (e.getIdEspacio() == espacio.getIdEspacio()) {
                cmbEspacio.setValue(e);
                return;
            }
        }
        cmbEspacio.getItems().add(espacio);
        cmbEspacio.setValue(espacio);
    }

    // ── Carga de combos ───────────────────────────────────────────────────────
    private void cargarMarcas() {
        try {
            List<Marca> marcas = SessionManager.MODO_DEMO
                    ? MockData.getMarcas()
                    : new com.estacionamiento.dao.MarcaDAO().listarTodas();
            cmbMarca.setConverter(new StringConverter<>() {
                public String toString(Marca m) { return m != null ? m.getNombre() : ""; }
                public Marca fromString(String s) { return null; }
            });
            cmbMarca.getItems().setAll(marcas);
        } catch (Exception ignored) {}
    }

    private void cargarEspaciosDisponibles() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;

        List<Espacio> todos = SessionManager.MODO_DEMO
                ? MockData.getEspacios()
                : espacioService.listarEspacios(sede.getIdEstacionamiento());

        cmbEspacio.setConverter(new StringConverter<>() {
            public String toString(Espacio e) {
                if (e == null) return "";
                String tag = e.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_PENSION
                        ? " [PENSIÓN]" : "";
                return (e.getCodigo() != null ? e.getCodigo() : "#" + e.getIdEspacio()) + tag;
            }
            public Espacio fromString(String s) { return null; }
        });

        for (Espacio esp : todos) {
            int estado = esp.getEstadoEspacio().getIdEstadoEspacio();
            if (estado == MisConstantes.ESPACIO_DISPONIBLE || estado == MisConstantes.ESPACIO_PENSION) {
                cmbEspacio.getItems().add(esp);
            }
        }
    }

    private void cargarTarifas() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;

        List<Tarifa> tarifas = SessionManager.MODO_DEMO
                ? MockData.getTarifas()
                : tarifaService.obtenerTarifasPorSede(sede.getIdEstacionamiento());

        cmbTarifa.setConverter(new StringConverter<>() {
            public String toString(Tarifa t) {
                if (t == null) return "";
                return t.getTipoTarifa().getDescripcion() + "  —  $" + String.format("%.0f", t.getPrecio())
                        + " (" + t.getTipoCobro().getNombre() + ")";
            }
            public Tarifa fromString(String s) { return null; }
        });
        cmbTarifa.getItems().setAll(tarifas);
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = SessionManager.MODO_DEMO
                    ? MockData.getClientes()
                    : clienteService.listarTodos();
            cmbCliente.setConverter(new StringConverter<>() {
                public String toString(Cliente c) {
                    return c != null ? c.getNombre() + " " + c.getApellidoPaterno() : "";
                }
                public Cliente fromString(String s) { return null; }
            });
            cmbCliente.getItems().setAll(clientes);
        } catch (Exception ignored) {}
    }

    // ── Listeners reactivos ───────────────────────────────────────────────────
    private void configurarListeners() {
        cmbTarifa.valueProperty().addListener((obs, old, tarifa) -> {
            if (tarifa == null) return;
            boolean esPorHora = tarifa.getTipoCobro().getIdTipoCobro() == MisConstantes.TIPO_COBRO_HORA;
            boolean esTarifaPension = tarifa.getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION;
            spnHoras.setVisible(esPorHora && !espacioPension);
            spnHoras.setManaged(esPorHora && !espacioPension);
            panelCliente.setVisible(esTarifaPension && !espacioPension);
            panelCliente.setManaged(esTarifaPension && !espacioPension);
        });

        cmbEspacio.valueProperty().addListener((obs, old, espacio) -> {
            if (espacio == null) return;
            boolean esPension = espacio.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_PENSION;
            espacioPension = esPension;
            panelCodigoAcceso.setVisible(esPension);
            panelCodigoAcceso.setManaged(esPension);
            if (esPension) {
                autoSeleccionarTarifaPension();
                cmbTarifa.setDisable(true);
                spnHoras.setVisible(false);
                spnHoras.setManaged(false);
                panelCliente.setVisible(false);
                panelCliente.setManaged(false);
            } else {
                cmbTarifa.setDisable(false);
            }
        });

        cmbCliente.valueProperty().addListener((obs, old, cliente) -> {
            if (cliente == null) return;
            autoCompletarCodigo(cliente);
        });
    }

    private void autoCompletarCodigo(Cliente cliente) {
        if (SessionManager.MODO_DEMO) return;
        try {
            CodigoAcceso activo = codigoAccesoService.obtenerCodigoActivo(cliente.getIdCliente());
            if (activo != null) {
                txtCodigoAcceso.setText(activo.getCodigo());
                lblCodigoStatus.setText("Código activo encontrado");
                lblCodigoStatus.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:bold;");
            } else {
                txtCodigoAcceso.clear();
                lblCodigoStatus.setText("Sin código activo — genéralo desde la pantalla de Clientes");
                lblCodigoStatus.setStyle("-fx-text-fill:#92400e; -fx-font-weight:bold;");
            }
        } catch (Exception ignored) {}
    }

    private void autoSeleccionarTarifaPension() {
        for (Tarifa t : cmbTarifa.getItems()) {
            if (t.getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION) {
                cmbTarifa.setValue(t);
                return;
            }
        }
    }

    // ── Nueva marca ───────────────────────────────────────────────────────────
    @FXML
    private void abrirNuevaMarca() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nueva Marca");
        dialogo.setHeaderText("Agregar nueva marca de vehículo");
        dialogo.setContentText("Nombre:");
        dialogo.showAndWait().ifPresent(nombre -> {
            String nombreTrim = nombre.trim();
            if (nombreTrim.isEmpty()) return;
            if (SessionManager.MODO_DEMO) {
                Marca nueva = new Marca();
                nueva.setNombre(nombreTrim.toUpperCase());
                nueva.setIdMarca(-1);
                cmbMarca.getItems().add(nueva);
                cmbMarca.setValue(nueva);
                return;
            }
            Marca m = new Marca();
            m.setNombre(nombreTrim);
            Marca guardada = new com.estacionamiento.dao.MarcaDAO().insertar(m);
            cargarMarcas();
            int id = guardada.getIdMarca();
            for (Marca item : cmbMarca.getItems()) {
                if (item.getIdMarca() == id) { cmbMarca.setValue(item); break; }
            }
        });
    }

    // ── Abrir formulario de cliente nuevo ─────────────────────────────────────
    @FXML
    private void abrirFormularioCliente() {
        try {
            FXMLLoader loader = NavegadorUI.crearLoader(
                    "/com/estacionamiento/vista/modales/FormularioCliente.fxml");
            Parent root = loader.load();
            Stage modal = new Stage();
            modal.setTitle("Nuevo Cliente");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(NavegadorUI.getPrimaryStage());
            Scene scene = new Scene(root);
            NavegadorUI.aplicarCss(scene);
            modal.setScene(scene);
            modal.showAndWait();
            cargarClientes();
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de cliente.");
        }
    }

    // ── Confirmar entrada ─────────────────────────────────────────────────────
    @FXML
    private void confirmar() {
        ocultarError();

        String placa = txtPlaca.getText().trim().toUpperCase();
        Espacio espacio = cmbEspacio.getValue();
        Tarifa tarifa = cmbTarifa.getValue();

        if (placa.isEmpty()) { mostrarError("La placa es obligatoria."); return; }
        if (espacio == null) { mostrarError("Selecciona un espacio."); return; }
        if (tarifa == null)  { mostrarError("Selecciona una tarifa."); return; }

        boolean esPension = espacio.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_PENSION;
        boolean esTarifaPension = tarifa.getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION;

        CodigoAcceso codigoObj = null;
        if (esPension) {
            String codigoStr = txtCodigoAcceso.getText().trim();
            if (!codigoStr.isEmpty()) {
                if (SessionManager.MODO_DEMO) {
                    lblCodigoStatus.setText("✓ Código válido (demo)");
                    lblCodigoStatus.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:bold;");
                } else {
                    if (!codigoAccesoService.validarAcceso(codigoStr,
                            SessionManager.getInstance().getEstacionamiento().getIdEstacionamiento())) {
                        lblCodigoStatus.setText("✗ Código inválido o vencido");
                        lblCodigoStatus.setStyle("-fx-text-fill:#dc2626; -fx-font-weight:bold;");
                        mostrarError("El código de acceso es inválido, vencido o ya fue cancelado.");
                        return;
                    }
                    lblCodigoStatus.setText("✓ Código válido");
                    lblCodigoStatus.setStyle("-fx-text-fill:#16a34a; -fx-font-weight:bold;");
                    try {
                        codigoObj = new CodigoAccesoDAO().buscarPorCodigo(codigoStr);
                    } catch (Exception ignored) {}
                }
            }
        }

        Marca marca = cmbMarca.getValue();
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setModelo(txtModelo.getText().trim());
        if (marca != null) vehiculo.setMarca(marca);

        if (esPension || esTarifaPension) {
            Cliente cliente = cmbCliente.getValue();
            if (cliente != null) vehiculo.setCliente(cliente);
        }

        Persona empleado = SessionManager.getInstance().getUsuario();
        Registro registro = new Registro();
        registro.setVehiculo(vehiculo);
        registro.setEspacio(espacio);
        registro.setTarifa(tarifa);
        registro.setPersona(empleado);
        registro.setCodigoAcceso(codigoObj);

        long horas = spnHoras.getValue();

        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Entrada registrada: placa=" + placa
                    + ", espacio=" + espacio.getCodigo()
                    + ", tarifa=" + tarifa.getTipoTarifa().getDescripcion());
            confirmado = true;
            cerrarVentana();
            return;
        }

        try {
            Registro resultado = registroServicio.registrarEntrada(registro, horas);
            if (resultado != null) {
                try {
                    if (esTarifaPension && codigoObj == null) {
                        ticketService.imprimirPension(resultado);
                    } else {
                        ticketService.imprimirEntrada(resultado);
                    }
                } catch (Exception ignored) {}
                confirmado = true;
                cerrarVentana();
            } else {
                mostrarError("No se pudo registrar la entrada. Verifica que el espacio esté disponible.");
            }
        } catch (Exception e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    @FXML private void cancelar() { cerrarVentana(); }

    private void cerrarVentana() {
        Stage stage = (Stage) txtPlaca.getScene().getWindow();
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
