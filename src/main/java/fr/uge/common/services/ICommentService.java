package fr.uge.common.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICommentService extends Remote {
    /**
     * Permite to add a mark for a product
     * @throws RemoteException remote exception
     */
    void addComment(long idUser, long idProduct, int mark) throws RemoteException;
    /**
     * Permite to add a mark and a comment for a product
     * @throws RemoteException remote exception
     */
    void addComment(long idUser, long idProduct, int mark, String comment) throws RemoteException;
}
