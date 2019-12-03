package fr.uge.common.services;

import fr.uge.common.objects.IUser;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ICommentService extends Remote {
    /**
     * Permite to add a mark for a product
     * @throws RemoteException remote exception
     */
    void addComment(IUser user, long idUser, long idProduct, int mark) throws RemoteException;
    /**
     * Permite to add a mark and a comment for a product
     * @throws RemoteException remote exception
     */
    void addComment(IUser user, long idUser, long idProduct, int mark, String comment) throws RemoteException;
    List<Map<String, String>> getComments(IUser user, long idProduct) throws RemoteException;
}
