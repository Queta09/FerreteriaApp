package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Empleado;
import com.ferreteria.ferreteriaapp.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    private AuthService authService;

    // Este método se ejecuta automáticamente al cargar la vista
    public void initialize() {
        authService = new AuthService();
    }

    @FXML
    protected void onLoginButtonClick() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Por favor, llene todos los campos.");
            return;
        }

        // Llamamos a la lógica de negocio
        Empleado empleadoLogueado = authService.login(usuario, password);

        if (empleadoLogueado != null) {
            try {
                // 1. Cargar el FXML del Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ferreteria/ferreteriaapp/main-view.fxml"));
                Scene dashboardScene = new Scene(loader.load(), 800, 600);

                // 2. Cargar estilos de BootstrapFX (Opcional, pero recomendado)
                // dashboardScene.getStylesheets().add(org.kordamp.bootstrapfx.scene.layout.Panel.class.getResource("bootstrapfx.css").toExternalForm());

                // 3. Obtener el controlador del Dashboard y pasarle el usuario
                MainController mainController = loader.getController();
                mainController.setEmpleado(empleadoLogueado);

                // 4. Obtener el escenario actual (Stage) y cambiar la escena
                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Sistema de Gestión Ferretera - Dashboard");
                stage.centerOnScreen(); // Centrar ventana

            } catch (IOException e) {
                e.printStackTrace();
                lblMensaje.setText("Error al cargar el menú principal.");
            }
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos.");
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