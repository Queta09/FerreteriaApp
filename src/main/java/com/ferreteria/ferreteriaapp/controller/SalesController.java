package com.ferreteria.ferreteriaapp.controller;

import com.ferreteria.ferreteriaapp.model.*;
import com.ferreteria.ferreteriaapp.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesController {

    // UI Clientes
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblFecha, lblTotal;
    @FXML private TextField txtDescuento;
    @FXML private Label lblSubtotal;

    // UI Busqueda Producto
    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<Producto> tablaBusqueda;
    @FXML private TableColumn<Producto, String> colProdNombre;
    @FXML private TableColumn<Producto, Double> colProdPrecio;
    @FXML private TableColumn<Producto, Integer> colProdStock;
    @FXML private Spinner<Integer> spinCantidad;

    // UI Carrito
    @FXML private TableView<DetalleVenta> tablaCarrito;
    @FXML private TableColumn<DetalleVenta, String> colCarProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCarCant;
    @FXML private TableColumn<DetalleVenta, Double> colCarPrecio;
    @FXML private TableColumn<DetalleVenta, Double> colCarTotal;

    @FXML private TextField txtPago;
    @FXML private Label lblCambio;

    // Lógica
    private ClienteManager clienteManager;
    private InventarioManager inventarioManager;
    private VentaManager ventaManager;

    private ObservableList<Producto> catalogoProductos;
    private ObservableList<DetalleVenta> carritoCompras;

    // Necesitamos saber quién es el empleado logueado (se pasará desde el Dashboard)
    private Empleado empleadoActual;

    public void initialize() {

        clienteManager = new ClienteManager();
        inventarioManager = new InventarioManager();
        ventaManager = new VentaManager();

        carritoCompras = FXCollections.observableArrayList();

        lblFecha.setText("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        spinCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        configurarTablas();
        cargarDatos();

        // Filtro de búsqueda dinámica
        txtBuscarProducto.textProperty().addListener((obs, old, niu) -> filtrarProductos(niu));

        cmbCliente.setConverter(new javafx.util.StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return (c == null) ? null : c.getNombre() + " " + c.getApellidoPaterno();
            }

            @Override
            public Cliente fromString(String string) {
                return null; // No necesitamos conversión inversa por ahora
            }
        });

        tablaBusqueda.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Cada vez que seleccionas un nuevo producto, pon el spinner en 1
                spinCantidad.getValueFactory().setValue(1);
            }
        });

        txtDescuento.textProperty().addListener((obs, old, niu) -> calcularTotal());

        txtDescuento.textProperty().addListener((obs, old, niu) -> {
            calcularTotal();
            calcularCambio(); // <--- Recalcular cambio si cambia el descuento
        });

        // NUEVO LISTENER
        txtPago.textProperty().addListener((obs, old, niu) -> calcularCambio());
    }

    private void calcularCambio() {
        try {
            // Obtenemos el total actual (limpiando el signo $)
            String totalStr = lblTotal.getText().replace("$", "").replace(",", "");
            double total = Double.parseDouble(totalStr);

            double pago = Double.parseDouble(txtPago.getText());

            if (pago < total) {
                lblCambio.setText("Faltan: $" + String.format("%.2f", total - pago));
                lblCambio.setStyle("-fx-text-fill: red;");
            } else {
                double cambio = pago - total;
                lblCambio.setText("$" + String.format("%.2f", cambio));
                lblCambio.setStyle("-fx-text-fill: green;");
            }
        } catch (NumberFormatException e) {
            lblCambio.setText("$0.00");
        }
    }

    // Método setter para que el Dashboard nos pase el usuario
    public void setEmpleado(Empleado emp) {
        this.empleadoActual = emp;
    }

    private void configurarTablas() {
        // Tabla Búsqueda
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));

        // Tabla Carrito
        colCarProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCarCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCarTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tablaCarrito.setItems(carritoCompras);
    }

    private void cargarDatos() {
        cmbCliente.setItems(FXCollections.observableArrayList(clienteManager.obtenerTodos()));
        catalogoProductos = FXCollections.observableArrayList(inventarioManager.obtenerTodos());
        tablaBusqueda.setItems(catalogoProductos);
    }

    private void filtrarProductos(String busqueda) {
        if (busqueda.isEmpty()) {
            tablaBusqueda.setItems(catalogoProductos);
        } else {
            ObservableList<Producto> filtrados = FXCollections.observableArrayList();
            for (Producto p : catalogoProductos) {
                if (p.getNombre().toLowerCase().contains(busqueda.toLowerCase()) ||
                        p.getCodigo().toLowerCase().contains(busqueda.toLowerCase())) {
                    filtrados.add(p);
                }
            }
            tablaBusqueda.setItems(filtrados);
        }
    }

    @FXML
    protected void agregarProducto() {
        Producto p = tablaBusqueda.getSelectionModel().getSelectedItem();
        if (p == null) {
            mostrarAlerta("Selección", "Seleccione un producto de la lista izquierda.");
            return;
        }

        int cantidad = spinCantidad.getValue();

        // Validación de Stock
        if (cantidad > p.getStockActual()) {
            mostrarAlerta("Stock Insuficiente", "Solo quedan " + p.getStockActual() + " unidades.");
            return;
        }

        // Crear detalle y agregar al carrito
        DetalleVenta detalle = new DetalleVenta(p, cantidad);
        carritoCompras.add(detalle);
        calcularTotal();

        // Restar "visualmente" el stock de la lista de búsqueda para no vender doble
        p.setStockActual(p.getStockActual() - cantidad);
        tablaBusqueda.refresh();
    }

    @FXML
    protected void quitarUnoDelCarrito() { // <--- CAMBIO DE NOMBRE
        DetalleVenta dv = tablaCarrito.getSelectionModel().getSelectedItem();
        if (dv != null) {
            // 1. Devolver 1 unidad al stock visual
            dv.getProducto().setStockActual(dv.getProducto().getStockActual() + 1);

            // 2. Lógica del carrito
            if (dv.getCantidad() > 1) {
                dv.setCantidad(dv.getCantidad() - 1);
                tablaCarrito.refresh();
            } else {
                carritoCompras.remove(dv);
            }
            tablaBusqueda.refresh();
            calcularTotal();
            calcularCambio(); // Si tienes el cálculo de cambio implementado
        }
    }

    @FXML
    protected void borrarFilaDelCarrito() {
        DetalleVenta dv = tablaCarrito.getSelectionModel().getSelectedItem();
        if (dv != null) {
            // 1. Devolver TODO el stock visualmente
            int cantidadDevuelta = dv.getCantidad();
            dv.getProducto().setStockActual(dv.getProducto().getStockActual() + cantidadDevuelta);

            // 2. Eliminar la fila completa sin preguntar
            carritoCompras.remove(dv);

            // 3. Actualizar interfaz
            tablaBusqueda.refresh();
            calcularTotal();
            calcularCambio(); // Recalcular el cambio/pago
        } else {
            mostrarAlerta("Selección", "Seleccione un producto del carrito para borrar.");
        }
    }

    private void calcularTotal() {
        double subtotal = 0;
        for (DetalleVenta dv : carritoCompras) {
            subtotal += dv.getSubtotal();
        }

        double descuento = 0;
        try {
            descuento = Double.parseDouble(txtDescuento.getText());
        } catch (NumberFormatException e) {
            descuento = 0;
        }

        double totalFinal = subtotal - descuento;
        if (totalFinal < 0) totalFinal = 0;

        // CORRECCIÓN VISUAL:
        lblSubtotal.setText(String.format("$%.2f", subtotal)); // El subtotal es la suma real de los items
        lblTotal.setText(String.format("$%.2f", totalFinal));
    }

    @FXML
    protected void finalizarVenta() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Falta Cliente", "Por favor seleccione un cliente.");
            return;
        }
        if (carritoCompras.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "Agregue productos antes de finalizar.");
            return;
        }

        // Construir el objeto Venta
        // NOTA: Si empleadoActual es null (por pruebas directas), asignamos uno por defecto o lanzamos error
        if (empleadoActual == null) {
            System.out.println("Advertencia: Empleado nulo, asegúrate de pasar el empleado desde el Dashboard.");
        }

        Venta venta = new Venta(cmbCliente.getValue(), empleadoActual);
        try {
            venta.setDescuento(Double.parseDouble(txtDescuento.getText()));
        } catch (Exception e) { venta.setDescuento(0); }
        for (DetalleVenta dv : carritoCompras) {
            venta.agregarDetalle(dv);
        }

        // Guardar en BD
        int idVenta= ventaManager.registrarVenta(venta);

        if (idVenta > 0) {
            venta.setId(idVenta);

            // Obtener el pago del TextField
            double pagoReal = 0;
            try {
                pagoReal = Double.parseDouble(txtPago.getText());
            } catch (Exception e) { pagoReal = venta.getTotal(); } // Si está vacío, asumimos pago exacto

            // Pasamos el pago al generador
            new TicketManager().generarTicketPDF(venta, pagoReal);

            mostrarAlerta("Venta Exitosa", "Venta guardada y Ticket generado");

            carritoCompras.clear();
            calcularTotal();
            catalogoProductos.setAll(inventarioManager.obtenerTodos());
        } else {
            mostrarAlerta("Error", "Hubo un problema al registrar la venta.");
        }
    }

    private void mostrarAlerta(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }
}