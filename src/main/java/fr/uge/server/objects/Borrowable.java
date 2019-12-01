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
        String tmp1 = padRight("Produit: " + name);
        String tmp2;
        if(state == 0){
            tmp2 = "Status: En attente depuis le " + asking_date;
        }
        else if(state == 1){
            tmp2 = "Status: En cours d'emprunt depuis le " + borrowing_date;
        }
        else{
            tmp2 = "Status: Rendu le " + returning_date;
        }
        tmp2 = padRight(tmp2);
        return tmp1 + tmp2;
    }
    @Override
    public long getBorrowId() throws RemoteException {
        return id_borrow;
    }
    @Override
    public long getProductId() throws RemoteException {
        return id_product;
    }
    @Override
    public long getState() {
        return state;
    }

    private String padRight(String s) {
        return String.format("%-" + 100 + "s", s);
    }
}
