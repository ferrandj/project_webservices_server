package fr.uge.server.services;

import fr.uge.common.services.IBorrowService;
import fr.uge.database.Database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;

public class BorrowService extends UnicastRemoteObject implements IBorrowService {

    public BorrowService() throws RemoteException {
    }

    @Override
    public int borrowProduct(long idUser, long idProduct) throws RemoteException {
        System.out.println("ENTER BORROWPRODUCT");
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            //regarder si idUser existe, pareil pour idProduct
            if(!isOnTheDatabase("user", idUser, stm)){
                throw new IllegalArgumentException("This user does not exist");
            }
            System.out.println("AFTER FIRST CHECK");
            if(!isOnTheDatabase("product", idProduct, stm)){
                throw new IllegalArgumentException("This product does not exist");
            }
            //regarder si pas déja de demande de ce duo user / product
            String constructedRequest = "SELECT * FROM borrow WHERE id_product == " + idProduct + " and id_user == " + idUser + " and state != 2";
            ResultSet res = stm.executeQuery(constructedRequest);
            if(res.next()){
                throw new IllegalStateException("you already ask for this product or you actually borrow it");
            }
            //regarder si le produit est disponible
            constructedRequest = "SELECT * FROM borrow WHERE id_product == " + idProduct + " and state == 1";
            res = stm.executeQuery(constructedRequest);
            if(res.next()){
                // si indisponible -> waiting list
                constructedRequest = "INSERT INTO borrow (id_user, id_product, state) VALUES(" + idUser + ", '" + idProduct + "', '" + 0 + "')";
                stm.executeUpdate(constructedRequest);
                return 0;
            }
            //si disponible --> borrow
            constructedRequest = "INSERT INTO borrow (id_user, id_product, state, borrowing_date) VALUES(" + idUser + ", '" + idProduct + "', '" + 1 + "', datetime('now','localtime'))";
            stm.executeUpdate(constructedRequest);
            //augmenter le nombre d'emprunt de l'user
            constructedRequest = "UPDATE user SET borrow_number = borrow_number + 1 WHERE id_user == " + idUser;
            stm.executeUpdate(constructedRequest);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    @Override
    public void returnProduct(long idUser, long idBorrow) throws RemoteException {
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            //regarder si idUser existe, pareil pour idBorrow
            if(!isOnTheDatabase("user", idUser, stm)){
                throw new IllegalArgumentException("This user does not exist");
            }
            String constructedRequest = "SELECT id_user, id_product FROM borrow WHERE id_borrow == " + idBorrow;
            ResultSet res = stm.executeQuery(constructedRequest);
            if(!res.next()){
                throw new IllegalArgumentException("This borrow does not exist");
            }
            long idProduct = res.getLong("id_product");
            //regarder si idUser correspond bien a cet idBorrow
            if(Long.parseLong(res.getString("id_user")) != idUser){
                throw new IllegalArgumentException("This user dos not match with this borrow");
            }
            //update date de rendu + update state
            constructedRequest = "UPDATE borrow SET state = 2, returning_date = datetime('now','localtime') WHERE id_borrow == " + idBorrow;
            stm.executeUpdate(constructedRequest);
            //chercher le prochain locataire si il y a une waiting list
            findNextTenant(idProduct, stm);
            //Todo NOTIFICATION A CALL ICI
            //Todo COMMENTAIRE A CALL ICI
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnTheDatabase(String table, long id, Statement stm) throws SQLException {
        String constructedRequest = "SELECT * FROM " + table + " WHERE id_" + table +" == " + id;
        ResultSet res = stm.executeQuery(constructedRequest);
        return res.next();
    }

    private void findNextTenant(long idProduct, Statement stm) throws SQLException {
        String constructedRequest = "SELECT id_user, type, borrow_number, borrowing_date FROM borrow INNER JOIN user USING(id_user) WHERE id_product == " + idProduct + " and state == 0";
        ResultSet res = stm.executeQuery(constructedRequest);
        // Priorité 1.Prof 2.Elève | Nombre d'emprunt moins élevé | date de demande
        ArrayList<ArrayList> userList = new ArrayList<>();
        while(res.next()){
            ArrayList<Object> lst = new ArrayList<>();
            lst.add(res.getLong("id_borrow"));
            lst.add(res.getInt("type"));
            lst.add(res.getLong("borrow_number"));
            lst.add(res.getDate("borrowing_date"));
            userList.add(lst);
        }
        if (userList.isEmpty()){
            return;
        }
        if(userList.size() > 1) {
            userList = keepTeacherOrReturn(userList);
            if(userList.size() > 1) {
                userList = keepLowerBorrow(userList);
                if(userList.size() > 1) {
                    userList = obtainFirstUserAsking(userList);
                }
            }
        }
        long idNextTenant = (long)userList.get(0).get(0);
        constructedRequest = "UPDATE borrow SET state = 2, returning_date = datetime('now','localtime') WHERE id_borrow == " + idNextTenant;
        stm.executeUpdate(constructedRequest);
    }

    private ArrayList<ArrayList> obtainFirstUserAsking(ArrayList<ArrayList> userList) {
        ArrayList oldestUser = userList.get(0);
        for(int i = 1; i < userList.size(); i++){
            Date objectDate  = (Date)userList.get(i).get(3);
            Date oldestDate  = (Date)oldestUser.get(3);
            if(objectDate.getTime() < oldestDate.getTime()){
                oldestUser = userList.get(i);
            }
        }
        ArrayList<ArrayList> finalList = new ArrayList<>();
        finalList.add(oldestUser);
        return finalList;
    }

    private ArrayList<ArrayList> keepLowerBorrow(ArrayList<ArrayList> userList) {
        long min = Long.MAX_VALUE;
        for(ArrayList user : userList){
            if((long)user.get(2) < min){
                min = (long)user.get(2);
            }
        }
        ArrayList<ArrayList> newList = new ArrayList<>();
        for(ArrayList user : userList){
            if((long)user.get(2) == min){
                newList.add(user);
            }
        }
        return newList;
    }

    private ArrayList<ArrayList> keepTeacherOrReturn(ArrayList<ArrayList> userList){
        ArrayList<ArrayList> teacherList = new ArrayList<>();
        for (ArrayList user : userList){
            if((int)user.get(1) == 0){
                teacherList.add(user);
            }
        }
        if (teacherList.size() == 0){
            return userList;
        }
        return teacherList;
    }
}
