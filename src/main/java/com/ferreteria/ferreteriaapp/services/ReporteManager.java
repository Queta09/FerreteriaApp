package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Cliente;
import com.ferreteria.ferreteriaapp.model.Empleado;
import com.ferreteria.ferreteriaapp.model.Venta;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la generación de estadísticas y reportes financieros.
 * Utiliza consultas SQL avanzadas para calcular utilidades y filtrar información sensible.
 */

public class ReporteManager {
    /**
     * Obtiene un listado de ventas filtrado por rango de fechas y rol de usuario.
     * <p>
     * Incluye una subconsulta SQL para calcular el costo total de los productos vendidos
     * y determinar la ganancia neta (Utilidad) por venta.
     * </p>
     *
     * @param inicio Fecha inicial del reporte.
     * @param fin Fecha final del reporte.
     * @param idEmpleado ID del empleado para filtrar (VENDEDOR) o -1 para ver todo (ADMIN).
     * @return Lista de ventas con los datos financieros calculados.
     */

    private Connection connection;

    public ReporteManager() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Venta> obtenerVentasPorFiltro(LocalDate inicio, LocalDate fin, int idEmpleado) {
        List<Venta> ventas = new ArrayList<>();

        // 1. Convertimos las fechas de Java a String
        String fechaInicio = inicio.toString(); // "2025-11-01"
        String fechaFin = fin.toString();       // "2025-11-24"

        System.out.println("Buscando ventas entre: " + fechaInicio + " y " + fechaFin);

        // 2. Query con LEFT JOIN y filtro de fecha por string simple (YYYY-MM-DD)
        // Usamos 'v.fecha LIKE ?' para buscar coincidencias parciales si el rango falla,
        // pero primero probemos el rango estándar con substr.
        StringBuilder sql = new StringBuilder(
                "SELECT v.id, v.fecha, v.total, v.descuento, " + // Agregué v.descuento por si acaso
                        "c.id as cid, c.nombre as cnombre, c.apellido_paterno as cpaterno, " +
                        "e.id as eid, e.nombre as enombre, " +
                        "(SELECT SUM(d.cantidad * p.precio_costo) " +
                        " FROM detalles_venta d " +
                        " JOIN productos p ON d.producto_id = p.id " +
                        " WHERE d.venta_id = v.id) as costo_total " +
                        "FROM ventas v " +
                        "LEFT JOIN clientes c ON v.cliente_id = c.id " +   // LEFT JOIN vital
                        "LEFT JOIN empleados e ON v.empleado_id = e.id " + // LEFT JOIN vital
                        "WHERE substr(v.fecha, 1, 10) >= ? AND substr(v.fecha, 1, 10) <= ? " // Comparación de texto
        );

        if (idEmpleado != -1) {
            sql.append("AND v.empleado_id = ? ");
        }

        sql.append("ORDER BY v.id DESC"); // Ordenar por ID es más seguro que por fecha si el formato falla

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);

            if (idEmpleado != -1) {
                pstmt.setInt(3, idEmpleado);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Debug por cada fila encontrada
                    System.out.println("Encontrada venta ID: " + rs.getInt("id"));

                    // Construcción de objetos (Manejo de Nulos)
                    Cliente c = new Cliente();
                    if (rs.getInt("cid") != 0) {
                        c.setId(rs.getInt("cid"));
                        c.setNombre(rs.getString("cnombre"));
                        c.setApellidoPaterno(rs.getString("cpaterno"));
                    } else {
                        c.setNombre("Cliente Genérico");
                        c.setApellidoPaterno("");
                    }

                    Empleado e = new Empleado();
                    if (rs.getInt("eid") != 0) {
                        e.setId(rs.getInt("eid"));
                        e.setNombre(rs.getString("enombre"));
                    } else {
                        e.setNombre("Desconocido");
                    }

                    Venta v = new Venta(c, e);
                    v.setId(rs.getInt("id"));
                    v.setTotal(rs.getDouble("total"));

                    double costo = rs.getDouble("costo_total");
                    double ganancia = v.getTotal() - costo;
                    v.setGanancia(ganancia);

                    ventas.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Total resultados: " + ventas.size());
        return ventas;
    }
}