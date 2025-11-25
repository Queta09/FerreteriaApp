package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoManager implements IGestionable<Empleado> {

    private Connection connection;

    public EmpleadoManager() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS empleados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido_paterno TEXT NOT NULL," +
                "apellido_materno TEXT," +
                "telefono TEXT," +
                "puesto TEXT," +
                "usuario TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creando tabla empleados: " + e.getMessage());
        }
    }

    @Override
    public void agregar(Empleado emp) {
        String sql = "INSERT INTO empleados(nombre, apellido_paterno, apellido_materno, telefono, puesto, usuario, password, rol) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.getNombre());
            pstmt.setString(2, emp.getApellidoPaterno());
            pstmt.setString(3, emp.getApellidoMaterno());
            pstmt.setString(4, emp.getTelefono());
            pstmt.setString(5, emp.getPuesto());
            pstmt.setString(6, emp.getUsuario());
            pstmt.setString(7, emp.getPassword());
            pstmt.setString(8, emp.getRol());
            pstmt.executeUpdate();
            System.out.println("Empleado agregado: " + emp.getUsuario());
        } catch (SQLException e) {
            System.err.println("Error guardando empleado: " + e.getMessage());
        }
    }

    @Override
    public List<Empleado> obtenerTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getInt("id"));

                e.setNombre(rs.getString("nombre"));
                e.setApellidoPaterno(rs.getString("apellido_paterno"));
                e.setApellidoMaterno(rs.getString("apellido_materno"));
                e.setTelefono(rs.getString("telefono"));
                e.setPuesto(rs.getString("puesto"));
                e.setUsuario(rs.getString("usuario"));
                e.setPassword(rs.getString("password"));
                e.setRol(rs.getString("rol"));

                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void actualizar(Empleado emp) {
        String sql = "UPDATE empleados SET nombre=?, apellido_paterno=?, apellido_materno=?, telefono=?, puesto=?, usuario=?, password=?, rol=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.getNombre());
            pstmt.setString(2, emp.getApellidoPaterno());
            pstmt.setString(3, emp.getApellidoMaterno());
            pstmt.setString(4, emp.getTelefono());
            pstmt.setString(5, emp.getPuesto());
            pstmt.setString(6, emp.getUsuario());
            pstmt.setString(7, emp.getPassword());
            pstmt.setString(8, emp.getRol());

            // El ID es el criterio para saber a cuál actualizar (WHERE id = ?)
            pstmt.setInt(9, emp.getId());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Empleado actualizado: " + emp.getUsuario());
            } else {
                System.err.println("No se encontró empleado con ID: " + emp.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error actualizando empleado: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM empleados WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Empleado eliminado con ID: " + id);
            } else {
                System.err.println("No se encontró empleado para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("Error eliminando empleado: " + e.getMessage());
        }
    }

    @Override
    public Empleado obtenerPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Empleado e = new Empleado();
                    e.setId(rs.getInt("id"));
                    e.setNombre(rs.getString("nombre"));
                    e.setApellidoPaterno(rs.getString("apellido_paterno"));
                    e.setApellidoMaterno(rs.getString("apellido_materno"));
                    e.setTelefono(rs.getString("telefono"));
                    e.setPuesto(rs.getString("puesto"));
                    e.setUsuario(rs.getString("usuario"));
                    e.setPassword(rs.getString("password"));
                    e.setRol(rs.getString("rol"));
                    return e;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}