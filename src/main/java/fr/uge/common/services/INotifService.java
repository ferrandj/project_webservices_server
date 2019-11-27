package fr.uge.common.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface INotifService extends Remote {
    /**
     * notif the user when the product is avaible
     * @throws RemoteException remote exception
     */
    List<String> notifBorrow(long idUser) throws RemoteException;
}
