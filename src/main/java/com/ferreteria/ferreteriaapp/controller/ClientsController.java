package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Cliente;
import com.ferreteria.ferreteriaapp.services.ClienteManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class ClientsController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colID;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colPaterno;
    @FXML private TableColumn<Cliente, String> colMaterno;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscar;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private ClienteManager clienteManager;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado;

    public void initialize() {
        clienteManager = new ClienteManager();
        listaClientes = FXCollections.observableArrayList();

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Filtro para permitir solo números en el teléfono
        txtTelefono.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtTelefono.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        cargarDatosTabla();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                mostrarDetallesCliente(clienteSeleccionado);
            }
        });
    }

    private void cargarDatosTabla() {
        listaClientes.clear();
        listaClientes.addAll(clienteManager.obtenerTodos());
        tablaClientes.setItems(listaClientes);
    }

    private void mostrarDetallesCliente(Cliente c) {
        txtNombre.setText(c.getNombre());
        txtPaterno.setText(c.getApellidoPaterno());
        txtMaterno.setText(c.getApellidoMaterno());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDireccion.setText(c.getDireccion());

        btnGuardar.setText("Actualizar");
        btnEliminar.setDisable(false);
    }

    @FXML
    protected void guardarCliente() {
        if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre y apellido paterno son obligatorios.");
            return;
        }

        if (clienteSeleccionado == null) {
            Cliente nuevo = new Cliente();
            llenarDatos(nuevo);
            clienteManager.agregar(nuevo);
            mostrarAlerta("Éxito", "Cliente guardado correctamente.");
        } else {
            llenarDatos(clienteSeleccionado);
            clienteManager.actualizar(clienteSeleccionado);
            mostrarAlerta("Éxito", "Cliente actualizado correctamente.");
        }

        limpiarFormulario();
        cargarDatosTabla();
    }

    private void llenarDatos(Cliente c) {
        c.setNombre(txtNombre.getText());
        c.setApellidoPaterno(txtPaterno.getText());
        c.setApellidoMaterno(txtMaterno.getText());
        c.setTelefono(txtTelefono.getText());
        c.setCorreo(txtCorreo.getText());
        c.setDireccion(txtDireccion.getText());
    }

    @FXML
    protected void eliminarCliente() {
        if (clienteSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Cliente");
        confirmacion.setContentText("¿Estás seguro de eliminar a " + clienteSeleccionado.getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            clienteManager.eliminar(clienteSeleccionado.getId());
            limpiarFormulario();
            cargarDatosTabla();
        }
    }

    @FXML
    protected void limpiarFormulario() {
        clienteSeleccionado = null;
        txtNombre.clear();
        txtPaterno.clear();
        txtMaterno.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();

        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaClientes.getSelectionModel().clearSelection();
    }

    @FXML
    protected void buscarCliente() {
        String texto = txtBuscar.getText().toLowerCase();
        if (texto.isEmpty()) {
            tablaClientes.setItems(listaClientes);
        } else {
            ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
            for (Cliente c : listaClientes) {
                if (c.getNombre().toLowerCase().contains(texto) ||
                        c.getApellidoPaterno().toLowerCase().contains(texto)) {
                    filtrados.add(c);
                }
            }
            tablaClientes.setItems(filtrados);
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