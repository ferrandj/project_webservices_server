package fr.uge.server.services;

import fr.uge.common.objects.IUser;
import fr.uge.common.services.IConnectionService;
import fr.uge.common.services.IProductStorageService;
import fr.uge.database.Database;
import fr.uge.server.objects.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionService extends UnicastRemoteObject implements IConnectionService {

    private final static Logger logger = Logger.getLogger(ConnectionService.class.getName());

    private final ConcurrentHashMap<Long, Long> users;

    public ConnectionService(ConcurrentHashMap<Long, Long> users) throws RemoteException {
        this.users = users;
    }

    @Override
    public IUser login(String username, String password) throws RemoteException {
        IUser user = null;
        if(validString(username) && validString(password)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                String constructedRequest = "SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password + "'";
                ResultSet res = stm.executeQuery(constructedRequest);
                if (res.next()) {
                    long id_user = res.getLong("id_user");
                    if (!users.containsKey(id_user)) {
                        Random secureLongGenerator = new Random();
                        long secureLong = secureLongGenerator.nextLong();
                        while(users.containsValue(secureLong)){
                            secureLong = secureLongGenerator.nextLong();
                        }
                        user = new User(res.getLong("id_user"), res.getString("username"), res.getLong("type"), res.getLong("borrow_number"), secureLong);
                        users.put(id_user, secureLong);
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.INFO, e.getMessage());
            }
        }
        return user;
    }
    @Override
    public boolean logout(IUser user) throws RemoteException {
        return users.remove(user.getIdUser(), user.getUniqueId());
    }

    private boolean validString(String str){
        return str != null && !str.contains("*") && !str.contains("%") && !str.contains("/") && !str.contains("\\");
    }
}
