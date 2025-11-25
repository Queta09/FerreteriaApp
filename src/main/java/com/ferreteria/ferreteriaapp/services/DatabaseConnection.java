package com.ferreteria.ferreteriaapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:ferreteria.db";

    public static Connection getConnection() {
        try{
            return DriverManager.getConnection(URL);
        }catch(SQLException e){
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return null;
        }
    }
}
