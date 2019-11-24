package fr.uge.server.objects;

import fr.uge.common.objects.IUser;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class User extends UnicastRemoteObject implements IUser {

    private final long idUser;
    private final String username;
    private final long type;
    private final long borrowNumber;

    public User(long idUser, String username, long type, long borrowNumber) throws RemoteException {
        this.idUser = idUser;
        this.username = Objects.requireNonNull(username);
        this.type = type;
        this.borrowNumber = borrowNumber;
    }

    @Override
    public long getIdUser() throws RemoteException {
        return idUser;
    }
    @Override
    public String getUsername() throws RemoteException {
        return username;
    }
    @Override
    public long getType() throws RemoteException {
        return type;
    }
    @Override
    public long getBorrowNumber() throws RemoteException {
        return borrowNumber;
    }
}
