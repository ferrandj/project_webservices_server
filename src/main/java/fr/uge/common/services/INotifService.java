package fr.uge.common.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INotifService extends Remote {
    /**
     * notif the user when the product is avaible
     * @throws RemoteException remote exception
     */
    void notifBorrow() throws RemoteException;
}
