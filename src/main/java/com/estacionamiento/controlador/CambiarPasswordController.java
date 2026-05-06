package com.estacionamiento.controlador;

import com.estacionamiento.servicio.LoginServicio;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;
import com.estacionamiento.modelo.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class CambiarPasswordController {

    @FXML private PasswordField txtNuevaPass;
    @FXML private PasswordField txtConfirmarPass;
    @FXML private Label lblError;

    private final LoginServicio loginServicio = new LoginServicio();

    @FXML
    private void onCambiar() {
        String pass = txtNuevaPass.getText();
        String confirm = txtConfirmarPass.getText();
        
        // Obtenemos el usuario que se guardo en sesión temporalmente durante el login
        Persona usuario = SessionManager.getInstance().getUsuario();

        if (pass.isEmpty() || pass.length() < 4) {
            mostrarError("La contraseña es muy corta.");
            return;
        }

        if (!pass.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        try {
            // Llamamos a tu servicio
            boolean exito = loginServicio.cambiarContraseña(usuario.getIdPersona(), pass);
            
            if (exito) {
                // Al cambiarla, el servicio ya actualizó el SessionManager con el nuevo objeto Persona
                // que tiene requiereCambio = false. Ahora vamos a la pantalla principal.
                NavegadorUI.mostrarLogin();
            } else {
                mostrarError("No se pudo actualizar. Intenta más tarde.");
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        SessionManager.getInstance().setUsuario(null); // Limpiamos sesion fallida
        NavegadorUI.mostrarLogin(); // Metodo hipotetico para volver atrás
    }

    private void mostrarError(String msj) {
        lblError.setText(msj);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}