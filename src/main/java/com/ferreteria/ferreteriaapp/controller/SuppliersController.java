package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Proveedor;
import com.ferreteria.ferreteriaapp.services.ProveedorManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class SuppliersController {

    // --- TABLA Y COLUMNAS ---
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, Integer> colID;
    @FXML private TableColumn<Proveedor, String> colEmpresa;
    @FXML private TableColumn<Proveedor, String> colRFC;
    @FXML private TableColumn<Proveedor, String> colContacto; // Usaremos 'nombre' del agente
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colDias;

    // --- FORMULARIO EMPRESA ---
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtRFC;
    @FXML private TextField txtDias;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCorreo;

    // --- FORMULARIO CONTACTO ---
    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private TextField txtTelefono;

    // --- UTILIDADES ---
    @FXML private TextField txtBuscar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private ProveedorManager proveedorManager;
    private ObservableList<Proveedor> listaProveedores;
    private Proveedor proveedorSeleccionado;

    public void initialize() {
        proveedorManager = new ProveedorManager();
        listaProveedores = FXCollections.observableArrayList();

        // 1. Configurar Columnas
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        colRFC.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("nombre")); // Muestra nombre del vendedor
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasEntrega"));

        // 2. Cargar datos
        cargarDatosTabla();

        // 3. Listener de selección
        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                proveedorSeleccionado = newVal;
                mostrarDetalles(proveedorSeleccionado);
            }
        });
    }

    private void cargarDatosTabla() {
        listaProveedores.clear();
        listaProveedores.addAll(proveedorManager.obtenerTodos());
        tablaProveedores.setItems(listaProveedores);
    }

    private void mostrarDetalles(Proveedor p) {
        // Empresa
        txtEmpresa.setText(p.getNombreEmpresa());
        txtRFC.setText(p.getRfc());
        txtDias.setText(p.getDiasEntrega());
        txtDireccion.setText(p.getDireccion());
        txtCorreo.setText(p.getCorreo());

        // Contacto
        txtNombre.setText(p.getNombre());
        txtPaterno.setText(p.getApellidoPaterno());
        txtMaterno.setText(p.getApellidoMaterno());
        txtTelefono.setText(p.getTelefono());

        btnGuardar.setText("Actualizar");
        btnEliminar.setDisable(false);
    }

    @FXML
    protected void guardarProveedor() {
        if (txtEmpresa.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre de la empresa es obligatorio.");
            return;
        }

        if (proveedorSeleccionado == null) {
            // CREAR
            Proveedor nuevo = new Proveedor();
            llenarDatos(nuevo);
            proveedorManager.agregar(nuevo);
            mostrarAlerta("Éxito", "Proveedor registrado correctamente.");
        } else {
            // ACTUALIZAR
            llenarDatos(proveedorSeleccionado);
            proveedorManager.actualizar(proveedorSeleccionado);
            mostrarAlerta("Éxito", "Proveedor actualizado correctamente.");
        }
        limpiarFormulario();
        cargarDatosTabla();
    }

    private void llenarDatos(Proveedor p) {
        // Empresa
        p.setNombreEmpresa(txtEmpresa.getText());
        p.setRfc(txtRFC.getText());
        p.setDiasEntrega(txtDias.getText());
        p.setDireccion(txtDireccion.getText());
        p.setCorreo(txtCorreo.getText());

        // Contacto
        p.setNombre(txtNombre.getText());
        p.setApellidoPaterno(txtPaterno.getText());
        p.setApellidoMaterno(txtMaterno.getText());
        p.setTelefono(txtTelefono.getText());
    }

    @FXML
    protected void eliminarProveedor() {
        if (proveedorSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Proveedor");
        alert.setContentText("¿Borrar a " + proveedorSeleccionado.getNombreEmpresa() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            proveedorManager.eliminar(proveedorSeleccionado.getId());
            limpiarFormulario();
            cargarDatosTabla();
        }
    }

    @FXML
    protected void limpiarFormulario() {
        proveedorSeleccionado = null;
        // Limpiar campos empresa
        txtEmpresa.clear(); txtRFC.clear(); txtDias.clear(); txtDireccion.clear(); txtCorreo.clear();
        // Limpiar campos contacto
        txtNombre.clear(); txtPaterno.clear(); txtMaterno.clear(); txtTelefono.clear();

        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaProveedores.getSelectionModel().clearSelection();
    }

    @FXML
    protected void buscarProveedor() {
        String busqueda = txtBuscar.getText().toLowerCase();
        if (busqueda.isEmpty()) {
            tablaProveedores.setItems(listaProveedores);
        } else {
            ObservableList<Proveedor> filtrados = FXCollections.observableArrayList();
            for (Proveedor p : listaProveedores) {
                if (p.getNombreEmpresa().toLowerCase().contains(busqueda) ||
                        (p.getRfc() != null && p.getRfc().toLowerCase().contains(busqueda))) {
                    filtrados.add(p);
                }
            }
            tablaProveedores.setItems(filtrados);
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