package fr.uge.common.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBorrow extends Remote {
    /**
     * this function attribute the user to a product if possible, else put the user on waiting list
     * @return 1 if avaible | 0 if waintingList
     * @throws RemoteException remote exception
     */
    int borrowProduct(long idProduct) throws RemoteException;
    /**
     * this function permite to return a product borrowed by the user
     * @throws RemoteException remote exception
     */
    void returnProduct(long idProduct) throws RemoteException;
}
