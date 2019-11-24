package fr.uge.common.objects;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUser extends Remote {
    long getIdUser() throws RemoteException;
    String getUsername() throws RemoteException;
    long getType() throws RemoteException;
    long getBorrowNumber() throws RemoteException;
}
