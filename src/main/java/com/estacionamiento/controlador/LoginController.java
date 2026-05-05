package com.estacionamiento.controlador;

import com.estacionamiento.servicio.LoginServicio;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final LoginServicio loginServicio = new LoginServicio();

    @FXML
    private void onLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor ingresa usuario y contraseña.");
            return;
        }

        // ── MODO DEMO ──────────────────────────────────────────────────────────
        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Login con usuario='" + usuario + "' — BD omitida.");
            MockData.inyectarSesionDemo();
            NavegadorUI.mostrarMainLayout();
            return;
        }
        // ──────────────────────────────────────────────────────────────────────

        ocultarError();

        try {
            int resultado = loginServicio.procesarLogin(usuario, password);
            switch (resultado) {
                case 1 -> {
                    
                    if (SessionManager.getInstance().getUsuario().isRequiereCambio()) {
                        mostrarError("Tu contraseña ha caducado. Contacta al administrador.");
                    } else {
                        NavegadorUI.mostrarMainLayout();
                    }
                }
                case 0 -> NavegadorUI.mostrarZonaSeleccion();   // Admin: elegir zona
                case -1 -> mostrarError("Usuario o contraseña incorrectos.");
                case -2 -> mostrarError("Sin permisos asignados. Contacta al administrador.");
                case 2 -> NavegadorUI.mostrarCambioPassword();//vamos a irnos a la pantalal de cambiar contraseña donde llemaremos al servicio de persona de cambiar contraseña 
              
                
                default -> mostrarError("Error inesperado. Intenta de nuevo.");
            }
        } catch (Exception e) {
            mostrarError("Error de conexión: " + e.getMessage());
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onLogin();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> txtUsuario.requestFocus());
    }
}
