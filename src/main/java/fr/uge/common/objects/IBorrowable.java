package fr.uge.common.objects;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBorrowable extends Remote {
    String getFormattedBorrowable() throws RemoteException;
}
