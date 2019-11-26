package fr.uge.server.services;

import fr.uge.common.services.INotifService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NotifService extends UnicastRemoteObject implements INotifService {
    public NotifService() throws RemoteException {
    }

    @Override
    public void notifBorrow() throws RemoteException {

    }
}
