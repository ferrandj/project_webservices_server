package fr.uge.server;

import fr.uge.common.services.IConnectionService;
import fr.uge.common.services.IProductStorageService;
import fr.uge.server.services.ConnectionService;
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
            IConnectionService connectionService = new ConnectionService();
            Naming.rebind("rmi://localhost:1099/storageService", storageService);
            Naming.rebind("rmi://localhost:1099/connectionService", connectionService);
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Failure: " + e.getMessage());
        }
    }
}
