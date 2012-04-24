package com.borisp.faces.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** A class providing connection to the database. */
public class DatabaseConnection {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/";

    /** Establishes a connection to the database. */
    public static Connection getConnection() throws SQLException {
        System.out.println("Initiating database connection");
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", USERNAME);
        connectionProps.put("password", PASSWORD);

        conn = DriverManager.getConnection(CONNECTION_STRING, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }
}
