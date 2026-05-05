package com.estacionamiento.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavegadorUI {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Sistema de Estacionamiento");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void mostrarLogin() {
        cargarEscena("/com/estacionamiento/vista/Login.fxml", 520, 600);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void mostrarCambioPassword(){

        cargarEscena("/com/estacionamiento/vista/CambiarPassword.fxml", 520, 600);
        primaryStage.centerOnScreen();
        primaryStage.centerOnScreen();
      
    }

    public static void mostrarZonaSeleccion() {
        cargarEscena("/com/estacionamiento/vista/ZonaSeleccion.fxml", 1200, 720);
        primaryStage.centerOnScreen();
    }

    public static void mostrarMainLayout() {
        cargarEscena("/com/estacionamiento/vista/MainLayout.fxml", 1280, 800);
        primaryStage.centerOnScreen();
    }

    private static void cargarEscena(String fxmlPath, double ancho, double alto) {
        try {
            URL url = NavegadorUI.class.getResource(fxmlPath);
            if (url == null) {
                throw new IOException("No se encontró el FXML: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, ancho, alto);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando vista: " + fxmlPath, e);
        }
    }

    public static <T> T abrirModal(String fxmlPath, String titulo) {
        try {
            URL url = NavegadorUI.class.getResource(fxmlPath);
            if (url == null) throw new IOException("FXML no encontrado: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle(titulo);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(primaryStage);
            Scene scene = new Scene(root);
            aplicarCss(scene);
            modal.setScene(scene);
            modal.setResizable(false);
            modal.showAndWait();

            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Error abriendo modal: " + fxmlPath, e);
        }
    }

    public static FXMLLoader crearLoader(String fxmlPath) {
        URL url = NavegadorUI.class.getResource(fxmlPath);
        if (url == null) throw new RuntimeException("FXML no encontrado: " + fxmlPath);
        return new FXMLLoader(url);
    }

    public static void aplicarCss(Scene scene) {
        URL css = NavegadorUI.class.getResource("/com/estacionamiento/vista/styles/app.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}
