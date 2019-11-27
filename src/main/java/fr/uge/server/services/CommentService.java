package fr.uge.server.services;

import fr.uge.common.services.ICommentService;
import fr.uge.database.Database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentService extends UnicastRemoteObject implements ICommentService {

    private final static Logger logger = Logger.getLogger(CommentService.class.getName());

    public CommentService() throws RemoteException {
    }

    @Override
    public void addComment(long idUser, long idProduct, int mark) throws RemoteException {
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            //regarder si il n'y a pas déja un commentaire idUser / idProduct
            checkIdOnTable(idUser, mark, stm);
            //ajouter dans la database
            String constructedRequest = "INSERT INTO comment (id_user, id_product, mark) VALUES(" + idUser + ", " + idProduct + ", " + mark + " )";
            stm.executeUpdate(constructedRequest);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void addComment(long idUser, long idProduct, int mark, String comment) throws RemoteException {
        if(comment == null){
            logger.log(Level.INFO, "Comment is null");
            return;
        }
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            //regarder si il n'y a pas déja un commentaire idUser / idProduct
            if(!checkIdOnTable(idUser, mark, stm)){
                logger.log(Level.INFO, "Invalid entry");
                return;
            }
            //ajouter dans la database
            String constructedRequest = "INSERT INTO comment (id_user, id_product, mark, description) VALUES(" + idUser + ", " + idProduct + ", " + mark + ", \"" + comment + "\")";
            stm.executeUpdate(constructedRequest);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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
}
