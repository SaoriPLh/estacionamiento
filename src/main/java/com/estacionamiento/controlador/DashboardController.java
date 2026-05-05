package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Espacio;
import com.estacionamiento.modelo.Registro;
import com.estacionamiento.servicio.EspacioService;
import com.estacionamiento.servicio.RegistroServicio;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML private Label lblZona;
    @FXML private Label lblTotal;
    @FXML private Label lblDisponibles;
    @FXML private Label lblOcupados;
    @FXML private Label lblPension;
    @FXML private FlowPane flowEspacios;

    // Buscador por placa
    @FXML private TextField txtBuscarPlaca;
    @FXML private VBox panelResultadoBusqueda;
    @FXML private Label lblResultadoBusqueda;
    @FXML private Button btnSalidaBusqueda;
    @FXML private Label lblErrorBusqueda;

    private final EspacioService espacioService = new EspacioService();
    private final RegistroServicio registroServicio = new RegistroServicio();

    private Registro registroBuscado = null;

    @FXML
    private void initialize() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede != null) lblZona.setText("Zona: " + sede.getNombre());
        cargarEspacios();
    }

    @FXML
    private void recargar() {
        MainLayoutController.reiniciarTimer();
        cargarEspacios();
    }

    // ── Botón "+ Registrar Entrada" en el encabezado (sin espacio preseleccionado) ──
    @FXML
    private void abrirRegistroEntrada() {
        MainLayoutController.reiniciarTimer();
        RegistroEntradaController ctrl = NavegadorUI.abrirModal(
                "/com/estacionamiento/vista/modales/RegistroEntrada.fxml",
                "Registrar Entrada");
        if (ctrl != null && ctrl.isConfirmado()) recargar();
    }

    // ── Carga de la grilla ────────────────────────────────────────────────────
    private void cargarEspacios() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;

        List<Espacio> espacios;
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Cargando 30 espacios simulados — BD omitida.");
            espacios = MockData.getEspacios();
        } else {
            espacios = espacioService.listarEspacios(sede.getIdEstacionamiento());
        }

        int total = espacios.size();
        int disponibles = 0, ocupados = 0, pension = 0;
        for (Espacio e : espacios) {
            int est = e.getEstadoEspacio().getIdEstadoEspacio();
            if (est == MisConstantes.ESPACIO_DISPONIBLE) disponibles++;
            else if (est == MisConstantes.ESPACIO_OCUPADO) ocupados++;
            else if (est == MisConstantes.ESPACIO_PENSION) pension++;
        }

        lblTotal.setText(String.valueOf(total));
        lblDisponibles.setText(String.valueOf(disponibles));
        lblOcupados.setText(String.valueOf(ocupados));
        lblPension.setText(String.valueOf(pension));

        flowEspacios.getChildren().clear();
        for (Espacio espacio : espacios) {
            flowEspacios.getChildren().add(crearBotonEspacio(espacio));
        }
    }

    // ── Creación de cada cajón ────────────────────────────────────────────────
    private VBox crearBotonEspacio(Espacio espacio) {
        int estado = espacio.getEstadoEspacio().getIdEstadoEspacio();
        System.out.println("estado de espacio "+estado);
        
        int tipoEspacio = espacio.getTipoEspacio().getIdTipoEspacio();
        System.out.println("tipo de espacio "+tipoEspacio);
      String styleClass;

if (estado == MisConstantes.ESPACIO_DISPONIBLE 
    && tipoEspacio == MisConstantes.TIPO_ESPACIO_RESERVA) {

    styleClass = "espacio-reserva-disponible"; 
} else {
    styleClass = switch (estado) {
        case MisConstantes.ESPACIO_DISPONIBLE -> "espacio-disponible";
        case MisConstantes.ESPACIO_OCUPADO    -> "espacio-ocupado";
        case MisConstantes.ESPACIO_PENSION    -> "espacio-pension";
        case MisConstantes.ESPACIO_RESERVADO  -> "espacio-reservado";
        default -> "espacio-disponible";
    };
}

        String icono = switch (estado) {
            case MisConstantes.ESPACIO_DISPONIBLE -> "";
            case MisConstantes.ESPACIO_OCUPADO    -> "●";
            case MisConstantes.ESPACIO_PENSION    -> "★";
            case MisConstantes.ESPACIO_RESERVADO  -> "◉";
            default -> "";
        };

       
       
          String iconColor = switch (estado) {
            case MisConstantes.ESPACIO_DISPONIBLE -> "#166534";
            case MisConstantes.ESPACIO_OCUPADO    -> "#991b1b";
            case MisConstantes.ESPACIO_PENSION    -> "#1e40af";
            case MisConstantes.ESPACIO_RESERVADO    -> "#991b1b";
       
            default -> "#1e293b";
     
            };

        VBox contenedor = new VBox(8);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.getStyleClass().addAll("espacio-btn", styleClass);

        Label lblIcono = new Label(icono);
        System.out.println("COLOR FINAL: " + iconColor);

lblIcono.setStyle(
    "-fx-font-size:22px;" +
    "-fx-font-weight:bold;" +
    "-fx-text-fill:" + iconColor
);

        String codigoTexto = espacio.getCodigo() != null ? espacio.getCodigo() : "#" + espacio.getIdEspacio();
        Label lblNumero = new Label(codigoTexto);
        lblNumero.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#111827;");

        Label lblEstado = new Label(MisConstantes.getNombreEstadoEspacio(estado));
        lblEstado.setStyle("-fx-font-size:10px; -fx-text-fill:#374151;");

        contenedor.getChildren().addAll(lblIcono, lblNumero, lblEstado);

        // TODOS los cajones son clicables
        contenedor.setOnMouseClicked(e -> manejarClickEspacio(espacio));

        return contenedor;
    }

    // ── Router de acciones por estado ────────────────────────────────────────
    private void manejarClickEspacio(Espacio espacio) {
        MainLayoutController.reiniciarTimer();
        int estado = espacio.getEstadoEspacio().getIdEstadoEspacio();
        switch (estado) {
            case MisConstantes.ESPACIO_DISPONIBLE -> abrirRegistroEntradaConEspacio(espacio);
            case MisConstantes.ESPACIO_OCUPADO    -> abrirAccionesOcupado(espacio);
            case MisConstantes.ESPACIO_PENSION    -> abrirAccionesPension(espacio);
            case MisConstantes.ESPACIO_RESERVADO  -> abrirAccionesReservado(espacio);
        }
    }

    // ── Disponible → Entrada directa preseleccionada ─────────────────────────
    private void abrirRegistroEntradaConEspacio(Espacio espacio) {
        try {
            FXMLLoader loader = NavegadorUI.crearLoader(
                    "/com/estacionamiento/vista/modales/RegistroEntrada.fxml");
            Parent root = loader.load();
            RegistroEntradaController ctrl = loader.getController();
            ctrl.setEspacioPreseleccionado(espacio);

            Stage modal = new Stage();
            modal.setTitle("Registrar Entrada — Espacio " + espacio.getCodigo());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(NavegadorUI.getPrimaryStage());
            Scene scene = new Scene(root);
            NavegadorUI.aplicarCss(scene);
            modal.setScene(scene);
            modal.setResizable(false);
            modal.showAndWait();

            if (ctrl.isConfirmado()) recargar();
        } catch (Exception e) {
            mostrarError("Error abriendo registro de entrada: " + e.getMessage());
        }
    }

    // ── Ocupado → Salida ──────────────────────────────────────────────────────
    private void abrirAccionesOcupado(Espacio espacio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Espacio #" + espacio.getCodigo());
        alert.setHeaderText("Espacio ocupado — ¿qué deseas hacer?");

        ButtonType btnSalida   = new ButtonType("Registrar Salida");
        ButtonType btnCancelar = new ButtonType("Cancelar");
        alert.getButtonTypes().setAll(btnSalida, btnCancelar);
        estilizarAlert(alert);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == btnSalida) abrirRegistroSalida(espacio);
    }

    // ── Pensión → Entrada con código o Salida ────────────────────────────────
    private void abrirAccionesPension(Espacio espacio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Espacio #" + espacio.getCodigo() + "  [PENSIÓN]");
        alert.setHeaderText("Espacio de pensión — ¿qué deseas hacer?");

        ButtonType btnEntrada  = new ButtonType("Registrar Entrada (con código)");
        ButtonType btnSalida   = new ButtonType("Registrar Salida");
        ButtonType btnCancelar = new ButtonType("Cancelar");
        alert.getButtonTypes().setAll(btnEntrada, btnSalida, btnCancelar);
        estilizarAlert(alert);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isEmpty()) return;
        if (res.get() == btnEntrada) abrirRegistroEntradaConEspacio(espacio);
        else if (res.get() == btnSalida) abrirRegistroSalida(espacio);
    }

    // ── Reservado → Entrada o Liberar ────────────────────────────────────────
    private void abrirAccionesReservado(Espacio espacio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Espacio #" + espacio.getCodigo() + "  [RESERVADO]");
        alert.setHeaderText("Espacio reservado — ¿qué deseas hacer?");

        ButtonType btnEntrada  = new ButtonType("Confirmar Entrada");
        ButtonType btnLiberar  = new ButtonType("Liberar Espacio");
        ButtonType btnCancelar = new ButtonType("Cancelar");
        alert.getButtonTypes().setAll(btnEntrada, btnLiberar, btnCancelar);
        estilizarAlert(alert);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isEmpty()) return;
        if (res.get() == btnEntrada) abrirRegistroEntradaConEspacio(espacio);
        else if (res.get() == btnLiberar) {
            if (!SessionManager.MODO_DEMO) espacioService.liberarCajon(espacio.getIdEspacio());
            recargar();
        }
    }

    // ── Modal de salida ───────────────────────────────────────────────────────
    private void abrirRegistroSalida(Espacio espacio) {
        try {
            FXMLLoader loader = NavegadorUI.crearLoader(
                    "/com/estacionamiento/vista/modales/RegistroSalida.fxml");
            Parent root = loader.load();
            RegistroSalidaController ctrl = loader.getController();

            Stage modal = new Stage();
            modal.setTitle("Registrar Salida — Espacio " + espacio.getCodigo());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(NavegadorUI.getPrimaryStage());
            Scene scene = new Scene(root, 520, 420);
            NavegadorUI.aplicarCss(scene);
            modal.setScene(scene);
            modal.setResizable(false);

            // Pre-select after the window is ready to avoid NPE during search
            modal.setOnShown(e -> ctrl.setEspacioPreseleccionado(espacio));
            modal.showAndWait();

            if (ctrl.isConfirmado()) recargar();
        } catch (Exception e) {
            mostrarError("Error abriendo modal de salida: " + e.getMessage());
        }
    }

    // ── Buscador por placa ────────────────────────────────────────────────────
    @FXML
    private void buscarPorPlaca() {
        MainLayoutController.reiniciarTimer();
        String placa = txtBuscarPlaca.getText().trim().toUpperCase();
        panelResultadoBusqueda.setVisible(false);
        panelResultadoBusqueda.setManaged(false);
        lblErrorBusqueda.setVisible(false);
        lblErrorBusqueda.setManaged(false);
        registroBuscado = null;

        if (placa.isEmpty()) {
            lblErrorBusqueda.setText("Escribe una placa para buscar.");
            lblErrorBusqueda.setVisible(true);
            lblErrorBusqueda.setManaged(true);
            return;
        }

        if (SessionManager.MODO_DEMO) {
            lblErrorBusqueda.setText("Búsqueda por placa no disponible en modo demo.");
            lblErrorBusqueda.setVisible(true);
            lblErrorBusqueda.setManaged(true);
            return;
        }

        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;
        List<Registro> activos = registroServicio.obtenerRegistrosFiltrados(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), null, sede.getIdEstacionamiento());

        registroBuscado = activos.stream()
                .filter(r -> r.getEstadoRegistro() != null
                        && r.getEstadoRegistro().getIdEstadoRegistro() == MisConstantes.REGISTRO_ACTIVO)
                .filter(r -> r.getVehiculo() != null
                        && placa.equalsIgnoreCase(r.getVehiculo().getPlaca()))
                .findFirst().orElse(null);

        if (registroBuscado == null) {
            lblErrorBusqueda.setText("No se encontró un registro activo para la placa \"" + placa + "\".");
            lblErrorBusqueda.setVisible(true);
            lblErrorBusqueda.setManaged(true);
            return;
        }

        String espacioCod = registroBuscado.getEspacio() != null
                ? registroBuscado.getEspacio().getCodigo() : "—";
        lblResultadoBusqueda.setText("Placa " + placa + "  →  Espacio " + espacioCod);
        panelResultadoBusqueda.setVisible(true);
        panelResultadoBusqueda.setManaged(true);
    }

    @FXML
    private void abrirSalidaDesdeBusqueda() {
        if (registroBuscado == null || registroBuscado.getEspacio() == null) return;
        txtBuscarPlaca.clear();
        panelResultadoBusqueda.setVisible(false);
        panelResultadoBusqueda.setManaged(false);
        abrirRegistroSalida(registroBuscado.getEspacio());
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msg);
        estilizarAlert(a);
        a.showAndWait();
    }

    private void estilizarAlert(Alert alert) {
        java.net.URL css = NavegadorUI.class.getResource(
                "/com/estacionamiento/vista/styles/app.css");
        if (css != null) {
            alert.getDialogPane().getStylesheets().add(css.toExternalForm());
            alert.getDialogPane().getStyleClass().add("modal-root");
        }
    }
}
