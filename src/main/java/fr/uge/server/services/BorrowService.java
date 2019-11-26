package fr.uge.server.services;

import fr.uge.common.services.IBorrowService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BorrowService extends UnicastRemoteObject implements IBorrowService {

    public BorrowService() throws RemoteException {
    }

    @Override
    public int borrowProduct(long idUser, long idProduct) throws RemoteException {
        //regarder si idUser existe, pareil pour idProduct
        //regarder si le produit est disponible
        //regarder si pas déja de demande de ce duo user / product
        //renvois 0 si waiting list  et 1 si borrow
        //ajouter la ligne bdd correspondante
        return 0;

    }

    @Override
    public void returnProduct(long idUser, long idBorrow) throws RemoteException {
        //regarder si idUser existe, pareil pour idBorrow
        //regarder si idUser correspond bien a cet idBorrow
        //update date de rendu + update state
        //chercher le prochain locataire si il y a une waiting list
        //si oui -> notifier dans ce cas + création new borrow
    }
}
