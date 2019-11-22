package fr.uge.common.objects;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProduct extends Remote {
    public long getIdProduct() throws RemoteException;
    public long getIdProductType() throws RemoteException;
    public String getName() throws RemoteException;
    public String getImageUrl() throws RemoteException;
}
