package fr.uge.server.objects;

import fr.uge.common.objects.IBorrowable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Borrowable extends UnicastRemoteObject implements IBorrowable {

    private final long id_borrow;
    private final long id_user;
    private final long id_product;
    private final String name;
    private final long state;
    private final String asking_date;
    private final String borrowing_date;
    private final String returning_date;

    public Borrowable(long id_borrow, long id_user, long id_product, String name, long state, String asking_date, String borrowing_date, String returning_date) throws RemoteException {
        this.id_borrow = id_borrow;
        this.id_user = id_user;
        this.id_product = id_product;
        this.name = name;
        this.state = state;
        this.asking_date = asking_date;
        this.borrowing_date = borrowing_date;
        this.returning_date = returning_date;
    }

    @Override
    public String getFormattedBorrowable() throws RemoteException {
        StringBuilder stringBuilder = new StringBuilder("Produit: ");
        stringBuilder.append(name).append("   Status: ");
        if(state == 0){
            stringBuilder.append("En attente depuis le ").append(asking_date);
        }
        else if(state == 1){
            stringBuilder.append("Em cours d'emprunt depuis le ").append(borrowing_date);
        }
        else{
            stringBuilder.append("Rendu le ").append(returning_date);
        }
        return stringBuilder.toString();
    }
}
