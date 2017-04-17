package model.server.exceptions;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 * @author Ilya Ivanov
 */
public class NoSuitableOrder extends RemoteException {
    public NoSuitableOrder(String s) {
        super(s);
    }
}
