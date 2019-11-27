package fr.uge.common.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IProductType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProductStorageService extends Remote {
    boolean addProduct(long idProductType, String name, String image_url) throws RemoteException;
    List<IProduct> getProducts(String request, long idProductType) throws RemoteException;
    List<IProductType> getProductTypes() throws RemoteException;
}
