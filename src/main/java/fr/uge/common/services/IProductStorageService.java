package fr.uge.common.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IProductType;
import fr.uge.common.objects.IUser;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProductStorageService extends Remote {
    boolean addProduct(IUser user, long idProductType, String name, String image_url) throws RemoteException;
    List<IProduct> getProducts(IUser user, String request) throws RemoteException;
    IProduct getProduct(IUser user, long id_product) throws RemoteException;
    List<IProductType> getProductTypes(IUser user) throws RemoteException;
}
