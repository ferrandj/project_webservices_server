package fr.uge.database;

import java.sql.*;
import java.util.Objects;

public class Database{

    /* TODO Faire en sorte que ça soit potable niveau connexion deco */

    private final String url;

    private Connection connection;
    private Statement statement;
    private boolean isConnected;

    public Database(String url) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.url = Objects.requireNonNull(url);
    }

    public void connect() throws SQLException {
        if(isConnected){
            close();
        }
        connection = DriverManager.getConnection(url);
        statement = connection.createStatement();
        isConnected = true;
    }

    public ResultSet query(String request) throws SQLException {
        return statement.executeQuery(request);
    }
    public void close() throws SQLException {
        statement.close();
        connection.close();
        isConnected = false;
    }
}
