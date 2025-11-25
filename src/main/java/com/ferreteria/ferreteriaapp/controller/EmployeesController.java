package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Empleado;
import com.ferreteria.ferreteriaapp.services.EmpleadoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeesController {

    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private TableColumn<Empleado, String> colNombre, colPuesto, colUsuario, colRol;
    @FXML private TextField txtNombre, txtPaterno, txtMaterno, txtPuesto, txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Button btnEliminar;

    private EmpleadoManager manager;
    private Empleado seleccionado;

    public void initialize() {
        manager = new EmpleadoManager();

        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // O usa un SimpleStringProperty para nombre completo
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Llenar combo de roles
        cmbRol.setItems(FXCollections.observableArrayList("ADMIN", "VENDEDOR"));

        cargarTabla();

        // Listener
        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, old, niu) -> {
            if (niu != null) {
                seleccionado = niu;
                llenarFormulario(niu);
            }
        });
    }

    private void cargarTabla() {
        tablaEmpleados.setItems(FXCollections.observableArrayList(manager.obtenerTodos()));
    }

    private void llenarFormulario(Empleado e) {
        txtNombre.setText(e.getNombre());
        txtPaterno.setText(e.getApellidoPaterno());
        txtMaterno.setText(e.getApellidoMaterno());
        txtPuesto.setText(e.getPuesto());
        txtUsuario.setText(e.getUsuario());
        txtPassword.setText(e.getPassword());
        cmbRol.setValue(e.getRol());
        btnEliminar.setDisable(false);
    }

    @FXML
    protected void guardar() {
        if (txtUsuario.getText().isEmpty() || cmbRol.getValue() == null) {
            mostrarAlerta("Error", "Usuario y Rol son obligatorios.");
            return;
        }

        if (seleccionado == null) {
            Empleado nuevo = new Empleado();
            datosDeFormulario(nuevo);
            manager.agregar(nuevo); // Asegúrate que tu Manager tenga este método
            mostrarAlerta("Éxito", "Empleado creado.");
        } else {
            datosDeFormulario(seleccionado); // Actualiza el objeto en memoria con lo que escribiste en los TextFields
            manager.actualizar(seleccionado); // Manda el cambio a la Base de Datos
            mostrarAlerta("Éxito", "Datos del empleado actualizados.");
        }
        limpiar();
        cargarTabla();
    }

    private void datosDeFormulario(Empleado e) {
        e.setNombre(txtNombre.getText());
        e.setApellidoPaterno(txtPaterno.getText());
        e.setApellidoMaterno(txtMaterno.getText());
        e.setPuesto(txtPuesto.getText());
        e.setUsuario(txtUsuario.getText());
        e.setPassword(txtPassword.getText());
        e.setRol(cmbRol.getValue());
        // Teléfono por defecto si no lo pones en el formulario
        e.setTelefono("000-000-0000");
    }

    @FXML
    protected void eliminar() {
        if (seleccionado != null) {
            manager.eliminar(seleccionado.getId());
            mostrarAlerta("Éxito", "Empleado eliminado.");
            limpiar();
            cargarTabla();
        }
    }

    @FXML
    protected void limpiar() {
        seleccionado = null;
        txtNombre.clear(); txtPaterno.clear(); txtMaterno.clear();
        txtPuesto.clear(); txtUsuario.clear(); txtPassword.clear();
        cmbRol.setValue(null);
        btnEliminar.setDisable(true);
        tablaEmpleados.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }
}