package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.services.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaBaseDatos {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE BASE DE DATOS ---");

        // 1. Intentar conectar
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn != null) {
                System.out.println("✅ Conexión exitosa a SQLite.");

                // 2. Crear la tabla de Clientes (si no existe)
                String sqlCreate = "CREATE TABLE IF NOT EXISTS clientes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nombre TEXT NOT NULL," +
                        "telefono TEXT," +
                        "correo TEXT," +
                        "direccion TEXT" +
                        ");";

                Statement stmt = conn.createStatement();
                stmt.execute(sqlCreate);
                System.out.println("Tabla 'clientes' verificada/creada.");

                // 3. Insertar un Cliente de prueba
                String sqlInsert = "INSERT INTO clientes(nombre, telefono, correo, direccion) VALUES(?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sqlInsert);
                pstmt.setString(1, "Juan Pérez");
                pstmt.setString(2, "555-1234");
                pstmt.setString(3, "juan@test.com");
                pstmt.setString(4, "Calle Prueba 123");
                pstmt.executeUpdate();

                System.out.println("Cliente de prueba insertado correctamente.");

                // 4. Leer para confirmar que se guardó
                String sqlSelect = "SELECT * FROM clientes";
                ResultSet rs = stmt.executeQuery(sqlSelect);

                System.out.println("\n--- DATOS EN LA BASE DE DATOS ---");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            " | Nombre: " + rs.getString("nombre") +
                            " | Correo: " + rs.getString("correo"));
                }

            } else {
                System.out.println("No se pudo conectar.");
            }

        } catch (Exception e) {
            System.out.println("Error grave: " + e.getMessage());
            e.printStackTrace();
        }
    }
}