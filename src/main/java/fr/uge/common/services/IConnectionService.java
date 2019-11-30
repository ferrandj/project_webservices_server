package fr.uge.common.services;

import fr.uge.common.objects.IUser;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnectionService extends Remote {
    IUser login(String username, String password) throws RemoteException;
    boolean logout(IUser user) throws RemoteException;
}
