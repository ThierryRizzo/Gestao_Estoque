package com.senai.infraestructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Banco {
    private static final String URL = "jdbc:mysql://localhost:3306/GestaoEstoqueDB";
    private static final String USUARIO = "root";
    private static final String SENHA = "admin123";

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
