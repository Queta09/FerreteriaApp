package com.ferreteria.ferreteriaapp.model;

public class Producto {

    private int id;
    private String codigo;        // SKU o Código de Barras
    private String nombre;
    private String categoria;     // Ej. Herramientas, Plomería
    private double precioCosto;   // A cómo lo compras
    private double precioVenta;   // A cómo lo vendes
    private int stockActual;
    private int stockMinimo;      // Detonante de la alerta

    // RELACIÓN CON PROVEEDOR
    private int idProveedor;      // La llave foránea (Para guardar en BD)
    private String nombreProveedor; // Campo auxiliar (Solo para mostrar en la tabla)

    public Producto() {
    }

    public Producto(int id, String codigo, String nombre, String categoria, double precioCosto, double precioVenta, int stockActual, int stockMinimo, int idProveedor) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.idProveedor = idProveedor;
    }

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    // Getter/Setter especial para la Tabla (No se guarda en BD, se llena con JOIN)
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    // Lógica de Negocio: ¿Necesita reabastecer?
    public boolean estaBajoDeStock() {
        return stockActual <= stockMinimo;
    }

    @Override
    public String toString() {
        return nombre + " ($" + precioVenta + ")";
    }
}