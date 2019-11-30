package fr.uge.server.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IUser;
import fr.uge.common.services.IBorrowService;
import fr.uge.common.services.ICommentService;
import fr.uge.database.Database;
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

public class BorrowService extends UnicastRemoteObject implements IBorrowService {

    private final static Logger logger = Logger.getLogger(BorrowService.class.getName());
    private final ConcurrentHashMap<Long, Long> users;
    private final ICommentService commentService = new CommentService();

    public BorrowService(ConcurrentHashMap<Long, Long> users) throws RemoteException {
        this.users = users;
    }

    @Override
    public int borrowProduct(IUser user, long idUser, long idProduct) throws RemoteException {
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
        }
        return 0;

    }
    @Override
    public ICommentService returnProduct(IUser user, long idUser, long idBorrow) throws RemoteException {
        if(isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si idUser existe, pareil pour idBorrow
                if (!isOnTheDatabase("user", idUser, stm)) {
                    logger.log(Level.INFO, "This user does not exist");
                    return null;
                }
                String constructedRequest = "SELECT id_user, id_product FROM borrow WHERE id_borrow == " + idBorrow;
                ResultSet res = stm.executeQuery(constructedRequest);
                if (!res.next()) {
                    logger.log(Level.INFO, "This borrow does not exist");
                    return null;
                }
                long idProduct = res.getLong("id_product");
                //regarder si idUser correspond bien a cet idBorrow
                if (Long.parseLong(res.getString("id_user")) != idUser) {
                    logger.log(Level.INFO, "This user dos not match with this borrow");
                    return null;
                }
                //update date de rendu + update state
                constructedRequest = "UPDATE borrow SET state = 2, returning_date = datetime('now','localtime') WHERE id_borrow == " + idBorrow;
                stm.executeUpdate(constructedRequest);
                //chercher le prochain locataire si il y a une waiting list
                findNextTenant(idProduct, stm);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return commentService;
        }
        return null;
    }
    @Override
    public List<IProduct> getBorrowedProducts(IUser user, String name) throws RemoteException {
        ArrayList<IProduct> products = new ArrayList<>();
        if (isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                String constructedRequest = "SELECT * FROM borrow WHERE id_user == " + user.getIdUser();
                ResultSet res = stm.executeQuery(constructedRequest);
                while(res.next()) {
                    Product product = new Product(res.getLong("id_product"), res.getLong("id_product_type"), res.getString("name"), res.getString("image_url"));
                    products.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    private boolean isOnTheDatabase(String table, long id, Statement stm) throws SQLException {
        String constructedRequest = "SELECT * FROM " + table + " WHERE id_" + table +" == " + id;
        ResultSet res = stm.executeQuery(constructedRequest);
        return res.next();
    }
    private void findNextTenant(long idProduct, Statement stm) throws SQLException {
        String constructedRequest = "SELECT id_user, type, borrow_number, asking_date FROM borrow INNER JOIN user USING(id_user) WHERE id_product == " + idProduct + " and state == 0";
        ResultSet res = stm.executeQuery(constructedRequest);
        // Priorité 1.Prof 2.Elève | Nombre d'emprunt moins élevé | date de demande
        ArrayList<ArrayList> userList = new ArrayList<>();
        while(res.next()){
            ArrayList<Object> lst = new ArrayList<>();
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
        long idNextTenant = (long)userList.get(0).get(0);
        constructedRequest = "UPDATE borrow SET state = 1, borrowing_date = datetime('now','localtime') WHERE id_borrow == " + idNextTenant;
        stm.executeUpdate(constructedRequest);
        //add the tuples (id_user / id_borrow) on the notification table
        constructedRequest = "SELECT id_borrow FROM borrow where id_product == " + idProduct + " and id_user == " + idNextTenant;
        res = stm.executeQuery(constructedRequest);
        long newBorrow = res.getLong("id_borrow");
        constructedRequest = "INSERT INTO notification (id_user, id_borrow) VALUES(" + idNextTenant + ", " + newBorrow + ")";
        stm.executeUpdate(constructedRequest);

    }
    private ArrayList<ArrayList> obtainFirstUserAsking(ArrayList<ArrayList> userList) {
        ArrayList oldestUser = userList.get(0);
        for(int i = 1; i < userList.size(); i++){
            String objectDate  = (String)userList.get(i).get(3);
            String oldestDate  = (String)oldestUser.get(3);
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
            if(min > (long) user.get(2)){
                min = (long)user.get(2);
            }
        }
        ArrayList<ArrayList> newList = new ArrayList<>();
        for(ArrayList user : userList){
            if(min == (long) user.get(2)){
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
    private boolean isUserAuthenticated(IUser user) throws RemoteException {
        if(user != null){
            Long secureKey = users.get(user.getIdUser());
            return secureKey != null && secureKey == user.getUniqueId();
        }
        return false;
    }
}
