package fr.uge.server.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IProductType;
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

public class ProductStorageService extends UnicastRemoteObject implements IProductStorageService {


    public ProductStorageService() throws RemoteException {
    }

    @Override
    public void addProduct(long idProductType, String name, String image_url) throws RemoteException{
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            String constructedRequest = "INSERT INTO product (id_product_type, name, image_url) VALUES(" + idProductType + ", '" + name + "', '" + image_url + "')";
            stm.executeUpdate(constructedRequest);
        } catch (SQLException e) {
            // TODO We have to do something
            e.printStackTrace();
        }
    }
    @Override
    public List<IProduct> getProducts(String requestedName, long idProductType) throws RemoteException {
        ArrayList<IProduct> lst = new ArrayList<>();
        try(Connection con = Database.getConnection();
            Statement stm = con.createStatement())
        {
            String constructedRequest = "SELECT * FROM product WHERE name LIKE '%" + requestedName.toLowerCase() + "%' AND id_product_type = " + idProductType;
            ResultSet res = stm.executeQuery(constructedRequest);
            while(res.next()) {
                Product product = new Product(res.getLong("id_product"), res.getLong("id_product_type"), res.getString("name"), res.getString("image_url"));
                lst.add(product);
            }
        } catch (SQLException e) {
            // TODO We have to do something
            e.printStackTrace();
        }
        return lst;
    }
    @Override
    public List<IProductType> getProductTypes() throws RemoteException {
        ArrayList<IProductType> lst = new ArrayList<>();
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
            // TODO We have to do something
            e.printStackTrace();
        }
        return lst;
    }
}
