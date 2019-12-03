package fr.uge.server.services;

import fr.uge.common.objects.IBorrowable;
import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IUser;
import fr.uge.common.services.IBorrowService;
import fr.uge.common.services.ICommentService;
import fr.uge.common.services.IProductStorageService;
import fr.uge.database.Database;
import fr.uge.server.objects.Borrowable;
import fr.uge.server.objects.Product;
import fr.uge.server.objects.ProductType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BorrowService extends UnicastRemoteObject implements IBorrowService {

    private final static Logger logger = Logger.getLogger(BorrowService.class.getName());
    private final ConcurrentHashMap<Long, Long> users;

    public BorrowService(ConcurrentHashMap<Long, Long> users) throws RemoteException {
        this.users = users;
    }

    @Override
    public int borrowProduct(IUser user, long idUser, long idProduct, String name) throws RemoteException {
        if(isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si idUser existe, pareil pour idProduct
                if (!isOnTheDatabase("user", idUser, stm)) {
                    logger.log(Level.INFO, "This user does not exist");
                    return -1;
                }
                if (!isOnTheDatabase("product", idProduct, stm)) {
                    logger.log(Level.INFO, "This product does not exist");
                    return -1;
                }
                //regarder si pas déja de demande de ce duo user / product
                String constructedRequest = "SELECT * FROM borrow WHERE id_product == " + idProduct + " and id_user == " + idUser + " and state != 2";
                ResultSet res = stm.executeQuery(constructedRequest);
                if (res.next()) {
                    logger.log(Level.INFO, "You already ask for this product or you actually borrow it");
                    return -1;
                }
                //regarder si le produit est disponible
                constructedRequest = "SELECT * FROM borrow WHERE id_product == " + idProduct + " and state == 1";
                res = stm.executeQuery(constructedRequest);
                if (res.next()) {
                    // si indisponible -> waiting list
                    constructedRequest = "INSERT INTO borrow (id_user, id_product, name, state) VALUES(" + idUser + ", " + idProduct + ", '" + name + "', " + 0 + ")";
                    stm.executeUpdate(constructedRequest);
                    return 0;
                }
                //si disponible --> borrow
                constructedRequest = "INSERT INTO borrow (id_user, id_product, name, state, borrowing_date) VALUES(" + idUser + ", " + idProduct + ", '" + name + "', " + 1 + ", datetime('now','localtime'))";
                stm.executeUpdate(constructedRequest);
                //augmenter le nombre d'emprunt de l'user
                constructedRequest = "UPDATE user SET borrow_number = borrow_number + 1 WHERE id_user == " + idUser;
                stm.executeUpdate(constructedRequest);
                return 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;

    }
    @Override
    public void returnProduct(IUser user, long idUser, long idBorrow) throws RemoteException {
        if(isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si idUser existe, pareil pour idBorrow
                if (!isOnTheDatabase("user", idUser, stm)) {
                    logger.log(Level.INFO, "This user does not exist");
                }
                String constructedRequest = "SELECT id_user, id_product FROM borrow WHERE id_borrow == " + idBorrow;
                ResultSet res = stm.executeQuery(constructedRequest);
                if (!res.next()) {
                    logger.log(Level.INFO, "This borrow does not exist");
                }
                long idProduct = res.getLong("id_product");
                //regarder si idUser correspond bien a cet idBorrow
                if (Long.parseLong(res.getString("id_user")) != idUser) {
                    logger.log(Level.INFO, "This user dos not match with this borrow");
                }
                //update date de rendu + update state
                constructedRequest = "UPDATE borrow SET state = 2, returning_date = datetime('now','localtime') WHERE id_borrow == " + idBorrow;
                stm.executeUpdate(constructedRequest);
                //chercher le prochain locataire si il y a une waiting list
                findNextTenant(idProduct, stm);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public List<IBorrowable> getBorrowedProducts(IUser user, String name) throws RemoteException {
        ArrayList<IBorrowable> borrowedProducts = new ArrayList<>();
        if (isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                String constructedRequest = "SELECT * FROM borrow WHERE id_user == " + user.getIdUser() + " AND name LIKE '%" + name + "%'" ;
                ResultSet res = stm.executeQuery(constructedRequest);
                while (res.next()){
                    borrowedProducts.add(new Borrowable(res.getLong("id_borrow"), res.getLong("id_user"), res.getLong("id_product"), res.getString("name"), res.getLong("state"), res.getString("asking_date"), res.getString("borrowing_date"), res.getString("returning_date")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return borrowedProducts;
    }

    private boolean isOnTheDatabase(String table, long id, Statement stm) throws SQLException {
        String constructedRequest = "SELECT * FROM " + table + " WHERE id_" + table +" == " + id;
        ResultSet res = stm.executeQuery(constructedRequest);
        return res.next();
    }
    private void findNextTenant(long idProduct, Statement stm) throws SQLException {
        String constructedRequest = "SELECT id_borrow, id_user, type, borrow_number, asking_date FROM borrow INNER JOIN user USING(id_user) WHERE id_product == " + idProduct + " AND state == 0";
        ResultSet res = stm.executeQuery(constructedRequest);
        // Priorité 1.Prof 2.Elève | Nombre d'emprunt moins élevé | date de demande
        ArrayList<ArrayList> userList = new ArrayList<>();
        while(res.next()){
            ArrayList<Object> lst = new ArrayList<>();
            lst.add(res.getLong("id_borrow"));
            lst.add(res.getLong("id_user"));
            lst.add(res.getInt("type"));
            lst.add(res.getLong("borrow_number"));
            lst.add(res.getString("asking_date"));
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
        long idBorrow = (long)userList.get(0).get(0);
        long idNextTenant = (long)userList.get(0).get(1);
        constructedRequest = "UPDATE borrow SET state = 1, borrowing_date = datetime('now','localtime') WHERE id_user == " + idNextTenant + " AND id_borrow == " + idBorrow;
        stm.executeUpdate(constructedRequest);
        //add the tuples (id_user / id_borrow) on the notification table
        constructedRequest = "INSERT INTO notification (id_user, id_borrow) VALUES(" + idNextTenant + ", " + idBorrow + ")";
        stm.executeUpdate(constructedRequest);

    }
    private ArrayList<ArrayList> obtainFirstUserAsking(ArrayList<ArrayList> userList) {
        ArrayList oldestUser = userList.get(0);
        for(int i = 1; i < userList.size(); i++){
            String objectDate  = (String)userList.get(i).get(4);
            String oldestDate  = (String)oldestUser.get(4);
            System.out.println(objectDate + " " + oldestDate);
            if(objectDate.compareTo(oldestDate) < 0) {
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
            if(min > (long) user.get(3)){
                min = (long)user.get(3);
            }
        }
        ArrayList<ArrayList> newList = new ArrayList<>();
        for(ArrayList user : userList){
            if(min == (long) user.get(3)){
                newList.add(user);
            }
        }
        return newList;
    }
    private ArrayList<ArrayList> keepTeacherOrReturn(ArrayList<ArrayList> userList){
        ArrayList<ArrayList> teacherList = new ArrayList<>();
        for (ArrayList user : userList){
            if((int)user.get(2) == 0){
                teacherList.add(user);
            }
        }
        if (teacherList.size() == 0){
            return userList;
        }
        return teacherList;
    }
    private boolean isUserAuthenticated(IUser user) throws RemoteException {
        if(user != null){
            Long secureKey = users.get(user.getIdUser());
            return secureKey != null && secureKey == user.getUniqueId();
        }
        return false;
    }
}
