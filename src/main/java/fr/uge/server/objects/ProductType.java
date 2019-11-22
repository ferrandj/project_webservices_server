package fr.uge.server.objects;

import fr.uge.common.objects.IProductType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class ProductType extends UnicastRemoteObject implements IProductType {

    private final long idProductType;
    private final String name;

    public ProductType(long idProductType, String name) throws RemoteException {
        this.idProductType = idProductType;
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public long getIdProductType() throws RemoteException {
        return idProductType;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
