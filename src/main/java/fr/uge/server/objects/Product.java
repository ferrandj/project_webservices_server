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
    private final long is_borrowed;

    public Product(long id_product, long id_product_type, String name, String image_url, long is_borrowed) throws RemoteException{
        this.id_product = id_product;
        this.id_product_type = id_product_type;
        this.name = Objects.requireNonNull(name);
        this.image_url = Objects.requireNonNull(image_url);
        this.is_borrowed = is_borrowed;
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
    @Override
    public String getFormattedProduct() throws RemoteException {
        String tmp1 = padRight("Produit: " + name);
        String tmp2;
        if(is_borrowed == 1){
            tmp2 = "Status: le produit est déjà emprunté.";
        }
        else{
            tmp2 = "Status: le produit est disponible";
        }
        tmp2 = padRight(tmp2);
        return tmp1 + tmp2;
    }
    @Override
    public boolean isBorrowed() throws RemoteException {
        return is_borrowed == 1L;
    }

    private String padRight(String s) {
        return String.format("%-" + 100 + "s", s);
    }
}
