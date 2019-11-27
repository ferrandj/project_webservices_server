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

    /*private static void showTable(String tableName) {
        System.out.println("________________Debut " + tableName + "___________________");
        try (Connection con = Database.getConnection();
             Statement stm = con.createStatement()) {
            String constructedRequest = "SELECT * FROM " + tableName;
            ResultSet res = stm.executeQuery(constructedRequest);
            while (res.next()) {
                if (tableName.equals("user")) {
                    System.out.println(res.getLong("id_user") + " " + res.getString("username") + " " + res.getInt("type"));
                } else if (tableName.equals("borrow")) {
                    System.out.println(res.getLong("id_borrow") + " " + res.getLong("id_user") + " " + res.getLong("id_product") + " " + res.getInt("state") + " " + res.getString("asking_date") + " " + res.getString("borrowing_date") + " " + res.getString("returning_date"));
                } else if (tableName.equals("comment")) {
                    System.out.println(res.getLong("id_user") + " " + res.getLong("id_product") + " " + res.getInt("mark") + " " + res.getString("description") + " " + res.getString("comment_date"));
                } else if (tableName.equals("notification")) {
                    System.out.println(res.getLong("id_user") + " " + res.getLong("id_borrow"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("________________FIN__________________");

    }*/

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
