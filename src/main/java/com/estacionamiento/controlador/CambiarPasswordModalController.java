package com.estacionamiento.controlador;

import com.estacionamiento.servicio.LoginServicio;
import com.estacionamiento.util.SessionManager;
import com.estacionamiento.modelo.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class CambiarPasswordModalController {

    @FXML private PasswordField txtPassActual;
    @FXML private PasswordField txtNuevaPass;
    @FXML private PasswordField txtConfirmarPass;
    @FXML private Label lblError;

    private final LoginServicio loginServicio = new LoginServicio();

    @FXML
    private void onGuardar() {
        ocultarError();
        Persona usuario = SessionManager.getInstance().getUsuario();
        if (usuario == null) { cerrar(); return; }

        String actual  = txtPassActual.getText();
        String nueva   = txtNuevaPass.getText();
        String confirm = txtConfirmarPass.getText();

        if (actual.isBlank()) { mostrarError("Ingresa tu contraseña actual."); return; }
        if (nueva.length() < 4) { mostrarError("La nueva contraseña debe tener al menos 4 caracteres."); return; }
        if (!nueva.equals(confirm)) { mostrarError("Las contraseñas nuevas no coinciden."); return; }

        if (!loginServicio.verificarPasswordActual(usuario, actual)) {
            mostrarError("La contraseña actual es incorrecta.");
            return;
        }

        boolean exito = loginServicio.cambiarContraseña(usuario.getIdPersona(), nueva);
        if (exito) {
            cerrar();
        } else {
            mostrarError("No se pudo actualizar la contraseña. Intenta de nuevo.");
        }
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNuevaPass.getScene().getWindow();
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
