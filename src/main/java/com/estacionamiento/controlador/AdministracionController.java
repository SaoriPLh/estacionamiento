package com.estacionamiento.controlador;

import com.estacionamiento.modelo.Espacio;
import com.estacionamiento.modelo.Persona;
import com.estacionamiento.modelo.Registro;
import com.estacionamiento.modelo.ResumenGananciasDTO;
import com.estacionamiento.modelo.ResumenNominaDTO;
import javafx.application.Platform;
import com.estacionamiento.servicio.EspacioService;
import com.estacionamiento.servicio.PersonalService;
import com.estacionamiento.servicio.RegistroServicio;
import com.estacionamiento.util.MisConstantes;
import com.estacionamiento.util.MockData;
import com.estacionamiento.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdministracionController {

    @FXML private Label lblIngresos;
    @FXML private Label lblOcupacion;
    @FXML private Label lblOperadores;
    @FXML private Label lblPromedioDia;
    @FXML private BarChart<String, Number> barChart;
    @FXML private Label lblTotalEmpleados;
    @FXML private Label lblNominaTotal;
    @FXML private TableView<Persona> tablaEmpleados;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colRol;
    @FXML private TableColumn<Persona, String> colSalario;

    private final RegistroServicio registroServicio = new RegistroServicio();
    private final PersonalService personalService = new PersonalService();
    private final EspacioService espacioService = new EspacioService();

    @FXML
    private void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(data -> {
            Persona p = data.getValue();
            return new SimpleStringProperty(p.getNombre() + " " + p.getApellidoPaterno());
        });
        colRol.setCellValueFactory(data ->
                new SimpleStringProperty(MisConstantes.getNombreRol(data.getValue().getRol().getIdRol())));
        colSalario.setCellValueFactory(data -> {
            Double sal = data.getValue().getSalario();
            return new SimpleStringProperty(sal != null ? String.format("$%.0f", sal) : "—");
        });
    }

    @FXML
    private void recargar() {
        MainLayoutController.reiniciarTimer();
        cargarDatos();
    }

    private void cargarDatos() {
        var sede = SessionManager.getInstance().getEstacionamiento();
        if (sede == null) return;

        if (SessionManager.MODO_DEMO) {
            System.out.println("[DEMO] Administración: datos simulados — BD omitida.");
            ResumenGananciasDTO g = MockData.getResumenGanancias();
            List<Persona> personal = MockData.getPersonal();
            List<Espacio> espacios = MockData.getEspacios();

            lblIngresos.setText(String.format("$%.0f", g.gananciasTotales));
            long ocupados = espacios.stream().filter(e ->
                    e.getEstadoEspacio().getIdEstadoEspacio() != MisConstantes.ESPACIO_DISPONIBLE).count();
            lblOcupacion.setText(String.format("%.1f%%", ocupados * 100.0 / espacios.size()));
            long operadores = personal.stream()
                    .filter(p -> p.getRol().getIdRol() == MisConstantes.ROL_EMPLEADO).count();
            lblOperadores.setText(String.valueOf(operadores));
            lblPromedioDia.setText("$" + String.format("%.0f", g.gananciasTotales / 30.0));
            ResumenNominaDTO nomina = personalService.calcularResumenNomina(personal);
            lblTotalEmpleados.setText(String.valueOf(nomina.getTotalEmpleados()));
            lblNominaTotal.setText(String.format("$%.0f", nomina.getTotalNomina()));
            cargarGrafica(MockData.getHistorial());
            tablaEmpleados.getItems().setAll(personal);
            return;
        }

        Thread hilo = new Thread(() -> {
            List<Registro> registros = registroServicio.obtenerRegistrosFiltrados(
                    LocalDateTime.now().minusDays(30), LocalDateTime.now(), null, sede.getIdEstacionamiento());
            List<Espacio> espacios = espacioService.listarEspacios(sede.getIdEstacionamiento());
            List<Persona> personal = personalService.obtenerPersonalDeSedeActual();
            ResumenNominaDTO nomina = personalService.calcularResumenNomina(personal);
            ResumenGananciasDTO ganancias = registroServicio.obtenerGanancias(registros);

            Platform.runLater(() -> {
                lblIngresos.setText(String.format("$%.0f", ganancias.gananciasTotales));

                long ocupados = espacios.stream().filter(e ->
                        e.getEstadoEspacio().getIdEstadoEspacio() != MisConstantes.ESPACIO_DISPONIBLE).count();
                double tasaOcupacion = espacios.isEmpty() ? 0 : (ocupados * 100.0 / espacios.size());
                lblOcupacion.setText(String.format("%.1f%%", tasaOcupacion));

                long operadoresActivos = personal.stream()
                        .filter(p -> p.getRol().getIdRol() == MisConstantes.ROL_EMPLEADO).count();
                lblOperadores.setText(String.valueOf(operadoresActivos));

                lblTotalEmpleados.setText(String.valueOf(nomina.getTotalEmpleados()));
                lblNominaTotal.setText(String.format("$%.0f", nomina.getTotalNomina()));

                LocalDateTime hace7Dias = LocalDate.now().minusDays(7).atStartOfDay();
                double totalSemana = registros.stream()
                        .filter(r -> r.getHoraEntrada() != null && r.getHoraEntrada().isAfter(hace7Dias))
                        .mapToDouble(Registro::getMonto).sum();
                lblPromedioDia.setText(String.format("$%.0f", totalSemana / 7.0));

                cargarGrafica(registros);
                tablaEmpleados.getItems().setAll(personal);
            });
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cargarGrafica(List<Registro> registros) {
        barChart.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ingresos ($)");

        double ingresosNormal = registros.stream()
                .filter(r -> r.getTarifa() != null
                        && r.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_NORMAL)
                .mapToDouble(Registro::getMonto).sum();
        double ingresosPension = registros.stream()
                .filter(r -> r.getTarifa() != null
                        && r.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_PENSION)
                .mapToDouble(Registro::getMonto).sum();
        double ingresosEspecial = registros.stream()
                .filter(r -> r.getTarifa() != null
                        && r.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_ESPECIAL)
                .mapToDouble(Registro::getMonto).sum();
        double ingresosConvenio = registros.stream()
                .filter(r -> r.getTarifa() != null
                        && r.getTarifa().getTipoTarifa().getIdTipoTarifa() == MisConstantes.TARIFA_CONVENIO)
                .mapToDouble(Registro::getMonto).sum();

        serie.getData().add(new XYChart.Data<>("Normal", ingresosNormal));
        serie.getData().add(new XYChart.Data<>("Pensión", ingresosPension));
        serie.getData().add(new XYChart.Data<>("Especial", ingresosEspecial));
        serie.getData().add(new XYChart.Data<>("Convenio", ingresosConvenio));

        barChart.getData().add(serie);
        barChart.setLegendVisible(true);
        barChart.setAnimated(false);
    }
}
