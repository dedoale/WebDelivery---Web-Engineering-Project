package it.univaq.webdelivery.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionManager {
    private static String url;
    private static String username;
    private static String password;

    public static void initialize(String dbUrl, String dbUser, String dbPassword) throws ClassNotFoundException {
        url = dbUrl;
        username = dbUser;
        password = dbPassword;
        // Carica il driver JDBC di MySQL
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("DbConnectionManager non inizializzato. Chiamare prima il metodo initialize.");
        }
        return DriverManager.getConnection(url, username, password);
    }
}
