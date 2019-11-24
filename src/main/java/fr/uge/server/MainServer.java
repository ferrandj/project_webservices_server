package fr.uge.server;

import fr.uge.common.services.IProductStorageService;
import fr.uge.server.services.ProductStorageService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    private static final Logger logger = Logger.getLogger(MainServer.class.getName());

    public static void main(String[] args) throws SQLException {
        try {
            LocateRegistry.createRegistry(1099);
            IProductStorageService storageService = new ProductStorageService();
            Naming.rebind("rmi://localhost:1099/storageService", storageService);
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Failure: " + e.getMessage());
        }
    }
}
