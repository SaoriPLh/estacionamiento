package com.estacionamiento.controlador;

import com.estacionamiento.dao.CiudadDAO;
import com.estacionamiento.dao.CodigoAccesoDAO;
import com.estacionamiento.dao.DireccionDAO;
import com.estacionamiento.dao.EspacioDAO;
import com.estacionamiento.dao.EstacionamientoDAO;
import com.estacionamiento.modelo.*;
import com.estacionamiento.servicio.CodigoAccesoService;
import com.estacionamiento.servicio.PersonalService;
import com.estacionamiento.servicio.TarifaService;
import com.estacionamiento.util.ConfiguracionLocal;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ConfiguracionController {

    private static final Logger logger = Logger.getLogger(ConfiguracionController.class.getName());

    // Personal
    @FXML private TableView<Persona> tablaPersonal;
    @FXML private TableColumn<Persona, String> colPNombre;
    @FXML private TableColumn<Persona, String> colPUsername;
    @FXML private TableColumn<Persona, String> colPRol;
    @FXML private TableColumn<Persona, String> colPSalario;
    @FXML private TableColumn<Persona, String> colPEstado;
    @FXML private TableColumn<Persona, Void> colPAcciones;

    // Tarifas
    @FXML private TableView<Tarifa> tablaTarifas;
    @FXML private TableColumn<Tarifa, String> colTTipo;
    @FXML private TableColumn<Tarifa, String> colTCobro;
    @FXML private TableColumn<Tarifa, String> colTPrecio;
    @FXML private TableColumn<Tarifa, String> colTDescuento;
    @FXML private TableColumn<Tarifa, Void> colTAcciones;

    // Códigos
    @FXML private TextField txtBuscarCodigo;
    @FXML private TableView<CodigoAcceso> tablaCodigos;
    @FXML private TableColumn<CodigoAcceso, String> colCCodigo;
    @FXML private TableColumn<CodigoAcceso, String> colCCliente;
    @FXML private TableColumn<CodigoAcceso, String> colCEstado;
    @FXML private TableColumn<CodigoAcceso, String> colCFechaFin;
    @FXML private TableColumn<CodigoAcceso, Void> colCAcciones;

    // Form tarifa
    @FXML private VBox panelFormTarifa;
    @FXML private Label lblFormTarifaTitulo;
    @FXML private ComboBox<TipoTarifa> cmbTipoTarifa;
    @FXML private ComboBox<TipoCobro> cmbTipoCobro;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDescuento;
    @FXML private VBox panelConvenio;
    @FXML private ComboBox<UnidadDescuento> cmbUnidadDescuento;
    @FXML private TextField txtCantidadDescuento;
    @FXML private Label lblErrorTarifa;

    // Form empleado
    @FXML private VBox panelFormEmpleado;
    @FXML private Label lblFormEmpleadoTitulo;
    @FXML private TextField txtENombre;
    @FXML private TextField txtEApellidoP;
    @FXML private TextField txtEApellidoM;
    @FXML private TextField txtEUsername;
    @FXML private PasswordField txtEPassword;
    @FXML private PasswordField txtEConfirmPassword;
    @FXML private Label lblEPasswordHint;
    @FXML private TextField txtESalario;
    @FXML private VBox panelSedesCheck;
    @FXML private Label lblErrorEmpleado;

    // Impresoras
    @FXML private Label lblImpresoraTickets;
    @FXML private Label lblImpresoraReportes;

    // Espacios
    @FXML private TableView<Espacio> tablaEspacios;
    @FXML private TableColumn<Espacio, String> colECodigo;
    @FXML private TableColumn<Espacio, String> colETipo;
    @FXML private TableColumn<Espacio, String> colEEstado;
    @FXML private TableColumn<Espacio, Void> colEAcciones;
    @FXML private VBox panelFormEspacio;
    @FXML private TextField txtEspacioCodigo;
    @FXML private ComboBox<TipoEspacio> cmbEspacioTipo;
    @FXML private Label lblErrorEspacio;

    // Sedes
    @FXML private TableView<Estacionamiento> tablaSedes;
    @FXML private TableColumn<Estacionamiento, String> colSNombre;
    @FXML private TableColumn<Estacionamiento, String> colSDireccion;
    @FXML private TableColumn<Estacionamiento, String> colSEstado;
    @FXML private TableColumn<Estacionamiento, Void> colSAcciones;
    @FXML private VBox panelFormSede;
    @FXML private TextField txtSedeNombre;
    @FXML private TextField txtSedeCalle;
    @FXML private TextField txtNumeroExterior;
    @FXML private TextField txtCodigoPostal;
    @FXML private ComboBox<Ciudad> cmbSedeCiudad;
    @FXML private Label lblErrorSede;

    private final PersonalService personalService = new PersonalService();
    private final TarifaService tarifaService = new TarifaService();
    private final CodigoAccesoService codigoService = new CodigoAccesoService();
    private final CodigoAccesoDAO codigoDAO = new CodigoAccesoDAO();
    private final EstacionamientoDAO estDAO = new EstacionamientoDAO();
    private final EspacioDAO espacioDAO = new EspacioDAO();
    private final CiudadDAO ciudadDAO = new CiudadDAO();
    private final DireccionDAO direccionDAO = new DireccionDAO();

    private Tarifa tarifaEditando = null;
    private Persona personaEditando = null;
    private List<Estacionamiento> todasLasSedes = new ArrayList<>();
    private final ObservableList<CodigoAcceso> listaCodigosCompleta = FXCollections.observableArrayList();
    private static final SimpleDateFormat FMT_FECHA = new SimpleDateFormat("yyyy-MM-dd");

  @FXML
private void initialize() {
    configurarTablaPersonal();
    configurarTablaTarifas();
    configurarTablaCodigos();
    configurarTablaEspacios();
    configurarTablaSedes();
    inicializarCombos();
    inicializarComboEspacioTipo();
    inicializarComboSedeCiudad();
    configurarListenerConvenio();
    recargarPersonal();
    recargarTarifas();
    recargarCodigos();
    recargarEspacios();
    recargarSedes();
    cargarImpresoras();

    txtCantidadDescuento.textProperty().addListener((obs, oldVal, newVal) -> actualizarPrecioEstimado());
    cmbUnidadDescuento.valueProperty().addListener((obs, oldVal, newVal) -> actualizarPrecioEstimado());
}

    // ===== PERSONAL =====

    private void configurarTablaPersonal() {
        colPNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre() + " " + d.getValue().getApellidoPaterno()));
        colPUsername.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsername()));
        colPRol.setCellValueFactory(d ->
                new SimpleStringProperty(MisConstantes.getNombreRol(d.getValue().getRol().getIdRol())));
        colPSalario.setCellValueFactory(d -> {
            Double sal = d.getValue().getSalario();
            return new SimpleStringProperty(sal != null ? String.format("$%.0f", sal) : "—");
        });
        colPEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPermisos() != null
                        && !d.getValue().getPermisos().isEmpty() ? "Activo" : "Sin permisos"));

        colPAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnCambio   = new Button("Pedir Cambio Contraseña");
            private final Button btnBaja     = new Button("Dar de baja");
            private final HBox   box         = new HBox(4, btnEditar, btnCambio, btnBaja);
            {
                btnEditar.getStyleClass().add("btn-secondary");
                btnCambio.getStyleClass().add("btn-outline");
                btnBaja.getStyleClass().add("btn-danger");
                btnEditar.setMinWidth(Region.USE_PREF_SIZE);
    btnCambio.setMinWidth(Region.USE_PREF_SIZE);
    btnBaja.setMinWidth(Region.USE_PREF_SIZE);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Persona p = getTableView().getItems().get(getIndex());
                btnEditar.setOnAction(e -> ConfiguracionController.this.editarEmpleado(p));
                btnCambio.setOnAction(e -> ConfiguracionController.this.solicitarCambioEmpleado(p));
                btnBaja.setOnAction(e -> ConfiguracionController.this.darDeBajaPersonal(p));
                setGraphic(box);
            }
        });
    }

    @FXML private void recargarPersonal() {
        List<Persona> personal;
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Personal simulado — BD omitida.");
            personal = MockData.getPersonal();
        } else {
            personal = personalService.obtenerPersonalDeSedeActual();
        }
        tablaPersonal.getItems().setAll(personal);
    }

    @FXML private void nuevoEmpleado() {
        personaEditando = null;
        lblFormEmpleadoTitulo.setText("Nuevo Empleado");
        limpiarFormEmpleado();
        cargarSedesEnForm();
        panelFormEmpleado.setVisible(true);
        panelFormEmpleado.setManaged(true);
    }

    private void actualizarPrecioEstimado() {
    // Solo calcular si es tipo Convenio
    if (cmbTipoTarifa.getValue() == null || 
        cmbTipoTarifa.getValue().getIdTipoTarifa() != MisConstantes.TARIFA_CONVENIO) {
        return;
    }

    try {
        var sedeActual = SessionManager.getInstance().getEstacionamiento();
        if (sedeActual == null) return;

        // 1. Obtener Tarifa Normal de la sede mediante el servicio
        Tarifa tarifa = tarifaService.obtenerTarifaNormalPorSede(sedeActual.getIdEstacionamiento());
        double precioNormal = tarifa.getPrecio();

        // 2. Obtener factores de los inputs
        UnidadDescuento unidad = cmbUnidadDescuento.getValue();
        String cantidadStr = txtCantidadDescuento.getText().trim();

        if (unidad != null && !cantidadStr.isEmpty()) {
            int cantidad = Integer.parseInt(cantidadStr);
            int factorMinutos = unidad.getFactorConversionMinutos(); // Ej. Hora = 60, Día = 1440

            // 3. Aplicar fórmula: (Normal * Factor * Cantidad) / 60
            double precioFinal = (precioNormal * factorMinutos * cantidad) / 60.0;

            // 4. Actualizar UI
            txtPrecio.setText(String.format("%.2f", precioFinal));
            ocultarErrorTarifa();
        }
    } catch (NumberFormatException e) {
        // Manejo de robustez: si el usuario borra el campo o pone letras
        txtPrecio.setText("0.00");
    } catch (Exception e) {
        mostrarErrorTarifa("Error al obtener tarifa base de la sede.");
    }
}
    

    private void cargarSedesEnForm() {
        panelSedesCheck.getChildren().clear();
        todasLasSedes.clear();

        if (SessionManager.MODO_DEMO) {
            Estacionamiento demo = MockData.getSede();
            todasLasSedes.add(demo);
            CheckBox cb = new CheckBox(demo.getNombre());
            cb.setSelected(true);
            cb.setUserData(demo);
            panelSedesCheck.getChildren().add(cb);
            return;
        }

        var empresa = SessionManager.getInstance().getEmpresa();
        if (empresa == null) return;

        List<Estacionamiento> sedes = estDAO.listarPorEmpresa(empresa.getIdEmpresa());
        var sedeActual = SessionManager.getInstance().getEstacionamiento();
        for (Estacionamiento s : sedes) {
            todasLasSedes.add(s);
            CheckBox cb = new CheckBox(s.getNombre());
            cb.setUserData(s);
            if (sedeActual != null && s.getIdEstacionamiento() == sedeActual.getIdEstacionamiento())
                cb.setSelected(true);
            panelSedesCheck.getChildren().add(cb);
        }
    }

    @FXML private void guardarEmpleado() {
        ocultarErrorEmpleado();
        String nombre    = txtENombre.getText().trim();
        String apellidoP = txtEApellidoP.getText().trim();
        String username  = txtEUsername.getText().trim();
        String password  = txtEPassword.getText();
        String confirm   = txtEConfirmPassword != null ? txtEConfirmPassword.getText() : "";

        if (nombre.isEmpty() || apellidoP.isEmpty()) {
            mostrarErrorEmpleado("Nombre y Apellido Paterno son obligatorios.");
            return;
        }

        Double salario = null;
        try {
            if (!txtESalario.getText().isBlank())
                salario = Double.parseDouble(txtESalario.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErrorEmpleado("El salario debe ser un numero valido.");
            return;
        }

        List<Estacionamiento> sedesSeleccionadas = new ArrayList<>();
        for (var node : panelSedesCheck.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                sedesSeleccionadas.add((Estacionamiento) cb.getUserData());
            }
        }
        if (sedesSeleccionadas.isEmpty()) {
            mostrarErrorEmpleado("Selecciona al menos una sede.");
            return;
        }

        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Empleado guardado: " + nombre);
            cancelarFormEmpleado();
            return;
        }

        // ── MODO EDICION ──────────────────────────────────────────────────────
        if (personaEditando != null) {
            if (!password.isEmpty()) {
                if (!password.equals(confirm)) {
                    mostrarErrorEmpleado("Las contrasenas no coinciden.");
                    return;
                }
                // Cambio de contrasena opcional al editar
                new com.estacionamiento.servicio.LoginServicio()
                        .cambiarContraseña(personaEditando.getIdPersona(), password);
            }
            boolean ok = personalService.actualizarEmpleado(
                    personaEditando.getIdPersona(), nombre, apellidoP,
                    txtEApellidoM.getText().trim(), salario, sedesSeleccionadas);
            if (ok) { cancelarFormEmpleado(); recargarPersonal(); }
            else    { mostrarErrorEmpleado("No se pudo actualizar el empleado."); }
            return;
        }

        // ── MODO NUEVO ────────────────────────────────────────────────────────
        if (username.isEmpty() || password.isEmpty()) {
            mostrarErrorEmpleado("Usuario y Contrasena son obligatorios al crear un empleado.");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarErrorEmpleado("Las contrasenas no coinciden.");
            return;
        }

        var empresa = SessionManager.getInstance().getEmpresa();
        Rol rol = new Rol(); rol.setIdRol(MisConstantes.ROL_EMPLEADO);

        Persona nuevo = personalService.construirPersonal(empresa, rol, nombre, apellidoP,
                txtEApellidoM.getText().trim(), username, password, false, salario);

        boolean ok = personalService.añadirEmpleadoConPermisos(nuevo, sedesSeleccionadas);
        if (ok) {
            cancelarFormEmpleado();
            recargarPersonal();
        } else {
            mostrarErrorEmpleado("No se pudo guardar el empleado. El usuario puede estar duplicado.");
        }
    }

    private void darDeBajaPersonal(Persona p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Dar de baja");
        confirm.setHeaderText("¿Revocar acceso a " + p.getNombre() + " " + p.getApellidoPaterno() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                personalService.darDeBajaAccesoPersonal(p.getIdPersona());
                recargarPersonal();
            }
        });
    }

    private void editarEmpleado(Persona p) {
        personaEditando = p;
        lblFormEmpleadoTitulo.setText("Editar Empleado");
        txtENombre.setText(p.getNombre());
        txtEApellidoP.setText(p.getApellidoPaterno());
        txtEApellidoM.setText(p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
        txtEUsername.setText(p.getUsername());
        txtEUsername.setDisable(true);
        txtESalario.setText(p.getSalario() != null ? String.valueOf(p.getSalario()) : "");
        txtEPassword.clear();
        txtEConfirmPassword.clear();
        if (lblEPasswordHint != null) lblEPasswordHint.setText("Contrasena (opcional)");
        cargarSedesEnForm();
        panelFormEmpleado.setVisible(true);
        panelFormEmpleado.setManaged(true);
        ocultarErrorEmpleado();
    }

    private void solicitarCambioEmpleado(Persona p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Solicitar cambio de contrasena");
        confirm.setHeaderText("¿Forzar cambio de contrasena a " + p.getNombre() + " " + p.getApellidoPaterno() + "?");
        confirm.setContentText("El empleado debera cambiar su contrasena en el proximo inicio de sesion.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = personalService.solicitarCambioContraseña(p.getIdPersona());
                Alert info = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                info.setHeaderText(ok ? "Cambio solicitado correctamente." : "No se pudo solicitar el cambio.");
                info.show();
            }
        });
    }

    // ===== IMPRESORAS =====

    private void cargarImpresoras() {
        if (lblImpresoraTickets == null || lblImpresoraReportes == null) return;
        String t = ConfiguracionLocal.getImpresora("printer.tickets");
        lblImpresoraTickets.setText(t != null && !t.isBlank() ? t : "No configurada");
        String r = ConfiguracionLocal.getImpresora("printer.reportes");
        lblImpresoraReportes.setText(r != null && !r.isBlank() ? r : "No configurada");
    }

    @FXML private void eliminarImpresoraTickets() {
        ConfiguracionLocal.eliminarImpresora("printer.tickets");
        cargarImpresoras();
    }

    @FXML private void eliminarImpresoraReportes() {
        ConfiguracionLocal.eliminarImpresora("printer.reportes");
        cargarImpresoras();
    }

    @FXML private void cancelarFormEmpleado() {
        panelFormEmpleado.setVisible(false);
        panelFormEmpleado.setManaged(false);
        personaEditando = null;
        limpiarFormEmpleado();
    }

    private void limpiarFormEmpleado() {
        txtENombre.clear(); txtEApellidoP.clear(); txtEApellidoM.clear();
        txtEUsername.clear(); txtEUsername.setDisable(false);
        txtEPassword.clear();
        if (txtEConfirmPassword != null) txtEConfirmPassword.clear();
        if (lblEPasswordHint != null) lblEPasswordHint.setText("Contrasena *");
        txtESalario.clear();
        panelSedesCheck.getChildren().clear();
        ocultarErrorEmpleado();
    }

    // ===== TARIFAS =====

    private void configurarTablaTarifas() {
        colTTipo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTipoTarifa().getDescripcion()));
        colTCobro.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTipoCobro().getNombre()));
        colTPrecio.setCellValueFactory(d ->
                new SimpleStringProperty("$" + d.getValue().getPrecio()));
        colTDescuento.setCellValueFactory(d -> {
            Tarifa t = d.getValue();
            if (t.getUnidadDescuento() != null && t.getUnidadDescuento().getTipoUnidad() != null) {
                String unidad = t.getUnidadDescuento().getTipoUnidad();
                return new SimpleStringProperty(t.getCantidad_descuento() + " " + unidad + " gratis");
            }
            Double desc = t.getValorDescuento();
            return new SimpleStringProperty(desc != null && desc > 0
                    ? String.format("%.0f%%", desc * 100) : "—");
        });
        colTAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnEliminar);
            {
                 box.setAlignment(Pos.CENTER);
                 box.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("btn-secondary");
                btnEliminar.getStyleClass().add("btn-danger");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Tarifa t = getTableView().getItems().get(getIndex());
                btnEditar.setOnAction(e -> editarTarifa(t));
                btnEliminar.setOnAction(e -> eliminarTarifa(t));
                setGraphic(box);
            }
        });
    }

    @FXML private void recargarTarifas() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Tarifas simuladas — BD omitida.");
            tablaTarifas.getItems().setAll(MockData.getTarifas());
        } else {
            tablaTarifas.getItems().setAll(tarifaService.obtenerTarifasPorSede(sede.getIdEstacionamiento()));
        }
    }

    @FXML private void nuevaTarifa() {
        tarifaEditando = null;
        lblFormTarifaTitulo.setText("Nueva Tarifa");
        txtPrecio.clear(); txtDescuento.clear(); txtCantidadDescuento.clear();
        cmbTipoTarifa.setValue(null); cmbTipoCobro.setValue(null); cmbUnidadDescuento.setValue(null);
        panelConvenio.setVisible(false); panelConvenio.setManaged(false);
        panelFormTarifa.setVisible(true);
        panelFormTarifa.setManaged(true);
    }

    private void editarTarifa(Tarifa t) {
        tarifaEditando = t;
        lblFormTarifaTitulo.setText("Editar Tarifa");
        txtPrecio.setText(String.valueOf(t.getPrecio()));
        txtDescuento.setText(t.getValorDescuento() != null ? String.valueOf(t.getValorDescuento()) : "");
        boolean esConvenio = t.getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_CONVENIO;
        panelConvenio.setVisible(esConvenio); panelConvenio.setManaged(esConvenio);
        if (esConvenio) {
            cmbUnidadDescuento.setValue(t.getUnidadDescuento());
            txtCantidadDescuento.setText(String.valueOf(t.getCantidad_descuento()));
        }
        panelFormTarifa.setVisible(true);
        panelFormTarifa.setManaged(true);
    }

    @FXML private void guardarTarifa() {
        ocultarErrorTarifa();
        String precioStr = txtPrecio.getText().trim();
        if (precioStr.isEmpty()) { mostrarErrorTarifa("El precio es obligatorio."); return; }

        double precio;
        try { precio = Double.parseDouble(precioStr); }
        catch (NumberFormatException e) { mostrarErrorTarifa("Precio inválido."); return; }

        boolean esConvenio = cmbTipoTarifa.getValue() != null
                && cmbTipoTarifa.getValue().getIdTipoTarifa() == MisConstantes.TARIFA_CONVENIO;

        var sede = SessionManager.getInstance().getEstacionamiento();

        if (tarifaEditando != null) {
            tarifaEditando.setPrecio(precio);
            String descStr = txtDescuento.getText().trim();
            if (!descStr.isEmpty()) {
                try { tarifaEditando.setValorDescuento(Double.parseDouble(descStr)); }
                catch (NumberFormatException ignored) {}
            }
            if (esConvenio) {
                tarifaEditando.setUnidadDescuento(cmbUnidadDescuento.getValue());
                try { tarifaEditando.setCantidad_descuento(Integer.parseInt(txtCantidadDescuento.getText().trim())); }
                catch (NumberFormatException ignored) {}
            }
            if (tarifaEditando.getEstacionamiento() == null && sede != null) {
                Estacionamiento est = new Estacionamiento();
                est.setIdEstacionamiento(sede.getIdEstacionamiento());
                tarifaEditando.setEstacionamiento(est);
            }
            tarifaService.actualizarTarifa(tarifaEditando);
        } else {
            TipoTarifa tipo = cmbTipoTarifa.getValue();
            TipoCobro cobro = cmbTipoCobro.getValue();
            if (tipo == null || cobro == null) { mostrarErrorTarifa("Selecciona tipo y cobro."); return; }
            Tarifa nueva = new Tarifa();
            nueva.setTipoTarifa(tipo);
            nueva.setTipoCobro(cobro);
            nueva.setPrecio(precio);
            String descStr = txtDescuento.getText().trim();
            if (!descStr.isEmpty()) {
                try { nueva.setValorDescuento(Double.parseDouble(descStr)); }
                catch (NumberFormatException ignored) {}
            }
            if (esConvenio && cmbUnidadDescuento.getValue() != null) {
                nueva.setUnidadDescuento(cmbUnidadDescuento.getValue());
                try { nueva.setCantidad_descuento(Integer.parseInt(txtCantidadDescuento.getText().trim())); }
                catch (NumberFormatException ignored) {}
            }
            Estacionamiento est = new Estacionamiento();
            est.setIdEstacionamiento(sede.getIdEstacionamiento());
            nueva.setEstacionamiento(est);
            tarifaService.añadirTarifa(sede.getIdEstacionamiento(), nueva);
        }
        cancelarFormTarifa();
        recargarTarifas();
    }

    private void eliminarTarifa(Tarifa t) {
        var sede = SessionManager.getInstance().getEstacionamiento();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar tarifa");
        confirm.setHeaderText("¿Eliminar la tarifa " + t.getTipoTarifa().getDescripcion() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                tarifaService.eliminarTarifa(sede, t.getIdTarifa());
                recargarTarifas();
            }
        });
    }

    @FXML private void cancelarFormTarifa() {
        panelFormTarifa.setVisible(false);
        panelFormTarifa.setManaged(false);
        panelConvenio.setVisible(false);
        panelConvenio.setManaged(false);
        tarifaEditando = null;
    }

    // ===== CÓDIGOS DE ACCESO =====

  private void configurarTablaCodigos() {
    // 1. Configuración de celdas normales
    colCCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigo()));
    colCCliente.setCellValueFactory(d -> {
        Cliente c = d.getValue().getCliente();
        return new SimpleStringProperty(c != null ? c.getNombre() + " " + c.getApellidoPaterno() : "—");
    });
    colCEstado.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getEstadoCodigo().getNombreEstado()));
    colCFechaFin.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getFechaFin() != null
                    ? FMT_FECHA.format(d.getValue().getFechaFin()) : "—"));

    // 2. Configuración de la columna de Acciones (Centrada)
    colCAcciones.setCellFactory(col -> new TableCell<>() {
        private final Button btnCancelar = new Button("Cancelar");
        private final HBox container = new HBox(btnCancelar);

        {
            // Estilo y alineación
            btnCancelar.getStyleClass().add("btn-danger");
            btnCancelar.setMinWidth(90); // Un poco más de margen para el texto
            
            container.setAlignment(Pos.CENTER);
            setAlignment(Pos.CENTER); 
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                CodigoAcceso c = getTableView().getItems().get(getIndex());
                
                // Lógica de estado
                boolean activo = c.getEstadoCodigo().getIdEstadoCodigo() == MisConstantes.CODIGO_ACTIVO;
                btnCancelar.setDisable(!activo);
                
                btnCancelar.setOnAction(e -> cancelarCodigo(c));
                setGraphic(container);
            }
        }
    });

    // 3. Búsqueda en tiempo real y filtrado
    FilteredList<CodigoAcceso> filtrada = new FilteredList<>(listaCodigosCompleta, p -> true);
    tablaCodigos.setItems(filtrada);
    
    if (txtBuscarCodigo != null) {
        txtBuscarCodigo.textProperty().addListener((obs, old, val) -> {
            String f = (val == null) ? "" : val.toLowerCase().trim();
            filtrada.setPredicate(cod -> {
                if (f.isEmpty()) return true;
                
                String codigo = (cod.getCodigo() != null) ? cod.getCodigo().toLowerCase() : "";
                String nombreCompleto = "";
                if (cod.getCliente() != null) {
                    nombreCompleto = (cod.getCliente().getNombre() + " " + 
                                     cod.getCliente().getApellidoPaterno()).toLowerCase();
                }
                
                return codigo.contains(f) || nombreCompleto.contains(f);
            });
        });
    }
}

    @FXML private void recargarCodigos() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Códigos de acceso: tabla vacía en demo — BD omitida.");
            listaCodigosCompleta.clear();
        } else {
            try {
                List<CodigoAcceso> codigos = codigoDAO.listarPorEstacionamiento(sede.getIdEstacionamiento());
                listaCodigosCompleta.setAll(codigos);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelarCodigo(CodigoAcceso c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar código");
        confirm.setHeaderText("¿Cancelar el código " + c.getCodigo() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                codigoService.cancelarCodigoPorId(c.getIdCodigo());
                recargarCodigos();
            }
        });
    }

    // ===== COMBOS =====

    private void inicializarCombos() {
        TipoTarifa[] tipos = {
            crearTipoTarifa(MisConstantes.TARIFA_NORMAL, "Normal"),
            crearTipoTarifa(MisConstantes.TARIFA_PENSION, "Pensión"),
            
            crearTipoTarifa(MisConstantes.TARIFA_CONVENIO, "Convenio"),
            
        };
        cmbTipoTarifa.getItems().addAll(tipos);
        cmbTipoTarifa.setConverter(new javafx.util.StringConverter<>() {
            public String toString(TipoTarifa t) { return t != null ? t.getDescripcion() : ""; }
            public TipoTarifa fromString(String s) { return null; }
        });

        TipoCobro[] cobros = {
            crearTipoCobro(MisConstantes.TIPO_COBRO_HORA, "Por Hora"),
            crearTipoCobro(MisConstantes.TIPO_COBRO_MENSUAL, "Mensual"),
       
        };
        cmbTipoCobro.getItems().addAll(cobros);
        cmbTipoCobro.setConverter(new javafx.util.StringConverter<>() {
            public String toString(TipoCobro t) { return t != null ? t.getNombre() : ""; }
            public TipoCobro fromString(String s) { return null; }
        });

        UnidadDescuento[] unidades = {
            new UnidadDescuento(MisConstantes.UNIDAD_HORA, "Hora", 60),
            new UnidadDescuento(MisConstantes.UNIDAD_DIA, "Día", 1440),
            new UnidadDescuento(MisConstantes.UNIDAD_MINUTO, "Minuto", 1),
        };
        cmbUnidadDescuento.getItems().addAll(unidades);
        cmbUnidadDescuento.setConverter(new javafx.util.StringConverter<>() {
            public String toString(UnidadDescuento u) { return u != null ? u.getTipoUnidad() : ""; }
            public UnidadDescuento fromString(String s) { return null; }
        });
    }

  private void configurarListenerConvenio() {
    cmbTipoTarifa.valueProperty().addListener((obs, old, tipo) -> {
        boolean esConvenio = tipo != null && tipo.getIdTipoTarifa() == MisConstantes.TARIFA_CONVENIO;
        panelConvenio.setVisible(esConvenio);
        panelConvenio.setManaged(esConvenio);

        // UI Dinámica para el campo de precio
        txtPrecio.setEditable(!esConvenio);
        if (esConvenio) {
            txtPrecio.setStyle("-fx-background-color: #e2e8f0; -fx-font-weight: bold; -fx-text-fill: #475569;");
            actualizarPrecioEstimado();
        } else {
            txtPrecio.setStyle(""); // Restaurar estilo original
        }
    });
}

    private TipoTarifa crearTipoTarifa(int id, String desc) {
        TipoTarifa t = new TipoTarifa(); t.setIdTipoTarifa(id); t.setDescripcion(desc); return t;
    }

    private TipoCobro crearTipoCobro(int id, String nombre) {
        TipoCobro t = new TipoCobro(); t.setIdTipoCobro(id); t.setNombre(nombre); return t;
    }

    // ===== ESPACIOS =====

  private void configurarTablaEspacios() {
    colECodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigo()));
    colETipo.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getTipoEspacio() != null ? d.getValue().getTipoEspacio().getDescripcion() : "—"));
    colEEstado.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getEstadoEspacio() != null ? d.getValue().getEstadoEspacio().getDescripcion() : "—"));

    colEAcciones.setCellFactory(col -> new TableCell<>() {
        private final Button btnEliminar = new Button("Eliminar");
        // 1. Creamos un contenedor HBox para el botón
        private final HBox container = new HBox(btnEliminar);

        { 
            // 2. Alineamos el HBox al centro
            container.setAlignment(Pos.CENTER);
            
            // 3. Alineamos la celda misma al centro (muy importante)
            setAlignment(Pos.CENTER); 
            
            btnEliminar.getStyleClass().add("btn-danger"); 
            // Evitamos que el botón se deforme
            btnEliminar.setMinWidth(Region.USE_PREF_SIZE); 
        }

        @Override 
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) { 
                setGraphic(null); 
            } else {
                // 4. Obtenemos el objeto de la fila
                Espacio e = getTableView().getItems().get(getIndex());
                btnEliminar.setOnAction(ev -> eliminarEspacio(e));
                
                // 5. Mostramos el contenedor centrado
                setGraphic(container);
            }
        }
    });
}

    @FXML private void recargarEspacios() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;
        if (SessionManager.MODO_DEMO) {
            tablaEspacios.getItems().setAll(com.estacionamiento.util.MockData.getEspacios());
        } else {
            tablaEspacios.getItems().setAll(espacioDAO.espaciosEstacionamiento(sede.getIdEstacionamiento()));
        }
    }

    @FXML private void nuevoEspacio() {
        txtEspacioCodigo.clear();
        cmbEspacioTipo.setValue(null);
        ocultarErrorEspacio();
        panelFormEspacio.setVisible(true);
        panelFormEspacio.setManaged(true);
    }

    @FXML private void guardarEspacio() {
        ocultarErrorEspacio();
        String codigo = txtEspacioCodigo.getText().trim();
        TipoEspacio tipo = cmbEspacioTipo.getValue();
        if (codigo.isEmpty()) { mostrarErrorEspacio("El código es obligatorio."); return; }
        if (tipo == null) { mostrarErrorEspacio("Selecciona un tipo de espacio."); return; }

        var sede = SessionManager.getInstance().getEstacionamiento();
        if (SessionManager.MODO_DEMO) {
            logger.info("[DEMO] Espacio no guardado en BD.");
            cancelarFormEspacio();
            return;
        }
        if (sede == null) { mostrarErrorEspacio("No hay sede seleccionada."); return; }

        Estacionamiento est = new Estacionamiento();
        est.setIdEstacionamiento(sede.getIdEstacionamiento());
        EstadoEspacio estadoDisponible = new EstadoEspacio();
        estadoDisponible.setIdEstadoEspacio(com.estacionamiento.util.MisConstantes.ESPACIO_DISPONIBLE);

        Espacio nuevo = new Espacio(est, tipo, estadoDisponible, codigo);
        Espacio guardado = espacioDAO.insertarEspacio(nuevo);
        if (guardado.getIdEspacio() > 0) {
            cancelarFormEspacio();
            recargarEspacios();
        } else {
            mostrarErrorEspacio("No se pudo guardar el espacio. Verifica que el código no esté duplicado.");
        }
    }

    private void eliminarEspacio(Espacio e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar espacio");
        confirm.setHeaderText("¿Eliminar el espacio " + e.getCodigo() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                espacioDAO.eliminar(e.getIdEspacio());
                recargarEspacios();
            }
        });
    }

    @FXML private void cancelarFormEspacio() {
        panelFormEspacio.setVisible(false);
        panelFormEspacio.setManaged(false);
        txtEspacioCodigo.clear();
        ocultarErrorEspacio();
    }

    private void inicializarComboEspacioTipo() {
        TipoEspacio normal = new TipoEspacio(com.estacionamiento.util.MisConstantes.TIPO_ESPACIO_NORMAL, "Normal");
        TipoEspacio reserva = new TipoEspacio(com.estacionamiento.util.MisConstantes.TIPO_ESPACIO_RESERVA, "Reserva");
        cmbEspacioTipo.getItems().addAll(normal, reserva);
        cmbEspacioTipo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(TipoEspacio t) { return t != null ? t.getDescripcion() : ""; }
            public TipoEspacio fromString(String s) { return null; }
        });
    }

    // ===== SEDES / ESTACIONAMIENTOS =====

private void configurarTablaSedes() {
    colSNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
    
    colSDireccion.setCellValueFactory(d -> {
        Direccion dir = d.getValue().getDireccion();
        if (dir == null) return new SimpleStringProperty("—");
        String ciudad = dir.getCiudad() != null ? dir.getCiudad().getNombre() : "";
        return new SimpleStringProperty(dir.getCalle() + (ciudad.isEmpty() ? "" : ", " + ciudad));
    });

    colSEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getActivo() ? "Activa" : "Inactiva"));

    colSAcciones.setCellFactory(col -> new TableCell<>() {
        private final Button btnDesactivar = new Button("Desactivar");
        // 1. Contenedor para centrar el botón
        private final HBox container = new HBox(btnDesactivar);

        {
            // 2. Alineación del contenedor y de la propia celda
            container.setAlignment(Pos.CENTER);
            setAlignment(Pos.CENTER);
            
            btnDesactivar.getStyleClass().add("btn-danger");
            // 3. Tamaño mínimo para evitar que el texto se corte
            btnDesactivar.setMinWidth(100); 
        }

        @Override 
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) { 
                setGraphic(null); 
            } else {
                Estacionamiento est = getTableView().getItems().get(getIndex());
                
                // Actualizamos el estado del botón según la sede
                btnDesactivar.setDisable(!est.getActivo());
                btnDesactivar.setOnAction(ev -> desactivarSede(est));
                
                // 4. Mostramos el contenedor centrado
                setGraphic(container);
            }
        }
    });
}

    @FXML private void recargarSedes() {
        var empresa = SessionManager.getInstance().getEmpresa();
        if (empresa == null) return;
        if (SessionManager.MODO_DEMO) {
            tablaSedes.getItems().setAll(com.estacionamiento.util.MockData.getSede());
        } else {
            tablaSedes.getItems().setAll(estDAO.listarPorEmpresa(empresa.getIdEmpresa()));
        }
    }

    @FXML private void nuevaSede() {
        txtSedeNombre.clear();
        txtSedeCalle.clear();
        cmbSedeCiudad.setValue(null);
        ocultarErrorSede();
        panelFormSede.setVisible(true);
        panelFormSede.setManaged(true);
    }

    @FXML private void guardarSede() {
        ocultarErrorSede();
        String nombre = txtSedeNombre.getText().trim();
        String calle = txtSedeCalle.getText().trim();
        String numExterior = txtNumeroExterior.getText().trim();
        String cp = txtCodigoPostal.getText().trim();
        Ciudad ciudad = cmbSedeCiudad.getValue();
        if (nombre.isEmpty()) { mostrarErrorSede("El nombre es obligatorio."); return; }
        if (calle.isEmpty()) { mostrarErrorSede("La dirección es obligatoria."); return; }
        if (cp.isEmpty()) { mostrarErrorSede("El codigo postal exterior es obligatorio."); return; }
        if (ciudad == null) { mostrarErrorSede("Selecciona una ciudad."); return; }

        var empresa = SessionManager.getInstance().getEmpresa();
        if (SessionManager.MODO_DEMO) {
            logger.info("[DEMO] Sede no guardada en BD.");
            cancelarFormSede();
            return;
        }
        if (empresa == null) { mostrarErrorSede("No hay empresa en sesión."); return; }

        int idDireccion = direccionDAO.insertar(calle, ciudad.getIdCiudad(),numExterior,cp);
        if (idDireccion <= 0) { mostrarErrorSede("Error al guardar la dirección."); return; }

        Direccion dir = new Direccion();
        dir.setIdDireccion(idDireccion);

        Estacionamiento nuevo = new Estacionamiento();
        nuevo.setNombre(nombre);
        nuevo.setEmpresa(empresa);
        nuevo.setDireccion(dir);
        nuevo.setActivo(true);

        Estacionamiento guardado = estDAO.insertar(nuevo);
        if (guardado.getIdEstacionamiento() > 0) {
            cancelarFormSede();

            recargarSedes();
            
            boolean seAñadioAdminActual = personalService.añadirAdministradorAnuevaSucursal(SessionManager.getInstance().getUsuario(),guardado);

        } else {
            mostrarErrorSede("No se pudo guardar la sede.");
        }
    }

    private void desactivarSede(Estacionamiento est) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Desactivar sede");
        confirm.setHeaderText("¿Desactivar " + est.getNombre() + "?");
        confirm.setContentText("La sede dejará de aparecer en el sistema.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                estDAO.desactivar(est.getIdEstacionamiento());
                recargarSedes();
            }
        });
    }

    @FXML private void cancelarFormSede() {
        panelFormSede.setVisible(false);
        panelFormSede.setManaged(false);
        txtSedeNombre.clear();
        txtSedeCalle.clear();
        ocultarErrorSede();
    }

    private void inicializarComboSedeCiudad() {
        if (!SessionManager.MODO_DEMO) {
            List<Ciudad> ciudades = ciudadDAO.listarTodas();
            cmbSedeCiudad.getItems().addAll(ciudades);
        }
        cmbSedeCiudad.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Ciudad c) {
                if (c == null) return "";
                return c.getNombre() + (c.getEstado() != null ? ", " + c.getEstado().getNombre() : "");
            }
            public Ciudad fromString(String s) { return null; }
        });
    }

    // ===== HELPERS =====

    private void mostrarErrorTarifa(String msg) {
        lblErrorTarifa.setText(msg); lblErrorTarifa.setVisible(true); lblErrorTarifa.setManaged(true);
    }
    private void ocultarErrorTarifa() {
        lblErrorTarifa.setVisible(false); lblErrorTarifa.setManaged(false);
    }
    private void mostrarErrorEmpleado(String msg) {
        lblErrorEmpleado.setText(msg); lblErrorEmpleado.setVisible(true); lblErrorEmpleado.setManaged(true);
    }
    private void ocultarErrorEmpleado() {
        lblErrorEmpleado.setVisible(false); lblErrorEmpleado.setManaged(false);
    }
    private void mostrarErrorEspacio(String msg) {
        lblErrorEspacio.setText(msg); lblErrorEspacio.setVisible(true); lblErrorEspacio.setManaged(true);
    }
    private void ocultarErrorEspacio() {
        lblErrorEspacio.setVisible(false); lblErrorEspacio.setManaged(false);
    }
    private void mostrarErrorSede(String msg) {
        lblErrorSede.setText(msg); lblErrorSede.setVisible(true); lblErrorSede.setManaged(true);
    }
    private void ocultarErrorSede() {
        lblErrorSede.setVisible(false); lblErrorSede.setManaged(false);
    }
}
