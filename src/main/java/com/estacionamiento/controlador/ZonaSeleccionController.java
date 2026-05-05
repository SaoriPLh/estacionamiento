package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Estacionamiento;
import com.estacionamiento.servicio.LoginServicio;
import com.estacionamiento.util.NavegadorUI;
import com.estacionamiento.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ZonaSeleccionController {

    @FXML private Label lblBienvenido;
    @FXML private HBox hboxZonas;

    private final LoginServicio loginServicio = new LoginServicio();

    @FXML
    private void initialize() {
        var usuario = SessionManager.getInstance().getUsuario();
        if (usuario != null) {
            lblBienvenido.setText("Bienvenido, " + usuario.getNombre());
        }

        List<Estacionamiento> sedes = loginServicio.obtenerSedesAutorizadas(usuario);
        for (Estacionamiento sede : sedes) {
            hboxZonas.getChildren().add(crearCardZona(sede));
        }
    }

    private VBox crearCardZona(Estacionamiento sede) {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("zona-card");
        card.setPadding(new Insets(30, 24, 30, 24));

        // Icono
        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(56, 56);
        iconBox.setMaxSize(56, 56);
        iconBox.setStyle("-fx-background-color:" + getColorForSede(sede) + ";-fx-background-radius:14;");
        Label iconLabel = new Label("P");
        iconLabel.setStyle("-fx-text-fill:white;-fx-font-size:22px;-fx-font-weight:bold;");
        iconBox.getChildren().add(iconLabel);

        Label nombre = new Label(sede.getNombre());
        nombre.getStyleClass().add("zona-title");

     /*   int numEspacios = sede.getEspaciosEstacionamiento() != null
                ? sede.getEspaciosEstacionamiento().size() : 0;
        Label espacios = new Label(numEspacios + " espacios");
        espacios.getStyleClass().add("zona-espacios");*/

        Label acceder = new Label("Acceder →");
        acceder.setStyle("-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;");
        acceder.setVisible(false);
        acceder.setManaged(false);

        card.getChildren().addAll(iconBox, nombre);

        card.setOnMouseEntered(e -> {
            card.getStyleClass().removeAll("zona-card");
            card.getStyleClass().add("zona-card-selected");
            acceder.setVisible(true);
            acceder.setManaged(true);
            if (!card.getChildren().contains(acceder)) card.getChildren().add(acceder);
        });
        card.setOnMouseExited(e -> {
            card.getStyleClass().removeAll("zona-card-selected");
            card.getStyleClass().add("zona-card");
            acceder.setVisible(false);
            acceder.setManaged(false);
        });
        card.setOnMouseClicked(e -> seleccionarZona(sede));

        return card;
    }

    private void seleccionarZona(Estacionamiento sede) {
        loginServicio.asignarEstacionamientoAdmin(sede);
        NavegadorUI.mostrarMainLayout();
    }

    private String getColorForSede(Estacionamiento sede) {
        String[] colores = {"#2563eb", "#16a34a", "#9333ea", "#ea580c", "#0891b2"};
        return colores[sede.getIdEstacionamiento() % colores.length];
    }
}
