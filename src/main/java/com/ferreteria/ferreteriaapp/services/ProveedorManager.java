package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorManager implements IGestionable<Proveedor> {

    private Connection connection;

    public ProveedorManager() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS proveedores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +           // Nombre del contacto
                "apellido_paterno TEXT," + // Apellido del contacto
                "apellido_materno TEXT," + // Apellido del contacto
                "telefono TEXT," +
                "nombre_empresa TEXT NOT NULL," + // Empresa
                "rfc TEXT," +
                "direccion TEXT," +
                "correo TEXT," +
                "dias_entrega TEXT" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void agregar(Proveedor p) {
        String sql = "INSERT INTO proveedores(nombre, apellido_paterno, apellido_materno, telefono, nombre_empresa, rfc, direccion, correo, dias_entrega) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getApellidoPaterno());
            pstmt.setString(3, p.getApellidoMaterno());
            pstmt.setString(4, p.getTelefono());
            pstmt.setString(5, p.getNombreEmpresa());
            pstmt.setString(6, p.getRfc());
            pstmt.setString(7, p.getDireccion());
            pstmt.setString(8, p.getCorreo());
            pstmt.setString(9, p.getDiasEntrega());
            pstmt.executeUpdate();
            System.out.println("Proveedor guardado: " + p.getNombreEmpresa());
        } catch (SQLException e) {
            System.err.println("Error al guardar proveedor: " + e.getMessage());
        }
    }

    @Override
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidoPaterno(rs.getString("apellido_paterno"));
                p.setApellidoMaterno(rs.getString("apellido_materno"));
                p.setTelefono(rs.getString("telefono"));

                // Datos propios
                p.setNombreEmpresa(rs.getString("nombre_empresa"));
                p.setRfc(rs.getString("rfc"));
                p.setDireccion(rs.getString("direccion"));
                p.setCorreo(rs.getString("correo"));
                p.setDiasEntrega(rs.getString("dias_entrega"));

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET nombre=?, apellido_paterno=?, apellido_materno=?, telefono=?, nombre_empresa=?, rfc=?, direccion=?, correo=?, dias_entrega=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getApellidoPaterno());
            pstmt.setString(3, p.getApellidoMaterno());
            pstmt.setString(4, p.getTelefono());
            pstmt.setString(5, p.getNombreEmpresa());
            pstmt.setString(6, p.getRfc());
            pstmt.setString(7, p.getDireccion());
            pstmt.setString(8, p.getCorreo());
            pstmt.setString(9, p.getDiasEntrega());
            pstmt.setInt(10, p.getId());
            pstmt.executeUpdate();
            System.out.println("Proveedor actualizado.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override public Proveedor obtenerPorId(int id) { return null; } // Pendiente si lo necesitas
}