package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Cliente;
import com.estacionamiento.modelo.CodigoAcceso;
import com.estacionamiento.modelo.Vehiculo;
import com.estacionamiento.servicio.ClienteService;
import com.estacionamiento.servicio.CodigoAccesoService;
import com.estacionamiento.util.DBConnection;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientesController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<ClienteInfo> tablaClientes;
    @FXML private TableColumn<ClienteInfo, String> colNombre;
    @FXML private TableColumn<ClienteInfo, String> colPlaca;
    @FXML private TableColumn<ClienteInfo, String> colTelefono;
    @FXML private TableColumn<ClienteInfo, String> colCorreo;
    @FXML private TableColumn<ClienteInfo, String> colPension;
    @FXML private TableColumn<ClienteInfo, String> colCodigo;
    @FXML private TableColumn<ClienteInfo, Void>   colAcciones;
    @FXML private Label lblEstado;

    private final ClienteService clienteService = new ClienteService();
    private final CodigoAccesoService codigoAccesoService = new CodigoAccesoService();
    private static final SimpleDateFormat FMT_FECHA = new SimpleDateFormat("yyyy-MM-dd");

    private final ObservableList<ClienteInfo> listaOriginal = FXCollections.observableArrayList();
    private FilteredList<ClienteInfo> listaFiltrada;

    // ── Modelo de fila ──────────────────────────────────────────────────────
    public static class ClienteInfo {
        public final Cliente cliente;
        public final String placa;
        public final boolean tienePension;
        public final boolean tieneCodigo;

        public ClienteInfo(Cliente c, String placa, boolean pension, boolean codigo) {
            this.cliente = c;
            this.placa = placa;
            this.tienePension = pension;
            this.tieneCodigo = codigo;
        }
    }

    @FXML
    private void initialize() {
        configurarTabla();
        configurarBusqueda();
        cargarClientes();
    }

    // ── Configuración de la tabla ───────────────────────────────────────────
    private void configurarTabla() {
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().cliente.getNombre() + " " + d.getValue().cliente.getApellidoPaterno()));

        colPlaca.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().placa));

        colTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().cliente.getTelefono() != null ? d.getValue().cliente.getTelefono() : "—"));

        colCorreo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().cliente.getCorreo() != null ? d.getValue().cliente.getCorreo() : "—"));

        colPension.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().tienePension ? "Sí" : "No"));
        colPension.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Sí".equals(item)
                        ? "-fx-text-fill:#16a34a; -fx-font-weight:bold;"
                        : "-fx-text-fill:#64748b;");
            }
        });

        colCodigo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().tieneCodigo ? "Sí" : "No"));
        colCodigo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Sí".equals(item)
                        ? "-fx-text-fill:#2563eb; -fx-font-weight:bold;"
                        : "-fx-text-fill:#64748b;");
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar    = new Button("Editar");
            private final Button btnCodigo    = new Button("Código");
            private final Button btnHistorial = new Button("Historial");
            private final HBox hbox = new HBox(6, btnEditar, btnCodigo, btnHistorial);

            {
                hbox.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("btn-secondary");
                btnCodigo.getStyleClass().add("btn-primary");
                btnHistorial.getStyleClass().add("btn-secondary");

                btnEditar.setOnAction(e -> {
                    ClienteInfo info = getTableView().getItems().get(getIndex());
                    abrirEditar(info.cliente);
                });
                btnCodigo.setOnAction(e -> {
                    ClienteInfo info = getTableView().getItems().get(getIndex());
                    abrirGenerarCodigo(info.cliente);
                });
                btnHistorial.setOnAction(e -> {
                    ClienteInfo info = getTableView().getItems().get(getIndex());
                    abrirHistorial(info.cliente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                ClienteInfo info = getTableView().getItems().get(getIndex());
                // Habilitar "Código" solo si tiene pensión activa Y no tiene código activo
                btnCodigo.setDisable(!info.tienePension || info.tieneCodigo);
                setGraphic(hbox);
            }
        });
    }

    // ── Búsqueda en tiempo real ─────────────────────────────────────────────
    private void configurarBusqueda() {
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        tablaClientes.setItems(listaFiltrada);
        txtBuscar.textProperty().addListener((obs, old, val) -> {
            String f = val == null ? "" : val.toLowerCase().trim();
            listaFiltrada.setPredicate(info -> {
                if (f.isEmpty()) return true;
                String nombre = (info.cliente.getNombre() + " " + info.cliente.getApellidoPaterno()).toLowerCase();
                String correo = info.cliente.getCorreo() != null ? info.cliente.getCorreo().toLowerCase() : "";
                String tel    = info.cliente.getTelefono() != null ? info.cliente.getTelefono() : "";
                return nombre.contains(f) || correo.contains(f) || tel.contains(f);
            });
        });
    }

    // ── Carga en hilo de fondo con consultas por lote ───────────────────────
    private void cargarClientes() {
        lblEstado.setText("Cargando clientes...");
        Thread t = new Thread(() -> {
            try {
                List<ClienteInfo> infos = new ArrayList<>();
                if (SessionManager.MODO_DEMO) {
                    for (Cliente c : MockData.getClientes()) {
                        infos.add(new ClienteInfo(c, "DEMO-01", true, false));
                    }
                } else {
                    var sede = SessionManager.getInstance().getEstacionamiento();

List<Cliente> clientes = clienteService
        .listarClientesPensionadosPorEstacionamiento(sede.getIdEstacionamiento());
                    // Batch queries — 3 queries total instead of 3×N
                    Map<Integer, String> placas     = batchPlacas();
                    Set<Integer>         conPension = batchClientesConPension();
                    Set<Integer>         conCodigo  = batchClientesConCodigo();

                    for (Cliente c : clientes) {
                        String placa      = placas.getOrDefault(c.getIdCliente(), "—");
                        boolean pension   = conPension.contains(c.getIdCliente());
                        boolean codigo    = conCodigo.contains(c.getIdCliente());
                        infos.add(new ClienteInfo(c, placa, pension, codigo));
                    }
                }
                final int total = infos.size();
                Platform.runLater(() -> {
                    listaOriginal.setAll(infos);
                    lblEstado.setText(total + " cliente(s)");
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblEstado.setText("Error al cargar clientes: " + e.getMessage()));
            }
        }, "clientes-loader");
        t.setDaemon(true);
        t.start();
    }

    // Primera placa por cliente: primero por vínculo directo, luego por pensión
    private Map<Integer, String> batchPlacas() {
        Map<Integer, String> map = new HashMap<>();
        String sql1 = "SELECT id_cliente, MIN(placa) AS placa FROM vehiculo " +
                      "WHERE id_cliente IS NOT NULL GROUP BY id_cliente";
        String sql2 = "SELECT p.id_cliente, MIN(v.placa) AS placa " +
                      "FROM pension p JOIN vehiculo v ON v.id_vehiculo = p.id_vehiculo " +
                      "WHERE p.id_cliente IS NOT NULL GROUP BY p.id_cliente";
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql1);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getInt("id_cliente"), rs.getString("placa"));
            }
            try (PreparedStatement ps = con.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_cliente");
                    if (!map.containsKey(id)) map.put(id, rs.getString("placa"));
                }
            }
        } catch (Exception e) {
            System.err.println("[ClientesController] batchPlacas: " + e.getMessage());
        }
        return map;
    }

    private Set<Integer> batchClientesConPension() {
        Set<Integer> set = new HashSet<>();
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return set;
        String sql = "SELECT DISTINCT p.id_cliente FROM pension p " +
                     "JOIN espacio e ON p.id_espacio = e.id_espacio " +
                     "WHERE p.id_estado_pension = 1 AND e.id_estacionamiento = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sede.getIdEstacionamiento());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) set.add(rs.getInt("id_cliente"));
            }
        } catch (Exception e) {
            System.err.println("[ClientesController] batchPension: " + e.getMessage());
        }
        return set;
    }

    private Set<Integer> batchClientesConCodigo() {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT DISTINCT id_cliente FROM codigo_acceso " +
                     "WHERE id_estado_codigo = 1 AND fecha_fin > NOW()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) set.add(rs.getInt("id_cliente"));
        } catch (Exception e) {
            System.err.println("[ClientesController] batchCodigo: " + e.getMessage());
        }
        return set;
    }

    // ── Acciones ───────────────────────────────────────────────────────────
    @FXML
    private void nuevoCliente() {
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
            recargar();
        } catch (Exception e) {
            mostrarError("Error abriendo formulario: " + e.getMessage());
        }
    }

    private void abrirEditar(Cliente cliente) {
        try {
            FXMLLoader loader = NavegadorUI.crearLoader(
                    "/com/estacionamiento/vista/modales/FormularioCliente.fxml");
            Parent root = loader.load();
            FormularioClienteController ctrl = loader.getController();
            ctrl.setCliente(cliente);
            Stage modal = new Stage();
            modal.setTitle("Editar Cliente — " + cliente.getNombre() + " " + cliente.getApellidoPaterno());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(NavegadorUI.getPrimaryStage());
            Scene scene = new Scene(root);
            NavegadorUI.aplicarCss(scene);
            modal.setScene(scene);
            modal.showAndWait();
            recargar();
        } catch (Exception e) {
            mostrarError("Error abriendo editor: " + e.getMessage());
        }
    }

    private void abrirGenerarCodigo(Cliente cliente) {
        try {
            FXMLLoader loader = NavegadorUI.crearLoader(
                    "/com/estacionamiento/vista/modales/FormularioCliente.fxml");
            Parent root = loader.load();
            FormularioClienteController ctrl = loader.getController();
            ctrl.setModoSoloCodigo(cliente);
            Stage modal = new Stage();
            modal.setTitle("Generar Código — " + cliente.getNombre() + " " + cliente.getApellidoPaterno());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(NavegadorUI.getPrimaryStage());
            Scene scene = new Scene(root);
            NavegadorUI.aplicarCss(scene);
            modal.setScene(scene);
            modal.showAndWait();
            recargar();
        } catch (Exception e) {
            mostrarError("Error generando código: " + e.getMessage());
        }
    }

    private void abrirHistorial(Cliente cliente) {
        String nombre = cliente.getNombre() + " " + cliente.getApellidoPaterno();
        try {
            StringBuilder sb = new StringBuilder();
            if (!SessionManager.MODO_DEMO) {
                // Vehículos
                String sqlV = "SELECT placa, modelo FROM vehiculo WHERE id_cliente = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sqlV)) {
                    ps.setInt(1, cliente.getIdCliente());
                    try (ResultSet rs = ps.executeQuery()) {
                        boolean hay = false;
                        while (rs.next()) {
                            if (!hay) { sb.append("Vehículos:\n"); hay = true; }
                            sb.append("  • ").append(rs.getString("placa"));
                            String mod = rs.getString("modelo");
                            if (mod != null && !mod.isBlank()) sb.append("  (").append(mod).append(")");
                            sb.append("\n");
                        }
                        if (!hay) sb.append("Sin vehículos registrados.\n");
                    }
                }
                // Códigos de acceso
                List<CodigoAcceso> codigos = codigoAccesoService.obtenerHistorial(cliente.getIdCliente());
                if (!codigos.isEmpty()) {
                    sb.append("\nCódigos de acceso:\n");
                    for (CodigoAcceso c : codigos) {
                        String estado = c.getEstadoCodigo() != null ? c.getEstadoCodigo().getNombreEstado() : "—";
                        String fechaFin = c.getFechaFin() != null ? FMT_FECHA.format(c.getFechaFin()) : "—";
                        sb.append("  • ").append(c.getCodigo())
                          .append("  [").append(estado).append("]")
                          .append("  Vence: ").append(fechaFin)
                          .append("\n");
                    }
                }
            } else {
                sb.append("Historial no disponible en modo DEMO.");
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Historial — " + nombre);
            a.setHeaderText(nombre);
            a.setContentText(sb.toString().isBlank() ? "Sin registros." : sb.toString());
            estilizarAlert(a);
            a.showAndWait();
        } catch (Exception e) {
            mostrarError("Error cargando historial: " + e.getMessage());
        }
    }

    @FXML
    private void recargar() {
        MainLayoutController.reiniciarTimer();
        listaOriginal.clear();
        cargarClientes();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msg);
        estilizarAlert(a);
        a.showAndWait();
    }

    private void estilizarAlert(Alert a) {
        java.net.URL css = NavegadorUI.class.getResource("/com/estacionamiento/vista/styles/app.css");
        if (css != null) {
            a.getDialogPane().getStylesheets().add(css.toExternalForm());
            a.getDialogPane().getStyleClass().add("modal-root");
        }
    }
}
