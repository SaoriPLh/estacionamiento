package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Cliente;
import com.estacionamiento.modelo.CodigoAcceso;
import com.estacionamiento.modelo.Tarifa;
import com.estacionamiento.servicio.ClienteService;
import com.estacionamiento.servicio.CodigoAccesoService;
import com.estacionamiento.servicio.TarifaService;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class FormularioClienteController {

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private VBox panelDatos;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private VBox panelCodigo;
    @FXML private Spinner<Integer> spnDias;
    @FXML private VBox panelCodigoGenerado;
    @FXML private Label lblCodigoGenerado;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

    private final ClienteService clienteService = new ClienteService();
    private final TarifaService tarifaService = new TarifaService();
    private final CodigoAccesoService codigoAccesoService = new CodigoAccesoService();

    private Cliente clienteGuardado = null;
    private boolean modoEdicion = false;

    @FXML
    private void initialize() {}

    public void setCliente(Cliente cliente) {
        modoEdicion = true;
        clienteGuardado = cliente;
        txtNombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "");
        txtApellidoP.setText(cliente.getApellidoPaterno() != null ? cliente.getApellidoPaterno() : "");
        txtApellidoM.setText(cliente.getApellidoMaterno() != null ? cliente.getApellidoMaterno() : "");
        txtTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        txtCorreo.setText(cliente.getCorreo() != null ? cliente.getCorreo() : "");
        btnGuardar.setText("Actualizar");
        panelCodigo.setVisible(true);
        panelCodigo.setManaged(true);
    }

    public void setModoSoloCodigo(Cliente cliente) {
        modoEdicion = true;
        clienteGuardado = cliente;
        // Ocultar formulario de datos personales — solo mostrar generador de código
        panelDatos.setVisible(false);
        panelDatos.setManaged(false);
        lblTitulo.setText("Generar Código de Acceso");
        lblSubtitulo.setText(cliente.getNombre() + " " + cliente.getApellidoPaterno());
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        panelCodigo.setVisible(true);
        panelCodigo.setManaged(true);
    }

    @FXML
    private void guardar() {
        ocultarError();

        String nombre = txtNombre.getText().trim();
        String apellidoP = txtApellidoP.getText().trim();

        if (nombre.isEmpty()) { mostrarError("El nombre es obligatorio."); return; }
        if (apellidoP.isEmpty()) { mostrarError("El apellido paterno es obligatorio."); return; }

        if (modoEdicion && clienteGuardado != null) {
            clienteGuardado.setNombre(nombre);
            clienteGuardado.setApellidoPaterno(apellidoP);
            clienteGuardado.setApellidoMaterno(txtApellidoM.getText().trim());
            clienteGuardado.setTelefono(txtTelefono.getText().trim());
            clienteGuardado.setCorreo(txtCorreo.getText().trim());
            if (SessionManager.MODO_DEMO) {
                System.out.println("[DEMO] Cliente actualizado: " + nombre + " " + apellidoP);
                btnGuardar.setDisable(true);
                return;
            }
            try {
                clienteService.actualizar(clienteGuardado);
                btnGuardar.setDisable(true);
            } catch (Exception e) {
                mostrarError("Error actualizando: " + e.getMessage());
            }
            return;
        }

        // Tarifa siempre es PENSION
        Tarifa tarifaPension = resolverTarifaPension();

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellidoPaterno(apellidoP);
        cliente.setApellidoMaterno(txtApellidoM.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setCorreo(txtCorreo.getText().trim());
        if (tarifaPension != null) cliente.setTarifa(tarifaPension);

        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Cliente registrado: " + nombre + " " + apellidoP + " — BD omitida.");
            clienteGuardado = cliente;
            clienteGuardado.setIdCliente(999);
            btnGuardar.setDisable(true);
            panelCodigo.setVisible(true);
            panelCodigo.setManaged(true);
            return;
        }

        try {
            Cliente guardado = clienteService.registrarCliente(cliente);
            if (guardado == null || guardado.getIdCliente() <= 0) {
                mostrarError("No se pudo guardar el cliente.");
                return;
            }
            clienteGuardado = guardado;
            btnGuardar.setDisable(true);
            panelCodigo.setVisible(true);
            panelCodigo.setManaged(true);
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void generarCodigo() {
        if (clienteGuardado == null) { mostrarError("Guarda el cliente primero."); return; }
        ocultarError();

        if (SessionManager.MODO_DEMO) {
            String codigoDemo = "LOGIC-" + (10000000 + (int)(Math.random() * 89999999));
            System.out.println("[DEMO] Código generado: " + codigoDemo);
            lblCodigoGenerado.setText(codigoDemo);
            panelCodigoGenerado.setVisible(true);
            panelCodigoGenerado.setManaged(true);
            return;
        }

        try {
            int dias = spnDias.getValue();
            Date inicio = Date.valueOf(LocalDate.now());
            Date fin = Date.valueOf(LocalDate.now().plusDays(dias));
            var sede = SessionManager.getInstance().getEstacionamiento();

            CodigoAcceso codigo = codigoAccesoService.asignarCodigo(
                    clienteGuardado.getIdCliente(), sede.getIdEstacionamiento(), inicio, fin);

            lblCodigoGenerado.setText(codigo.getCodigo());
            panelCodigoGenerado.setVisible(true);
            panelCodigoGenerado.setManaged(true);
        } catch (Exception e) {
            mostrarError("Error generando código: " + e.getMessage());
        }
    }

    private Tarifa resolverTarifaPension() {
        try {
            var sede = SessionManager.getInstance().getEstacionamiento();
            if (sede == null) return null;
            List<Tarifa> tarifas = SessionManager.MODO_DEMO
                    ? MockData.getTarifas()
                    : tarifaService.obtenerTarifasPorSede(sede.getIdEstacionamiento());
            return tarifas.stream()
                    .filter(t -> t.getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
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
}
