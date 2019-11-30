package fr.uge.common.services;

import fr.uge.common.objects.IProduct;
import fr.uge.common.objects.IUser;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBorrowService extends Remote {
    /**
     * this function attribute the user to a product if possible, else put the user on waiting list
     * @return 1 if avaible | 0 if waintingList
     * @throws RemoteException remote exception
     */
    int borrowProduct(IUser user, long idUser, long idProduct) throws RemoteException;
    /**
     * this function permite to return a product borrowed by the user
     * @throws RemoteException remote exception
     */
    ICommentService returnProduct(IUser user, long isUser, long idBorrow) throws RemoteException;
    List<IProduct> getBorrowedProducts(IUser user, String name) throws RemoteException;
}
