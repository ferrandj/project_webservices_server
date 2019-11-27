package fr.uge.server.services;

import fr.uge.common.services.INotifService;
import fr.uge.database.Database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotifService extends UnicastRemoteObject implements INotifService {
    public NotifService() throws RemoteException {
    }

    @Override
    public List<String> notifBorrow(long idUser) throws RemoteException {
        ArrayList<String> lst = new ArrayList<>();
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            String constructedRequest = "SELECT borrowing_date, name FROM notification INNER JOIN borrow USING(id_borrow) INNER JOIN product ON borrow.id_product == product.id_product WHERE notification.id_user == " + idUser;
            ResultSet res = stm.executeQuery(constructedRequest);
            while (res.next()){
                String str = res.getString("borrowing_date") + ": Votre location pour " + res.getString("name") + " est désormais disponible.";
                lst.add(str);
                System.out.println(str);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return lst;
    }
    public void deleteNotif(long idUser){
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            String constructedRequest = "DELETE FROM notification WHERE id_user == " + idUser;
            stm.executeUpdate(constructedRequest);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
