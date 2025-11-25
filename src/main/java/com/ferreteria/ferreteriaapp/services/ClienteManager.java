package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase responsable de la gestión de Clientes en la base de datos SQLite.
 * Maneja la creación de tablas y operaciones CRUD con campos de nombre separados.
 */
public class ClienteManager implements IGestionable<Cliente> {

    private Connection connection;

    public ClienteManager() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaSiNoExiste();
    }

    /**
     * Crea la tabla 'clientes' con columnas separadas para nombres y apellidos.
     */
    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido_paterno TEXT NOT NULL," +
                "apellido_materno TEXT," +
                "telefono TEXT," +
                "correo TEXT," +
                "direccion TEXT" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla clientes: " + e.getMessage());
        }
    }

    @Override
    public void agregar(Cliente cliente) {
        String sql = "INSERT INTO clientes(nombre, apellido_paterno, apellido_materno, telefono, correo, direccion) VALUES(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellidoPaterno());
            pstmt.setString(3, cliente.getApellidoMaterno());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getCorreo());
            pstmt.setString(6, cliente.getDireccion());
            pstmt.executeUpdate();
            System.out.println("Cliente guardado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setApellidoPaterno(rs.getString("apellido_paterno"));
                c.setApellidoMaterno(rs.getString("apellido_materno"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                c.setDireccion(rs.getString("direccion"));
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        Cliente c = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidoPaterno(rs.getString("apellido_paterno"));
                    c.setApellidoMaterno(rs.getString("apellido_materno"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreo(rs.getString("correo"));
                    c.setDireccion(rs.getString("direccion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return c;
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre=?, apellido_paterno=?, apellido_materno=?, telefono=?, correo=?, direccion=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellidoPaterno());
            pstmt.setString(3, cliente.getApellidoMaterno());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getCorreo());
            pstmt.setString(6, cliente.getDireccion());
            pstmt.setInt(7, cliente.getId());

            int filas = pstmt.executeUpdate();
            if(filas > 0) System.out.println("Cliente actualizado.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Cliente eliminado.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
        }
    }
}