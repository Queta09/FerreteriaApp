package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.DetalleVenta;
import com.ferreteria.ferreteriaapp.model.Venta;
import java.sql.*;

/**
 * Gestiona las operaciones de venta en la base de datos.
 * Incluye el registro de transacciones, detalles de venta y actualización de inventario.
 *
 * @author Falcon
 * @version 1.0
 */
public class VentaManager {
    /**
     * Registra una venta completa en el sistema aplicando una transacción de base de datos.
     * <p>
     * Este método realiza tres acciones críticas de forma atómica:
     * 1. Inserta la cabecera de la venta y recupera el ID generado.
     * 2. Inserta cada detalle de la venta (productos vendidos).
     * 3. Descuenta el stock del inventario en tiempo real.
     * </p>
     * Si cualquiera de estos pasos falla, se realiza un ROLLBACK para evitar datos corruptos.
     *
     * @param venta El objeto Venta con la lista de productos y totales calculados.
     * @return true si la transacción fue exitosa; false si ocurrió algún error.
     */

    private Connection connection;

    public VentaManager() {
        this.connection = DatabaseConnection.getConnection();
        //ESTO ES LO QUE ASEGURA QUE LA TABLA EXISTA
        crearTablasSiNoExisten();
    }

    private void crearTablasSiNoExisten() {
        try (Statement stmt = connection.createStatement()) {
            // 1. Crear tabla VENTAS si no existe
            stmt.execute("CREATE TABLE IF NOT EXISTS ventas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "fecha TEXT," +
                    "total REAL," +
                    "descuento REAL," +
                    "cliente_id INTEGER," +
                    "empleado_id INTEGER," +
                    "FOREIGN KEY(cliente_id) REFERENCES clientes(id)," +
                    "FOREIGN KEY(empleado_id) REFERENCES empleados(id))");

            // 2. Crear tabla DETALLES si no existe
            stmt.execute("CREATE TABLE IF NOT EXISTS detalles_venta (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "venta_id INTEGER," +
                    "producto_id INTEGER," +
                    "cantidad INTEGER," +
                    "precio_unitario REAL," +
                    "subtotal REAL," +
                    "FOREIGN KEY(venta_id) REFERENCES ventas(id)," +
                    "FOREIGN KEY(producto_id) REFERENCES productos(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int registrarVenta(Venta venta) {
        // La consulta espera 5 parámetros: 1.Fecha, 2.Total, 3.Descuento, 4.Cliente, 5.Empleado
        String sqlVenta = "INSERT INTO ventas(fecha, total, descuento, cliente_id, empleado_id) VALUES(?, ?, ?, ?, ?)";

        String sqlDetalle = "INSERT INTO detalles_venta(venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES(?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id = ?";

        try {
            connection.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Insertar la Venta
            int idVentaGenerado = -1;

            try (PreparedStatement pstmt = connection.prepareStatement(sqlVenta)) {
                pstmt.setString(1, String.valueOf(venta.getFecha()));
                pstmt.setDouble(2, venta.getTotal());
                pstmt.setDouble(3, venta.getDescuento());

                // Parámetro 4: Cliente
                pstmt.setInt(4, venta.getCliente().getId());

                // Parámetro 5: Empleado (CORREGIDO)
                if (venta.getEmpleado() != null) {
                    pstmt.setInt(5, venta.getEmpleado().getId());
                } else {
                    pstmt.setNull(5, java.sql.Types.INTEGER);
                }

                pstmt.executeUpdate();
            }

            // Recuperar ID generado (Método compatible con SQLite)
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    idVentaGenerado = rs.getInt(1);
                }
            }

            if (idVentaGenerado <= 0) {
                throw new SQLException("No se pudo obtener el ID de la venta.");
            }

            // 2. Insertar Detalles y Actualizar Stock
            try (PreparedStatement pstDetalle = connection.prepareStatement(sqlDetalle);
                 PreparedStatement pstStock = connection.prepareStatement(sqlStock)) {

                for (DetalleVenta dv : venta.getDetalles()) {
                    // Guardar Detalle
                    pstDetalle.setInt(1, idVentaGenerado);
                    pstDetalle.setInt(2, dv.getProducto().getId());
                    pstDetalle.setInt(3, dv.getCantidad());
                    pstDetalle.setDouble(4, dv.getPrecioUnitario());
                    pstDetalle.setDouble(5, dv.getSubtotal());
                    pstDetalle.executeUpdate();

                    // Restar Stock
                    pstStock.setInt(1, dv.getCantidad());
                    pstStock.setInt(2, dv.getProducto().getId());
                    pstStock.executeUpdate();
                }
            }

            connection.commit(); // CONFIRMAR CAMBIOS
            connection.setAutoCommit(true);
            System.out.println("Venta registrada exitosamente. ID: " + idVentaGenerado);
            return idVentaGenerado;

        } catch (SQLException e) {
            try {
                System.err.println("Error en transacción venta: " + e.getMessage());
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
    }
}