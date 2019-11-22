package fr.uge.server;

import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    private static final Logger logger = Logger.getLogger(MainServer.class.getName());

    public static void main(String[] args) throws SQLException {
        try {
            LocateRegistry.createRegistry(1099);
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Failure: " + e.getMessage());
        }
    }
}
