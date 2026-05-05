package com.estacionamiento;

import com.estacionamiento.util.NavegadorUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NavegadorUI.init(primaryStage);
        NavegadorUI.mostrarLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
