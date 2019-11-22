package fr.uge.common.objects;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProductType extends Remote {
    long getIdProductType() throws RemoteException;
    String getName() throws RemoteException;
}
