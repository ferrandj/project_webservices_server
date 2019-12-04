package fr.uge.server.services;

import fr.uge.common.objects.IUser;
import fr.uge.common.services.ICommentService;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentService extends UnicastRemoteObject implements ICommentService {

    private final static Logger logger = Logger.getLogger(CommentService.class.getName());
    private final ConcurrentHashMap<Long, Long> users;


    public CommentService(ConcurrentHashMap<Long, Long> users) throws RemoteException {
        this.users = users;
    }

    @Override
    public void addComment(IUser user, long idUser, long idProduct, int mark) throws RemoteException {
        if (isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si il n'y a pas déja un commentaire idUser / idProduct
                checkIdOnTable(idUser, mark, stm);
                //ajouter dans la database
                String constructedRequest = "INSERT INTO comment (id_user, id_product, username, mark) VALUES("
                        + idUser + ", " + idProduct + ", '" + user.getUsername() + "', " + mark + " )";
                stm.executeUpdate(constructedRequest);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void addComment(IUser user, long idUser, long idProduct, int mark, String comment) throws RemoteException {
        if (isUserAuthenticated(user)) {
            if (comment == null) {
                logger.log(Level.INFO, "Comment is null");
                return;
            }
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si il n'y a pas déja un commentaire idUser / idProduct
                if (!checkIdOnTable(idUser, mark, stm)) {
                    logger.log(Level.INFO, "Invalid entry");
                    return;
                }
                //ajouter dans la database
                String constructedRequest = "INSERT INTO comment (id_user, id_product, username, mark, description)"
                        + "VALUES(" + idUser + ", " + idProduct + ", '" + user.getUsername()
                        + "', " + mark + ", '" + comment + "')";
                stm.executeUpdate(constructedRequest);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public List<Map<String, String>> getComments(IUser user, long idProduct) throws RemoteException {
        ArrayList<Map<String, String>> comments = new ArrayList<>();
        if (isUserAuthenticated(user)) {
            try (Connection con = Database.getConnection();
                 Statement stm = con.createStatement()) {
                //regarder si il n'y a pas déja un commentaire idUser / idProduct
                String constructedRequest = "SELECT * FROM comment WHERE id_product == " + idProduct;
                ResultSet res = stm.executeQuery(constructedRequest);
                while (res.next()){
                    Map<String, String> row = new HashMap<>();
                    row.put("username", res.getString("username"));
                    row.put("mark", res.getString("mark"));
                    row.put("description", res.getString("description"));
                    comments.add(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    private boolean checkIdOnTable(long idUser, int mark, Statement stm) throws SQLException{
        if(!isOnTheDatabase("user", idUser, stm)){
            return false;
        }
        if(!isOnTheDatabase("product", idUser, stm)){
            return false;
        }
        if(mark < 0 || mark > 5){
            return false;
        }
        return true;
    }
    private boolean isOnTheDatabase(String table, long id, Statement stm) throws SQLException {
        String constructedRequest = "SELECT * FROM " + table + " WHERE id_" + table +" == " + id;
        ResultSet res = stm.executeQuery(constructedRequest);
        return res.next();
    }
    private boolean isUserAuthenticated(IUser user) throws RemoteException {
        if(user != null){
            Long secureKey = users.get(user.getIdUser());
            return secureKey != null && secureKey == user.getUniqueId();
        }
        return false;
    }
}
