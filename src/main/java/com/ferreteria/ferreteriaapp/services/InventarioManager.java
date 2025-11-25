package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioManager implements IGestionable<Producto> {

    private Connection connection;

    public InventarioManager() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        // SQL con Foreign Key hacia la tabla proveedores
        String sql = "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "codigo TEXT UNIQUE," +
                "nombre TEXT NOT NULL," +
                "categoria TEXT," +
                "precio_costo REAL," +
                "precio_venta REAL," +
                "stock_actual INTEGER," +
                "stock_minimo INTEGER," +
                "proveedor_id INTEGER," +
                "FOREIGN KEY(proveedor_id) REFERENCES proveedores(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void agregar(Producto p) {
        String sql = "INSERT INTO productos(codigo, nombre, categoria, precio_costo, precio_venta, stock_actual, stock_minimo, proveedor_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getCategoria());
            pstmt.setDouble(4, p.getPrecioCosto());
            pstmt.setDouble(5, p.getPrecioVenta());
            pstmt.setInt(6, p.getStockActual());
            pstmt.setInt(7, p.getStockMinimo());
            pstmt.setInt(8, p.getIdProveedor()); // Guardamos el ID numérico
            pstmt.executeUpdate();
            System.out.println("Producto agregado: " + p.getNombre());
        } catch (SQLException e) {
            System.err.println("Error al agregar producto: " + e.getMessage());
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        // JOIN: Traemos todos los datos del producto + el nombre de la empresa proveedora
        String sql = "SELECT p.*, prov.nombre_empresa FROM productos p " +
                "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecioCosto(rs.getDouble("precio_costo"));
                p.setPrecioVenta(rs.getDouble("precio_venta"));
                p.setStockActual(rs.getInt("stock_actual"));
                p.setStockMinimo(rs.getInt("stock_minimo"));
                p.setIdProveedor(rs.getInt("proveedor_id"));

                // Llenamos el campo auxiliar con el resultado del JOIN
                p.setNombreProveedor(rs.getString("nombre_empresa"));

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método especial para el Dashboard o Alertas
    public List<Producto> obtenerProductosBajosDeStock() {
        List<Producto> alertas = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock_actual <= stock_minimo";
        // ... (Implementación similar a obtenerTodos) ...
        // Por brevedad, puedes copiar la lógica del try-catch de arriba
        return alertas;
    }

    @Override public void actualizar(Producto p) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, categoria=?, precio_costo=?, precio_venta=?, stock_actual=?, stock_minimo=?, proveedor_id=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getCategoria());
            pstmt.setDouble(4, p.getPrecioCosto());
            pstmt.setDouble(5, p.getPrecioVenta());
            pstmt.setInt(6, p.getStockActual());
            pstmt.setInt(7, p.getStockMinimo());
            pstmt.setInt(8, p.getIdProveedor());
            pstmt.setInt(9, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override public void eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override public Producto obtenerPorId(int id) { return null; }
}