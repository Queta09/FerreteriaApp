package com.ferreteria.ferreteriaapp.model;

public class DetalleVenta {
    private int id;
    private int idVenta;
    private Producto producto; // Guardamos el objeto completo para mostrar nombre/código en la tabla
    private int cantidad;
    private double precioUnitario; // Precio al momento de la venta
    private double subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }

    // Propiedades para la Tabla JavaFX
    public String getNombreProducto() { return producto.getNombre(); }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        // Recalcular subtotal automáticamente al cambiar cantidad
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}