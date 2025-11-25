package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servicio de seguridad y autenticación.
 * Gestiona el acceso al sistema validando credenciales contra la base de datos.
 */

public class AuthService {
    /**
     * Verifica las credenciales de un usuario y recupera su perfil completo.
     *
     * @param usuario El nombre de usuario ingresado.
     * @param password La contraseña (en texto plano para este prototipo).
     * @return El objeto {@link Empleado} con su ROL (ADMIN/VENDEDOR) si las credenciales son válidas,
     * o null si la autenticación falla.
     */

    private Connection connection;

    public AuthService() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Verifica las credenciales contra la base de datos.
     * @return El objeto Empleado si el login es correcto, o null si falla.
     */
    public Empleado login(String usuario, String password) {
        String sql = "SELECT * FROM empleados WHERE usuario = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // ¡Login exitoso! Reconstruimos el objeto Empleado
                    Empleado empleado = new Empleado();
                    empleado.setId(rs.getInt("id"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellidoPaterno(rs.getString("apellido_paterno"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setUsuario(rs.getString("usuario"));
                    empleado.setRol(rs.getString("rol"));
                    // Por seguridad, no es necesario guardar el password en memoria si no se usa
                    return empleado;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null; // Credenciales incorrectas
    }
}
