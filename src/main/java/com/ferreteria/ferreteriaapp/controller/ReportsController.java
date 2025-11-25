package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Empleado;
import com.ferreteria.ferreteriaapp.model.Venta;
import com.ferreteria.ferreteriaapp.services.ReporteManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class ReportsController {

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Label lblRolInfo;
    @FXML private Label lblTotalPeriodo;
    @FXML private Label lblGananciaPeriodo; // <--- NUEVO LABEL PARA ADMIN

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, String> colEmpleado;

    // Columnas Financieras
    @FXML private TableColumn<Venta, Double> colCosto;
    @FXML private TableColumn<Venta, Double> colGanancia;
    @FXML private TableColumn<Venta, Double> colTotal;

    private ReporteManager reporteManager;
    private Empleado empleadoActual;

    public void initialize() {
        reporteManager = new ReporteManager();
        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());

        // Configurar Columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Nuevas columnas de dinero
        colGanancia.setCellValueFactory(new PropertyValueFactory<>("ganancia"));
        // Costo es derivado: Total - Ganancia
        colCosto.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTotal() - cell.getValue().getGanancia()));

        colCliente.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCliente().getNombre() + " " + cell.getValue().getCliente().getApellidoPaterno()));
        colEmpleado.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEmpleado().getNombre()));

        // Dar formato de moneda a las celdas (Opcional pero se ve mejor)
        formatoMoneda(colTotal);
        formatoMoneda(colGanancia);
        formatoMoneda(colCosto);
    }

    private void formatoMoneda(TableColumn<Venta, Double> col) {
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });
    }

    public void setEmpleado(Empleado empleado) {
        this.empleadoActual = empleado;

        if ("ADMIN".equals(empleado.getRol())) {
            lblRolInfo.setText("VISTA GERENCIAL (Utilidades Desbloqueadas)");
            colCosto.setVisible(true);
            colGanancia.setVisible(true);
        } else {
            lblRolInfo.setText("VISTA VENDEDOR (Corte de Caja)");
            // Ocultar columnas sensibles
            colCosto.setVisible(false);
            colGanancia.setVisible(false);
        }

        generarReporte();
    }

    @FXML
    protected void generarReporte() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        if (inicio == null || fin == null) return;

        int idFiltro = "ADMIN".equals(empleadoActual.getRol()) ? -1 : empleadoActual.getId();

        List<Venta> ventas = reporteManager.obtenerVentasPorFiltro(inicio, fin, idFiltro);
        tablaVentas.setItems(FXCollections.observableArrayList(ventas));

        // Cálculos Finales
        double ventaBruta = ventas.stream().mapToDouble(Venta::getTotal).sum();
        double utilidadNeta = ventas.stream().mapToDouble(Venta::getGanancia).sum();

        if ("ADMIN".equals(empleadoActual.getRol())) {
            lblTotalPeriodo.setText(String.format("Ventas: $%.2f | Utilidad Neta: $%.2f", ventaBruta, utilidadNeta));
        } else {
            lblTotalPeriodo.setText(String.format("Total Caja: $%.2f", ventaBruta));
        }
    }
}