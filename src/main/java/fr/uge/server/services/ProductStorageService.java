package fr.uge.server.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IProductType;
import fr.uge.common.objects.IUser;
import fr.uge.common.services.IProductStorageService;
import fr.uge.database.Database;
import fr.uge.server.objects.Product;
import fr.uge.server.objects.ProductType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductStorageService extends UnicastRemoteObject implements IProductStorageService {

    private final static Logger logger = Logger.getLogger(ProductStorageService.class.getName());
    private final ConcurrentHashMap<Long, Long> users;

    public ProductStorageService(ConcurrentHashMap<Long, Long> users) throws RemoteException {
        this.users = users;
    }

    @Override
    public boolean addProduct(IUser user, long idProductType, String name, String image_url) throws RemoteException{
        if(isUserAuthenticated(user)) {
            if (validId(idProductType) && validString(name)) {
                try (Connection con = Database.getConnection();
                     Statement stm = con.createStatement()) {
                    String constructedRequest = "INSERT INTO product (id_product_type, name, image_url) VALUES(" + idProductType + ", '" + name + "', '" + image_url + "')";
                    stm.executeUpdate(constructedRequest);
                } catch (SQLException e) {
                    logger.log(Level.INFO, e.getMessage());
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<IProduct> getProducts(IUser user, String requestedName) throws RemoteException {
        ArrayList<IProduct> lst = new ArrayList<>();
        if(isUserAuthenticated(user)){
            if(validString(requestedName)) {
                try (Connection con = Database.getConnection();
                     Statement stm = con.createStatement()) {
                    String constructedRequest = "SELECT * FROM product WHERE name LIKE \"%" + requestedName.toLowerCase() + "%\" AND id_product NOT IN (SELECT borrow.id_product FROM borrow WHERE (borrow.state == 1 OR borrow.state == 0) AND borrow.id_user == " + user.getIdUser() + ")";
                    ResultSet res = stm.executeQuery(constructedRequest);
                    while (res.next()) {
                        Product product = new Product(res.getLong("id_product"), res.getLong("id_product_type"), res.getString("name"), res.getString("image_url"), res.getLong("is_borrowed"));
                        lst.add(product);
                    }
                } catch (SQLException e) {
                    logger.log(Level.INFO, e.getMessage());
                }
            }
        }
        return lst;
    }
    @Override
    public IProduct getProduct(IUser user, long id_product) throws RemoteException {
        IProduct product = null;
        if(isUserAuthenticated(user)){
            if(validId(id_product)) {
                try (Connection con = Database.getConnection();
                     Statement stm = con.createStatement()) {
                    String constructedRequest = "SELECT * FROM product WHERE id_product = " + id_product;
                    ResultSet res = stm.executeQuery(constructedRequest);
                    if (res.next()) {
                        product = new Product(res.getLong("id_product"), res.getLong("id_product_type"), res.getString("name"), res.getString("image_url"), res.getLong("is_borrowed"));
                    }
                } catch (SQLException e) {
                    logger.log(Level.INFO, e.getMessage());
                }
            }
        }
        return product;
    }
    @Override
    public List<IProductType> getProductTypes(IUser user) throws RemoteException {
        ArrayList<IProductType> lst = new ArrayList<>();
        if(isUserAuthenticated(user)){
            try(Connection con = Database.getConnection();
                Statement stm = con.createStatement())
            {
                String constructedRequest = "SELECT * FROM product_type";
                ResultSet res = stm.executeQuery(constructedRequest);
                while(res.next()) {
                ProductType productType = new ProductType(res.getLong("id_product_type"), res.getString("name"));
                lst.add(productType);
            }
            } catch (SQLException e) {
                logger.log(Level.INFO, e.getMessage());
            }
        }
        return lst;
    }

    private boolean validId(long id){
        return id > 0L;
    }
    private boolean validString(String str){
        return !str.contains("*") && !str.contains("%") && !str.contains("/") && !str.contains("\\");
    }
    private boolean isUserAuthenticated(IUser user) throws RemoteException {
        if(user != null){
            Long secureKey = users.get(user.getIdUser());
            return secureKey != null && secureKey == user.getUniqueId();
        }
        return false;
    }
}
