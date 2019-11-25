package fr.uge.server.services;

import fr.uge.common.objects.IUser;
import fr.uge.common.services.IConnectionService;
import fr.uge.database.Database;
import fr.uge.server.objects.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class ConnectionService extends UnicastRemoteObject implements IConnectionService {

    private final HashSet<Long> users = new HashSet<>();

    public ConnectionService() throws RemoteException {
    }

    @Override
    public IUser login(String username, String password) throws RemoteException {
        IUser user = null;
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            String constructedRequest = "SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password + "'";
            ResultSet res = stm.executeQuery(constructedRequest);
            if(res.next()){
                long id_user = res.getLong("id_user");
                if(!users.contains(id_user)){
                    user = new User(res.getLong("id_user"), res.getString("username"), res.getLong("type"), res.getLong("borrow_number"));
                    users.add(id_user);
                }
            }
        } catch (SQLException e) {
            // TODO We have to do something
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean logout(IUser user) throws RemoteException {
        return users.remove(user.getIdUser());
    }
}
