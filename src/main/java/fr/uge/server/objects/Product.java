package fr.uge.server.objects;

import fr.uge.common.objects.IProduct;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class Product extends UnicastRemoteObject implements IProduct {

    private final long id_product;
    private final long id_product_type;
    private final String name;
    private final String image_url;

    public Product(long id_product, long id_product_type, String name, String image_url) throws RemoteException{
        this.id_product = id_product;
        this.id_product_type = id_product_type;
        this.name = Objects.requireNonNull(name);
        this.image_url = Objects.requireNonNull(image_url);
    }

    @Override
    public long getIdProduct() throws RemoteException {
        return id_product;
    }
    @Override
    public long getIdProductType() throws RemoteException {
        return id_product_type;
    }
    @Override
    public String getName() throws RemoteException {
        return name;
    }
    @Override
    public String getImageUrl() throws RemoteException {
        return image_url;
    }
}
