package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Empleado;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private Label lblUsuario;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button btnEmpleados;

    private Empleado empleadoActual;

    public void setEmpleado(Empleado empleado) {
        this.empleadoActual = empleado;
        lblUsuario.setText("Usuario: " + empleado.getUsuario());

        // SEGURIDAD: Si no es ADMIN, ocultamos y desactivamos el botón de empleados
        if (!"ADMIN".equals(empleado.getRol())) {
            btnEmpleados.setVisible(false);
            btnEmpleados.setManaged(false); // Para que no ocupe espacio visual
        } else {
            btnEmpleados.setVisible(true);
            btnEmpleados.setManaged(true);
        }
    }

    @FXML
    protected void mostrarEmpleados() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/employees-view.fxml"));
            mainPane.setCenter(loader.load());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    protected void mostrarVentas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/sales-view.fxml"));
            javafx.scene.Parent view = loader.load();

            // Obtener el controlador y pasarle el empleado
            SalesController controller = loader.getController();
            controller.setEmpleado(this.empleadoActual);

            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void mostrarClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/clients-view.fxml"));
            mainPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de Clientes.");
        }
    }

    @FXML
    protected void mostrarInventario() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/inventory-view.fxml"));
            mainPane.setCenter(loader.load());
        }catch (IOException e){
            e.printStackTrace();
            mostrarAlerta("Error",  "No se pudo cargar la vista de Inventario.");
        }
    }

    @FXML
    protected void mostrarProveedores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/suppliers-view.fxml"));
            mainPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el módulo de Proveedores.");
        }
    }

    @FXML
    protected void mostrarReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/reports-view.fxml"));
            javafx.scene.Parent view = loader.load();

            // Pasar el empleado para la lógica de seguridad
            ReportsController controller = loader.getController();
            controller.setEmpleado(this.empleadoActual);

            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el módulo de Reportes.");
        }
    }

    @FXML
    protected void cerrarSesion() {
        // Volver al Login
        try {
            Stage stage = (Stage) lblUsuario.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Login - Ferretería");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }


}