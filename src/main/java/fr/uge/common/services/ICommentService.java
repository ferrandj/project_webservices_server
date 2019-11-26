package fr.uge.common.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IComment extends Remote {
    /**
     * Permite to add a comment and a mark for a product
     * @throws RemoteException
     */
    void addComment(long idProduct) throws RemoteException;
}
