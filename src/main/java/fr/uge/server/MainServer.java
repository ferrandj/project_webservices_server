package fr.uge.server;

import fr.uge.common.services.IConnectionService;
import fr.uge.server.services.ConnectionService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    private static final Logger logger = Logger.getLogger(MainServer.class.getName());

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            IConnectionService connectionService = new ConnectionService();
            Naming.rebind("rmi://localhost:1099/connectionService", connectionService);
        } catch (RemoteException | MalformedURLException e) {
            logger.log(Level.SEVERE, "Failure: " + e.getMessage());
        }
    }
}
