package fr.uge.common.objects;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBorrowable extends Remote {
    String getFormattedBorrowable() throws RemoteException;
    long getBorrowId() throws RemoteException;
    long getProductId() throws RemoteException;
    long getState() throws RemoteException;
}
