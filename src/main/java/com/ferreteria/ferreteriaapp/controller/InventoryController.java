package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.Producto;
import com.ferreteria.ferreteriaapp.model.Proveedor;
import com.ferreteria.ferreteriaapp.services.InventarioManager;
import com.ferreteria.ferreteriaapp.services.ProveedorManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colProveedor;

    @FXML private TextField txtCodigo, txtNombre, txtCosto, txtPrecio, txtStock, txtMinimo, txtBuscar;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ComboBox<Proveedor> cmbProveedor;

    @FXML private Button btnGuardar, btnEliminar;

    private InventarioManager inventarioManager;
    private ProveedorManager proveedorManager;
    private ObservableList<Producto> listaProductos;
    private Producto productoSeleccionado;

    public void initialize() {
        inventarioManager = new InventarioManager();
        proveedorManager = new ProveedorManager();
        listaProductos = FXCollections.observableArrayList();

        // Configuracion de columnas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));

        // RowFactory para alerta visual de stock bajo
        tablaProductos.setRowFactory(tv -> new TableRow<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.estaBajoDeStock()) {
                    setStyle("-fx-background-color: #ffcdd2;");
                } else {
                    setStyle("");
                }
            }
        });

        // Filtros de entrada para evitar caracteres no numericos en campos de stock
        txtStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtStock.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtMinimo.textProperty().addListener((obs, old, niu) -> {
            if (!niu.matches("\\d*")) {
                txtMinimo.setText(niu.replaceAll("[^\\d]", ""));
            }
        });

        cargarCategorias();
        cargarProveedores();
        cargarTabla();

        // Listener de seleccion de tabla
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, niu) -> {
            if (niu != null) {
                productoSeleccionado = niu;
                mostrarDetalles(niu);
            }
        });
    }

    private void cargarCategorias() {
        cmbCategoria.getItems().addAll("Herramientas Manuales", "Herramientas Eléctricas", "Plomería", "Electricidad", "Construcción", "Jardinería");
    }

    private void cargarProveedores() {
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedorManager.obtenerTodos()));
    }

    private void cargarTabla() {
        listaProductos.clear();
        listaProductos.addAll(inventarioManager.obtenerTodos());
        tablaProductos.setItems(listaProductos);
    }

    private void mostrarDetalles(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtCosto.setText(String.valueOf(p.getPrecioCosto()));
        txtPrecio.setText(String.valueOf(p.getPrecioVenta()));
        txtStock.setText(String.valueOf(p.getStockActual()));
        txtMinimo.setText(String.valueOf(p.getStockMinimo()));

        for (Proveedor prov : cmbProveedor.getItems()) {
            if (prov.getId() == p.getIdProveedor()) {
                cmbProveedor.setValue(prov);
                break;
            }
        }

        btnGuardar.setText("Actualizar");
        btnEliminar.setDisable(false);
    }

    @FXML
    protected void guardarProducto() {
        try {
            if (txtNombre.getText().isEmpty() || cmbProveedor.getValue() == null) {
                mostrarAlerta("Error", "Nombre y Proveedor son obligatorios.");
                return;
            }

            // Validacion de valores numericos positivos
            double costo = Double.parseDouble(txtCosto.getText());
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int minimo = Integer.parseInt(txtMinimo.getText());

            if (costo < 0 || precio < 0 || stock < 0 || minimo < 0) {
                mostrarAlerta("Error", "No se permiten valores negativos en precios o stock.");
                return;
            }

            Producto p = (productoSeleccionado == null) ? new Producto() : productoSeleccionado;

            p.setCodigo(txtCodigo.getText());
            p.setNombre(txtNombre.getText());
            p.setCategoria(cmbCategoria.getValue());

            // Asignacion de valores validados
            p.setPrecioCosto(costo);
            p.setPrecioVenta(precio);
            p.setStockActual(stock);
            p.setStockMinimo(minimo);

            p.setIdProveedor(cmbProveedor.getValue().getId());

            if (productoSeleccionado == null) {
                inventarioManager.agregar(p);
                mostrarAlerta("Éxito", "Producto registrado.");
            } else {
                inventarioManager.actualizar(p);
                mostrarAlerta("Éxito", "Producto actualizado.");
            }

            limpiarFormulario();
            cargarTabla();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Verifique que los precios y stock sean números válidos.");
        }
    }

    @FXML protected void eliminarProducto() {
        if (productoSeleccionado == null) return;
        inventarioManager.eliminar(productoSeleccionado.getId());
        limpiarFormulario();
        cargarTabla();
    }

    @FXML protected void limpiarFormulario() {
        productoSeleccionado = null;
        txtCodigo.clear(); txtNombre.clear(); cmbCategoria.setValue(null);
        txtCosto.clear(); txtPrecio.clear(); txtStock.clear(); txtMinimo.clear();
        cmbProveedor.setValue(null);
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaProductos.getSelectionModel().clearSelection();
    }

    @FXML protected void buscarProducto() {
        String filtro = txtBuscar.getText().toLowerCase();
        if(filtro.isEmpty()) { tablaProductos.setItems(listaProductos); return; }

        ObservableList<Producto> filtrados = FXCollections.observableArrayList();
        for(Producto p : listaProductos) {
            if(p.getNombre().toLowerCase().contains(filtro) ||
                    p.getCodigo().toLowerCase().contains(filtro) ||
                    p.getCategoria().toLowerCase().contains(filtro)) {
                filtrados.add(p);
            }
        }
        tablaProductos.setItems(filtrados);
    }

    private void mostrarAlerta(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }
}