package com.ferreteria.ferreteriaapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private double total;
    private Cliente cliente;
    private Empleado empleado;
    private List<DetalleVenta> detalles; // Lista de items
    private double ganancia;
    private double descuento;

    public Venta(Cliente cliente, Empleado empleado) {
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        this.total += detalle.getSubtotal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public double getGanancia() {
        return ganancia;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }

    public double getTotalNeto() {
        return total - descuento;
    }
}