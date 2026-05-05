package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Persona;
import com.estacionamiento.servicio.PensionService;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLayoutController {

    private static final Logger logger = Logger.getLogger(MainLayoutController.class.getName());

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;
    @FXML private Label lblAvatarInicial;
    @FXML private Label lblZonaActiva;
    @FXML private Button btnDashboard;
    @FXML private Button btnHistorial;
    @FXML private Button btnAdministracion;
    @FXML private Button btnPensiones;
    @FXML private Button btnClientes;
    @FXML private Button btnConfiguracion;
    @FXML private StackPane contenedorContenido;

    private static final int INACTIVIDAD_MINUTOS = 15;
    private Timeline timerInactividad;
    private ScheduledExecutorService pensionScheduler;

    // Referencia estática para reiniciar timer desde otras clases
    private static MainLayoutController instancia;

    @FXML
    private void initialize() {
        instancia = this;
        configurarUsuario();
        configurarMenuPorRol();
        configurarTimerInactividad();
        mostrarDashboard();
        verificarPensionesEnBackground();
    }

    private void configurarUsuario() {
        Persona usuario = SessionManager.getInstance().getUsuario();
        if (usuario == null) return;

        String nombre = usuario.getNombre() + " " + usuario.getApellidoPaterno();
        lblNombreUsuario.setText(nombre);
        lblRolUsuario.setText(MisConstantes.getNombreRol(usuario.getRol().getIdRol()));
        lblAvatarInicial.setText(usuario.getNombre().substring(0, 1).toUpperCase());

        var sede = SessionManager.getInstance().getEstacionamiento();
        lblZonaActiva.setText(sede != null ? sede.getNombre() : "—");
    }

    private void configurarMenuPorRol() {
        Persona usuario = SessionManager.getInstance().getUsuario();
        if (usuario == null) return;

        boolean esAdmin = usuario.getRol().getIdRol() == MisConstantes.ROL_ADMIN;
        btnAdministracion.setVisible(esAdmin);
        btnAdministracion.setManaged(esAdmin);
        btnPensiones.setVisible(esAdmin);
        btnPensiones.setManaged(esAdmin);
        btnClientes.setVisible(esAdmin);
        btnClientes.setManaged(esAdmin);
        btnConfiguracion.setVisible(esAdmin);
        btnConfiguracion.setManaged(esAdmin);
    }

    private void configurarTimerInactividad() {
        timerInactividad = new Timeline(
                new KeyFrame(Duration.minutes(INACTIVIDAD_MINUTOS), e -> cerrarSesionPorInactividad())
        );
        timerInactividad.setCycleCount(1);
        timerInactividad.play();

        // Reiniciar timer en cualquier interacción con el contenedor
        contenedorContenido.addEventFilter(MouseEvent.ANY, e -> reiniciarTimer());
        contenedorContenido.setOnKeyTyped(e -> reiniciarTimer());
    }

    public static void reiniciarTimer() {
        if (instancia != null && instancia.timerInactividad != null) {
            instancia.timerInactividad.stop();
            instancia.timerInactividad.play();
        }
    }

    private void verificarPensionesEnBackground() {
        pensionScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pension-scheduler");
            t.setDaemon(true);
            return t;
        });
        pensionScheduler.scheduleAtFixedRate(() -> {
            try {
                PensionService ps = new PensionService();
                ps.verificarVencimientos();
                ps.enviarAvisosProximos(3);
            } catch (Exception e) {
                System.err.println("[MainLayout] Error verificando pensiones: " + e.getMessage());
            }
        }, 0, 24, TimeUnit.HOURS);
    }

    private void cerrarSesionPorInactividad() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Sesión expirada");
        alerta.setHeaderText("Cierre de sesión automático");
        alerta.setContentText("Tu sesión fue cerrada por inactividad (" + INACTIVIDAD_MINUTOS + " minutos).");
        alerta.showAndWait();
        cerrarSesion();
    }

    @FXML
    private void mostrarDashboard() {
        activarBoton(btnDashboard);
        cargarVista("/com/estacionamiento/vista/Dashboard.fxml");
    }

    @FXML
    private void mostrarHistorial() {
        activarBoton(btnHistorial);
        cargarVista("/com/estacionamiento/vista/Historial.fxml");
    }

    @FXML
    private void mostrarAdministracion() {
        activarBoton(btnAdministracion);
        cargarVista("/com/estacionamiento/vista/Administracion.fxml");
    }

    @FXML
    private void mostrarPensiones() {
        activarBoton(btnPensiones);
        cargarVista("/com/estacionamiento/vista/Pensiones.fxml");
    }

    @FXML
    private void mostrarClientes() {
        activarBoton(btnClientes);
        cargarVista("/com/estacionamiento/vista/Clientes.fxml");
    }

    @FXML
    private void mostrarConfiguracion() {
        activarBoton(btnConfiguracion);
        cargarVista("/com/estacionamiento/vista/Configuracion.fxml");
    }

    @FXML
    private void abrirCambiarPassword() {
        reiniciarTimer();
        NavegadorUI.abrirModal(
                "/com/estacionamiento/vista/modales/CambiarPasswordModal.fxml",
                "Cambiar Contraseña");
    }

    @FXML
    private void cerrarSesion() {
        if (timerInactividad != null) timerInactividad.stop();
        if (pensionScheduler != null) pensionScheduler.shutdownNow();
        instancia = null;
        SessionManager.getInstance().cerrarSesion();
        NavegadorUI.mostrarLogin();
    }

    private void cargarVista(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) throw new IOException("FXML no encontrado: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();
            contenedorContenido.getChildren().setAll(vista);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error cargando vista: " + fxmlPath, e);
            mostrarErrorVista(fxmlPath, e.getMessage());
        }
    }

    private void activarBoton(Button activo) {
        for (Button b : new Button[]{btnDashboard, btnHistorial, btnAdministracion, btnPensiones, btnClientes, btnConfiguracion}) {
            if (b != null) {
                b.getStyleClass().removeAll("nav-btn-active");
                if (!b.getStyleClass().contains("nav-btn")) b.getStyleClass().add("nav-btn");
            }
        }
        activo.getStyleClass().remove("nav-btn");
        activo.getStyleClass().add("nav-btn-active");
    }

    private void mostrarErrorVista(String path, String msg) {
        Label lbl = new Label("Error cargando vista:\n" + path + "\n" + msg);
        lbl.setStyle("-fx-text-fill:#ef4444;-fx-font-size:14px;");
        contenedorContenido.getChildren().setAll(lbl);
    }
}
