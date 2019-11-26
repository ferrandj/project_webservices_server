package fr.uge.server.services;

import fr.uge.common.services.ICommentService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CommentService extends UnicastRemoteObject implements ICommentService {
    public CommentService() throws RemoteException {
    }

    @Override
    public void addComment(long idUser, long idProduct, int mark) throws RemoteException {
        //
    }

    @Override
    public void addComment(long idUser, long idProduct, int mark, String comment) throws RemoteException {

    }
}
